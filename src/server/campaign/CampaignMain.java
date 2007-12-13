/*
 * MekWars - Copyright (C) 2004 
 * 
 * Derived from MegaMekNET (http://www.sourceforge.net/projects/megameknet)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 */

package server.campaign;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import megamek.MegaMek;
import megamek.client.Client;
import megamek.common.CriticalSlot;
import megamek.common.Entity;
import megamek.common.Mech;
import megamek.common.Mounted;
import megamek.common.WeaponType;

import server.MWServ;
import server.campaign.commands.*;
import server.campaign.commands.admin.*;
import server.campaign.commands.mod.*;
import server.campaign.commands.helpers.*;
import server.campaign.market2.Market2;
import server.campaign.market2.PartsMarket;
import server.campaign.mercenaries.MercHouse;
import server.campaign.operations.Operation;
import server.campaign.operations.OperationManager;
import server.campaign.operations.ShortOperation;
import server.campaign.pilot.SPilot;
import server.campaign.pilot.skills.*;
import server.campaign.util.HouseRankingHelpContainer;
import server.campaign.util.MechStatistics;
import server.campaign.util.Statistics;
import server.campaign.util.XMLFactionDataParser;
import server.campaign.util.XMLPlanetDataParser;
import server.campaign.util.XMLTerrainDataParser;
import server.campaign.votes.VoteManager;
import server.dataProvider.Server;
import server.mwcyclopscomm.MWCyclopsComm;
import server.util.AutomaticBackup;
import server.util.MWPasswd;
import server.util.RepairTrackingThread;
import server.mwmysql.mysqlHandler;

import common.CampaignData;
import common.Equipment;
import common.House;
import common.Influences;
import common.Planet;
import common.campaign.pilot.skills.PilotSkill;
import common.util.UnitUtils;

// TODO: remove serializable? remanant of a bygone era?
public final class CampaignMain implements Serializable {

	private static final long serialVersionUID = -8671163467590633378L;

	/**
	 * I realized, that almost every class needs access to the current global
	 * campaign state. So I decided (after consultation with McWizard) to make
	 * this back reference obsolete by introducing a public static member
	 * (Java's pardon to a global variable). Although this reduces code size,
	 * complexity of code and memory footprint, this is still a HACK! Java
	 * wasn't invented to step back to the old days of global variables. Object
	 * oriented coding should try to minimize cross references..
	 * 
	 * But someday you gotta do what you gotta do..... Imi.
	 */
	public static CampaignMain cm;

	public static DefaultServerOptions dso;

	private MWServ myServer;

	private Client megaMekClient = new Client("MWServer", "None", 0);

	private Server dataProviderServer;

	private MWCyclopsComm mwcc = null;

	private CampaignData data = new CampaignData();

	private Properties config = new Properties();

	//private ConcurrentHashMap<String, SPlayer> savePlayers = new ConcurrentHashMap<String, SPlayer>();

	private Hashtable<String, Command> Commands = new Hashtable<String, Command>();

	private Hashtable<String, MechStatistics> MechStats = new Hashtable<String, MechStatistics>();

	private Hashtable<String, String> omniVariantMods = new Hashtable<String, String>();

	private Hashtable<Integer, SPilotSkill> pilotSkills = new Hashtable<Integer, SPilotSkill>();

	private Hashtable<String, Equipment> blackMarketEquipmentCostTable = new Hashtable<String, Equipment>();

	private int gamesCompleted;// used by Tracker

	private int currentUnitID = 1;

	private int currentPilotID = 1;

	private TickThread TThread;

	private SliceThread SThread;

	private ImmunityThread IThread;

	private RepairTrackingThread RTT;

	private AutomaticBackup aub = new AutomaticBackup(System
			.currentTimeMillis());

	private Market2 market;

	private PartsMarket partsmarket;

	private VoteManager voteManager;

	private OperationManager opsManager;

	private Vector unresolvedContracts = new Vector(1,1);

	private UnitCosts unitCostLists = null;

	private TreeMap<String, String> NewsFeed = new TreeMap<String, String>();

	private boolean isArchiving = false;

	private Random r = new Random(System.currentTimeMillis());

	public mysqlHandler MySQL = null;
	
	private boolean validBBVersion = true;

	/**
	 * This is a hash collection of all the players that have yet to log into their houses
	 * This catch all is to keep from having to load the player file over and over again.
	 * Once the player has been logged in they are removed from this hash and added to the 
	 * houses memory.
	 */
	private Hashtable<String, SPlayer> lostSouls = new Hashtable<String, SPlayer>();

	// CONSTRUCTOR
	public CampaignMain(MWServ serv) {

		cm = this;
		myServer = serv;
		dso = new DefaultServerOptions();
		dso.createDefaults();

		// make sure vital folders exist
		File f = new File("./campaign/");
		if (!f.exists())
			f.mkdir();
		f = new File("./campaign/players/");
		if (!f.exists())
			f.mkdir();

		// Try to read the config file
		try {
			config.putAll(dso.getServerDefaults());// load all of the defaults
													// into the config file
													// before you load in the
													// campaign stuff
			config.load(new FileInputStream(this.myServer
					.getConfigParam("CAMPAIGNCONFIG")));
		} catch (Exception ex) {
			MWServ.mwlog.errLog("Problems with loading campaign config");
			MWServ.mwlog.errLog(ex);
			dso.createConfig();
			try {
				config.load(new FileInputStream(this.myServer
						.getConfigParam("CAMPAIGNCONFIG")));
			} catch (Exception ex1) {
				MWServ.mwlog
						.errLog("Problems with loading campaing config from defaults");
				MWServ.mwlog.errLog(ex1);
				System.exit(1);
			}
		}

		if (!getConfig("AllowedMegaMekVersion").equals("-1"))
			getConfig().setProperty("AllowedMegaMekVersion", MegaMek.VERSION);

		dso.createConfig(); // save the cofig file so any missed defaults are
							// added

		/*
		 * Create the auction environment/market. Notice that the new market
		 * implementation does not save a .dat file. While saving the status was
		 * a nice idea, it was creating dupes and NPEs after crashes.
		 */

		cm.isUsingMySQL();
//		if(cm.isSynchingBB())
//			cm.MySQL.

		market = new Market2();
		partsmarket = new PartsMarket();

		initializePilotSkills();
		// data.clearHouses();

		// Load & Init Data
		data = new CampaignData();

		// load megamek gameoptions;
		MWServ.mwlog.infoLog("Loading MegaMek Game Options");
		cm.megaMekClient.game.getOptions().loadOptions();

		// Parse Terrain
		// XMLTerrainDataParser tParse =
		new XMLTerrainDataParser("./data/terrain.xml");

		cm.loadTopUnitID();
		gamesCompleted = 0;

		// Read the data from the SHouse Data File
		loadFactionData();
		loadPlanetData();

		try {
			File configFile = new File("./campaign/banammo.dat");
			FileInputStream fis = new FileInputStream(configFile);
			BufferedReader dis = new BufferedReader(new InputStreamReader(fis));
			while (dis.ready()) {
				String line = dis.readLine();
				loadBanAmmo(line);
			}
			dis.close();
			fis.close();
		} catch (FileNotFoundException fne) {
			MWServ.mwlog.mainLog("No banned ammo data found.");
		} catch (Exception ex) {
			MWServ.mwlog.errLog("Problems reading banned ammo data.");
		}

		try {
			File configFile = new File("./campaign/ammocosts.dat");
			FileInputStream fis = new FileInputStream(configFile);
			BufferedReader dis = new BufferedReader(new InputStreamReader(fis));
			while (dis.ready()) {
				String line = dis.readLine();
				loadAmmoCosts(line);
			}
			dis.close();
			fis.close();
		} catch (FileNotFoundException fne) {
			MWServ.mwlog.mainLog("No ammo cost data found creating default");

			for (long ammo : cm.getData().getMunitionsByNumber().keySet()) {
				cm.getData().getAmmoCost().put(ammo, 9999);
			}
			saveAmmoCosts();
		} catch (Exception ex) {
			MWServ.mwlog.errLog("Problems reading ammo cost data");
		}

		// misc loads.
		cm.loadBannedTargetingSystems();
		cm.loadOmniVariantMods();
		cm.loadBlackMarketSettings();

		// create command hashs
		this.init();

		if (Boolean.parseBoolean(cm.getConfig("UseCalculatedCosts"))) {
			unitCostLists = new UnitCosts();
			unitCostLists.loadUnitCosts();
			// MWServ.mwlog.errLog(unitCostLists.displayUnitCostsLists());
		}

		// Load the Mech-Statistics
		if(CampaignMain.cm.isUsingMySQL()) {
			loadMechStatsFromDB();
		} else {
			try {
				File configFile = new File("./campaign/mechstat.dat");
				FileInputStream fis = new FileInputStream(configFile);
				BufferedReader dis = new BufferedReader(new InputStreamReader(fis));
				while (dis.ready()) {
					String line = dis.readLine();
					MechStatistics m = new MechStatistics(line);
					MechStats.put(m.getMechFileName(), m);
				}
				dis.close();
				fis.close();
			} catch (Exception ex) {
				MWServ.mwlog.errLog("Problems reading unit statistics data");
				MWServ.mwlog.errLog(ex);
				MWServ.mwlog.mainLog("No Mech Statistic Data found");
			}
		}
		if (Boolean.parseBoolean(getConfig("HTMLOUTPUT")))
			Statistics.doRanking();

		// Start a VoteManager.
		voteManager = new VoteManager(this);

		/*
		 * start an OperationManager. The manager loads all ops files and
		 * creates necessary instances of Validators, Resolvers and other helper
		 * objects as part of its construction.
		 */
		this.createNewOpsManager();

		/*
		 * Load all players in ./campaign/players and create SmallPlayers. This
		 * makes /c lastonline returns accurate, and allows the creation of two
		 * different rankings - one for people active within the last week, and
		 * another which lists all players.
		 * 
		 * Force a save cycle after the load in order to null the offline
		 * players.
		 */
		// int pFilesLoaded = 0;
		// File playersDir = new File("./campaign/players/");
		// File[] players = playersDir.listFiles();
		// for (File currF : players) {
		//	
		// //load pfile directly and determine player's house
		// SPlayer currP = this.loadPlayerFile(currF.getName(), true);
		// SHouse currH = currP.getMyHouse();
		//	
		// //add small player to the house hash
		// SmallPlayer smallp = new SmallPlayer(currP.getExperience(),
		// currP.getLastOnline(), currP.getRating(), currP.getName(),
		// currP.getFluffText(), currH);
		// currH.getSmallPlayers().put(currP.getName().toLowerCase(), smallp);
		//	
		// //explicity null the player, and periodically GC
		// currP = null;
		// if (pFilesLoaded >= 100) {
		// System.gc();
		// pFilesLoaded = 0;
		// } else {
		// pFilesLoaded++;
		// }
		//	
		// try {
		// Thread.sleep(50);
		// } catch (InterruptedException e) {
		// Thread.yield();
		// }
		// }
		// create & start a data provider
		int dataport = -1;
		try {
			dataport = Integer.parseInt(myServer.getConfigParam("DATAPORT"));
		} catch (NumberFormatException e) {
			MWServ.mwlog
					.errLog("Non-number given as dataport. Defaulting to 4867.");
			MWServ.mwlog.errLog(e);
			dataport = 4867;
		} finally {
			dataProviderServer = new Server(data, dataport, myServer
					.getConfigParam("SERVERIP"));
			Thread t = new Thread(dataProviderServer);
			t.start();
		}

		// Start Cyclops
		if (isUsingCyclops()) {
			getMWCC().skillWriteFromList(pilotSkills);
			getMWCC().houseWriteFromList(getData().getAllHouses());
			getMWCC().planetWriteFromList(getData().getAllPlanets());
		}

		// start tick, slice and immunity threads
		TThread = new TickThread(this, Integer.parseInt(getConfig("TickTime")));
		TThread.start();
		SThread = new SliceThread(this, Integer
				.parseInt(getConfig("SliceTime")));
		SThread.start();// it slices, it dices, it chops!
		IThread = new ImmunityThread();
		IThread.start();

		// start Advanced Repair, if enabled
		isUsingAdvanceRepair();

		// finally, announce restart in news feed.
		this.addToNewsFeed("MekWars Server Started!");
	}

	public void saveData() {
		try {
			data.saveData(new File("campaign"));
			/*
			 * MMNetXStream xml = new MMNetXStream(new DomDriver()); for
			 * (Iterator i = data.getAllHouses().iterator(); i.hasNext();) {
			 * SHouse h = (SHouse) i.next(); xml.toXML(h.getMembers(), new
			 * FileWriter("./campaign/members"+h.getName()+".xml")); }
			 */
		} catch (IOException e) {
			MWServ.mwlog.errLog(e);
		}
	}

	/**
	 * Saves the current campaign state to a file system.
	 */
	public void toFile() {

		try {

			// wait for the backup to finsh before you start saving files.
			while (cm.isArchiving())
				Thread.sleep(125);

			saveFactionData();
			savePlanetData();

			// Save omni variant mods
			cm.saveOmniVariantMods();

			// Save Mech-Stats
			FileOutputStream out = new FileOutputStream(
			"./campaign/mechstat.dat");
			PrintStream p = new PrintStream(out);
			if(CampaignMain.cm.isUsingMySQL()) {
				for (MechStatistics currStats : MechStats.values())
					currStats.toDB();
			} else {
				for (MechStatistics currStats : MechStats.values())
					p.println(currStats.toString());
				p.close();
				out.close();				
			}


			try {
				// Save the Readable Mechstats
				out = new FileOutputStream(getConfig("MechstatPath"));
				p = new PrintStream(out);
				p
						.println("<html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"format.css\"><style type=\"text/css\"></style></head><body><font face=\"Verdana, Arial, Helvetica, sans-serif\">");
				for (int i = 0; i <= 3; i++) {
					p.println(Statistics.doGetMechStats(i));
					p.println("<br>");
				}
				p.println("</font></body></style></html>");
				p.close();
				out.close();
			} catch (FileNotFoundException efnf) {
				// ignore
			}

			MWServ.mwlog.mainLog("STATUS SAVED");

		} catch (Exception ex) {
			MWServ.mwlog.errLog("Problems saving configuration to file");
			MWServ.mwlog.errLog(ex);
		}
	}

	public boolean isLoggedIn(String Username) {

		// always treat deds as logged in
		if (Username.startsWith("[Dedicated]"))
			return true;

		/*
		 * search all houses, all states, for user with this name. the hash
		 * searches are O(1), which means this is actually much faster than the
		 * old MMNET way, which was to try a .equals() on every player's name.
		 */
		String lowerName = Username.toLowerCase();
		for (House vh : data.getAllHouses()) {
			SHouse h = (SHouse) vh;
			if (h.getReservePlayers().containsKey(lowerName))
				return true;
			if (h.getActivePlayers().containsKey(lowerName))
				return true;
			if (h.getFightingPlayers().containsKey(lowerName))
				return true;
		}

		// we couldnt find the player. return false.
		return false;
	}

	public boolean getBooleanConfig(String key) {
		try {
			return Boolean.parseBoolean(cm.getConfig(key));
		} catch (Exception ex) {
			return false;
		}
	}

	public int getIntegerConfig(String key) {
		try {
			return Integer.parseInt(cm.getConfig(key));
		} catch (Exception ex) {
			return -1;
		}
	}

	public double getDoubleConfig(String key) {
		try {
			return Double.parseDouble(cm.getConfig(key));
		} catch (Exception ex) {
			return -1;
		}
	}

	public float getFloatConfig(String key) {
		try {
			return Float.parseFloat(cm.getConfig(key));
		} catch (Exception ex) {
			return -1;
		}
	}

	public String getConfig(String key) {

		if (config.getProperty(key) == null) {
			if (dso.getServerDefaults().getProperty(key) == null) {
				MWServ.mwlog.mainLog("You're missing the config variable: "
						+ key + " in campaignconfig!");
				MWServ.mwlog.errLog("You're missing the config variable: "
						+ key + " in campaignconfig! returning -1");
				return "-1";
			}
			// else
			return dso.getServerDefaults().getProperty(key).trim();
		}
		return config.getProperty(key).trim();
	}

	/**
	 * Method that allows other classes to access the opsManager instance via
	 * the static CampaignMain.
	 */
	public OperationManager getOpsManager() {
		return opsManager;
	}

	public void createNewOpsManager() {
		opsManager = new OperationManager();
	}

	public void fromUser(String text, String Username) {

		/*
		 * Only a few commands should be accepted from a logged out player.
		 * Unless the command is enroll, login, or register, return without
		 * further processing.
		 * 
		 * Register won't succeed unless player has a campaign account.
		 */
		if (!isLoggedIn(Username)
				&& (text.toUpperCase().indexOf("ENROLL") == -1)
				&& (text.toUpperCase().indexOf("LOGIN") == -1)
				&& (text.toUpperCase().indexOf("REGISTER") == -1)
				&& (text.toUpperCase().indexOf("GETSERVERCONFIGS") == -1)
				&& (text.toUpperCase().indexOf("SETCLIENTVERSION") == -1)
				&& (text.toUpperCase().indexOf("GETSAVEDMAIL") == -1)) {
			toUser("You are not logged in!", Username, true);
			return;
		}

		text = text.substring(2);
		// Date d = new Date(System.currentTimeMillis());
		// MWServ.mwlog.mainLog(d + ":" + "Command from User " + Username + ": "
		// + text);
		// MWServ.mwlog.cmdLog(Username + ": " + text);

		StringTokenizer ST = new StringTokenizer(text, "#");
		if (ST.hasMoreElements()) {

			// check command type
			String task = ((String) ST.nextElement()).toUpperCase();

			// idle checker omit pong command
			if (!task.equals("PONG")) {
				try {
					this.getPlayer(Username).setLastTimeCommandSent(
							System.currentTimeMillis());
				} catch (Exception ex) {
					if (!Username.startsWith("[Dedicated]"))// deds send lots of
															// commands
						MWServ.mwlog
								.errLog("Command received from a null player ("
										+ Username + ")?");
				}
			}

			// New Method (much cleaner)
			if (Commands.get(task) != null) {

				// log non-chat commands
				if (task.equals("MAIL") || task.equals("HOUSEMAIL")
						|| task.equals("HM") || task.equals("MODERATORMAIL")
						|| task.equals("MM") || task.equals("INCHARACTER")
						|| task.equals("IC")) {
					// do nothing
				} else {
					MWServ.mwlog.cmdLog(Username + ": " + text);
				}

				Command c = Commands.get(task);
				try {
					c.process(ST, Username);
				}catch(Exception ex) {
					MWServ.mwlog.errLog(ex);
					CampaignMain.cm.toUser("AM:Invalid Syntax: /"+task+" "+c.getSyntax(), Username);
				}
				return;
			}// if the text is a command

		}// end while(more elements)
	}// end fromUser

	public SPlanet getPlanetFromPartialString(String PlanetName, String Username) {

		// store matches so we can tell player if there's more than one
		int numMatches = 0;
		SPlanet theMatch = null;

		for (Planet currP : data.getAllPlanets()) {
			SPlanet p = (SPlanet) currP;

			// exact match
			if (p.getName().equals(PlanetName))
				return p;

			// store all matches
			if (p.getName().startsWith(PlanetName)) {
				theMatch = p;
				numMatches++;
			}
		}

		// too many matches
		if (numMatches > 1) {
			if (Username != null)
				toUser("\"" + PlanetName + "\" is not unique [" + numMatches
						+ " matches]. Please be more specific.", Username);
			return null;
		}

		if (numMatches == 0) {
			if (Username != null)
				toUser("Couldn't find a planet whose name begins with \""
						+ PlanetName + "\". Try again.", Username, true);
			return null;
		}

		// only one match! send it back.
		return theMatch;
	}

	public void doSendHouseMail(SHouse h, String Username, String text) {

		// send the text to all logged in players
		text = "(Housemail)" + Username + ":" + text;
		this.doSendToAllOnlinePlayers(h, text, true);

		// then add it to the faction's log
		MWServ.mwlog.factionLog(text.substring(11), h.getName());
	}

	/**
	 * Loop through all online players (all houses, all three duty modes) and
	 * send mail to those players who are mods.
	 */
	public void doSendModMail(String Username, String text) {

		int sendCommandLevel = 0;
		int commandLevel = CampaignMain.cm.getServerCommands().get("MM")
				.getExecutionLevel();
		int userLevel = 0;
		try {
			if (Username.equalsIgnoreCase("NOTE")) {
				sendCommandLevel = CampaignMain.cm.getServer().getUserLevel(
						text.substring(0, text.indexOf(" ")).trim());
			}
		} catch (Exception ex) {
			MWServ.mwlog.errLog(ex);
		}

		// Note it to the logs
		MWServ.mwlog.modLog(Username + ": " + text);
		text = "(Moderator Mail) " + Username + ": " + text;
		for (House vh : data.getAllHouses()) {
			SHouse h = (SHouse) vh;

			for (String currName : h.getReservePlayers().keySet()) {
				userLevel = CampaignMain.cm.getServer().getUserLevel(currName);
				if (userLevel >= commandLevel && userLevel >= sendCommandLevel)
					this.toUser(text, currName, true);
			}
			for (String currName : h.getActivePlayers().keySet()) {
				userLevel = CampaignMain.cm.getServer().getUserLevel(currName);
				if (userLevel >= commandLevel && userLevel >= sendCommandLevel)
					this.toUser(text, currName, true);
			}
			for (String currName : h.getFightingPlayers().keySet()) {
				userLevel = CampaignMain.cm.getServer().getUserLevel(currName);
				if (userLevel >= commandLevel && userLevel >= sendCommandLevel)
					this.toUser(text, currName, true);
			}
		}
	}

	/**
	 * After an error, loop through all online players and send text of the
	 * error to anyone who has modmail access.
	 */
	public void doSendErrLog(String text) {
		text = "(Error Log): " + text;
		for (House vh : data.getAllHouses()) {
			SHouse h = (SHouse) vh;

			for (String currName : h.getReservePlayers().keySet()) {
				Command command = CampaignMain.cm.getServerCommands().get("MM");
				if (CampaignMain.cm.getServer().getUserLevel(currName) >= command
						.getExecutionLevel())
					this.toUser(text, currName, true);
			}
			for (String currName : h.getActivePlayers().keySet()) {
				Command command = CampaignMain.cm.getServerCommands().get("MM");
				if (CampaignMain.cm.getServer().getUserLevel(currName) >= command
						.getExecutionLevel())
					this.toUser(text, currName, true);
			}
			for (String currName : h.getFightingPlayers().keySet()) {
				Command command = CampaignMain.cm.getServerCommands().get("MM");
				if (CampaignMain.cm.getServer().getUserLevel(currName) >= command
						.getExecutionLevel())
					this.toUser(text, currName, true);
			}

		}
	}

	/**
	 * @return Returns the mechStats.
	 */
	public Hashtable<String, MechStatistics> getMechStats() {
		return MechStats;
	}

	public void doProcessAutomaticReport(String s, String Username) {

		/*
		 * Format should be: Winner#DE#...Unit...#GY#...Units...#AL#...Units...
		 */

		/*
		 * return if the Username isn't listed if (s.indexOf(Username) == -1)
		 * return;
		 */

		// Now adays deds and Hosts actually report the game not the players.
		// So we need to check the winner if the winner is NULL due to a DRAW
		// Then check the name of the first player in the report string which
		// is the second element in a * delimited string
		// -Torren
		StringTokenizer report = new StringTokenizer(s, "#");
		SPlayer reporter = this.getPlayer(report.nextToken());

		if (reporter == null) {
			StringTokenizer report2 = new StringTokenizer(s, "*");

			// keep parsing until we find a players name!
			while (report2.hasMoreTokens()) {
				reporter = this.getPlayer(report2.nextToken());
				if (reporter != null)
					break;
			}
		}

		if (reporter == null)
			return;

		/*
		 * If the player isn't in any ShortOperations, he obviously has no
		 * standing to report. Tasks code used to sort winners and losers at
		 * this point, but we handle that in the ShortResovler.
		 */
		ShortOperation so = this.getOpsManager().getShortOpForPlayer(reporter);
		if (so == null)
			return;

		if (so.hasPlayer(reporter)) {
			Operation o = this.getOpsManager().getOperation(so.getName());
			this.getOpsManager().resolveShortAttack(o, so, s);
			return;
		}

	}// end doProcessAutomaticReport

	/**
	 * Method which pre-processes auto-disconnection info updates. Clients
	 * connected to a host send these updates when a unit is removed from play -
	 * this does not necessarily mean the unit is dead. It could have fled or
	 * been pushed from the field, etc.
	 * 
	 * ClientThread weeds out observers client side.
	 */
	public void addInProgressUpdate(String s, String Username) {

		// Return if user isn't an SPlayer.
		SPlayer reporter = this.getPlayer(Username);
		if (reporter == null)
			return;

		// If the reporting player isnt in a game, toss it.
		ShortOperation so = this.getOpsManager().getShortOpForPlayer(reporter);
		if (so == null)
			return;

		// If the short operation has more than two players
		if (so.getAllPlayerNames().size() > 2)
			return;

		// now that we have a game for the player, pass the destruction
		// string along to the short operation for handling.
		so.addInProgressUpdate(s);
	}

	public CampaignData getData() {
		return data;
	}

	public Vector<MercHouse> getMercHouses() {
		Vector<MercHouse> result = new Vector<MercHouse>(1,1);
		for (House currH : data.getAllHouses()) {
			SHouse sh = (SHouse) currH;
			if (sh.isMercHouse())
				result.add((MercHouse) currH);
		}
		result.trimToSize();
		return result;
	}

	/**
	 * Login a player to the server. Called by login, enroll command and (most
	 * commonly) SignOn.
	 * 
	 * If we find that the player is already in a faction, leave things as they
	 * are. If the player is not present in a house status hashtable, use
	 * this.getPlayer() to check the save queue and, if necessary, read the
	 * player in from text.
	 * 
	 * Any player who logs in should be put into the Reserve list. If he is
	 * reconnecting, the SignOn command will pass him through a reconnection
	 * check and clean up the various Operations threads, etc.
	 * 
	 * Players with no account (null this.getPlayer()) are also handled in
	 * SignOn, but we need to check there here as well in case the player
	 * ignores the SignOn click-through and attempts to log in anyway.
	 */
	public void doLoginPlayer(String Username) {

		// Loop through the houses and make sure he's not already logged in
		for (House vh : data.getAllHouses()) {
			SHouse currH = (SHouse) vh;
			if (currH.isLoggedIntoFaction(Username)) {
				toUser("You are already logged in to "
						+ currH.getColoredNameAsLink() + ".", Username, true);
				return;
			}
		}

		/*
		 * He's not in a house. lets look in the save queue and pfiles. If the
		 * getPlayer is null, extend an invitation to enroll (same as in
		 * SignOn.java, for uniformity).
		 */
		SPlayer toLogin = this.getPlayer(Username);
		
		if (toLogin == null) {
			this
					.toUser(
							"<font color=\"navy\"><br>---<br>"
									+ "It appears that you haven't signed up for this server's "
									+ "campaign.<br><a href=\"MEKWARS/c enroll\">Click here to get "
									+ "started.</a><br>---<br></font>",
							Username, true);
			return;
		}

		/*
		 * Now that we have a player who needs to be placed in a house. The
		 * player holds a faction name in his .dat file, which is used to
		 * bootstrap a link to the SHouse into SPlayer at load time. We may
		 * assume that this data is valid (if not, we have much deeper problems
		 * with the data we're using here) and put the player into the
		 * approperiate faction.
		 * 
		 * Note that the old MMNET code looped through the houses until it found
		 * one that purported to "own" the player. This is a pretty dramatic
		 * reversal of process, and not as OO-appropriate :-(
		 */
		SHouse loginHouse = toLogin.getMyHouse();
		if (loginHouse == null) {
			toUser(
					"Null login faction referenced from SPlayer. Major problem. Report ASAP.",
					Username, true);
			return;
		}
		String s = loginHouse.doLogin(toLogin);

		/*
		 * String returned from house includes motd, etc. The house performs one
		 * last-ditch check to see if the player is alread in the house and may
		 * return a null if it finds the player present, despite the failure of
		 * all of our previous location attempts.
		 */
		if (s != null) {

			// send the login message/MOTD
			toUser(s, Username, true);

			// Send the player his basic info (units, techs, etc)
			CampaignMain.cm.toUser("PS|" + toLogin.toString(true), Username,
					false);

			if (isUsingAdvanceRepair()) {

				if (!toLogin.hasRepairingUnits()) {
					CampaignMain.cm.toUser("PL|UTT|"
							+ toLogin.totalTechsToString(), Username, false);
					CampaignMain.cm.toUser("PL|UAT|"
							+ toLogin.totalTechsToString(), Username, false);
				} else {
					CampaignMain.cm.toUser("PL|UTT|"
							+ toLogin.totalTechsToString(), Username, false);
					CampaignMain.cm
							.toUser("PL|UAT|"
									+ toLogin.availableTechsToString(),
									Username, false);
				}
			}

			/*
			 * Player is logging in so clear their armies opps and send the
			 * player his army eligibilities.
			 */
			for (SArmy currA : toLogin.getArmies()) {
				currA.getLegalOperations().clear();
				CampaignMain.cm.getOpsManager().checkOperations(currA, false);
			}

			// send all currently online players to the one logging in
			StringBuilder result = new StringBuilder("PI|PL|");
			for (House vh : data.getAllHouses()) {
				SHouse currH = (SHouse) vh;
				for (SPlayer currP : currH.getReservePlayers().values())
					result.append(this.getPlayerUpdateString(currP) + "|");

				for (SPlayer currP : currH.getActivePlayers().values())
					result.append(this.getPlayerUpdateString(currP) + "|");

				for (SPlayer currP : currH.getFightingPlayers().values())
					result.append(this.getPlayerUpdateString(currP) + "|");
			}
			toUser(result.toString(), Username, false);

			// Add the logging in player to everyone who is already online
			this.doSendToAllOnlinePlayers("PI|DA|"
					+ getPlayerUpdateString(toLogin), false);

			// Send him the Tick Counter
			this.toUser("CC|NT|" + this.TThread.getRemainingSleepTime() + "|"
					+ false, Username, false);

			/*
			 * Once the player is logged in, set his last-command-sent to the
			 * current time. This stops the idle-kicking code from immediately
			 * logging out players who've just come online and not yet sent any
			 * commands.
			 */
			toLogin.setLastTimeCommandSent(System.currentTimeMillis());
			toLogin.setLastOnline(System.currentTimeMillis());

			/*
			 * Check if Staff Member and send MMOTD if so.
			 */
			if (CampaignMain.cm.getServer().isModerator(Username))
				CampaignMain.cm.toUser("(Moderator Mail) Mod MOTD: "
						+ CampaignMain.cm.getConfig("MMOTD"), Username);

			/*
			 * INCREDIBLY BAD HACK!
			 * 
			 * As player's sign into factions, get an IP and add it to the
			 * logger. With the demise of nfc.log (removed from NFC2, which was
			 * grafted into MekWars), there is a need for a grepable iplog.0 to
			 * search for double accounts and re- entering/ban circumventing
			 * players. Despite the heinous way we draw the IP, this should
			 * work.
			 * 
			 * @urgru 1.29.06 :-(
			 */
			MWServ.mwlog.ipLog("Name: " + Username + " IP: "
					+ CampaignMain.cm.getServer().getIP(Username));

		}
	}// end CampaignMain.doLogin(String userName)

	/**
	 * Log a player out of the campaign. The CampaignMain portion of logout is
	 * markedly simpler than login. All of the more complex code (like
	 * chickening and disconnection thread spinning) is dealt with in SHouse.
	 * 
	 * Note that all players who log out are inserted into the savePlayer hash
	 * for removal. this.getPlayer() will retreive the memory resident SPlayer
	 * from the save queue if the player returns before the purge.
	 */
	public void doLogoutPlayer(String name) {

		// if the name is null or blank, return.
		if (name == null || name.trim().length() == 0)
			return;

		// if there is not player with the given name, return
		SPlayer toLogout = this.getPlayer(name);
		if (toLogout == null)
			return;

		/*
		 * double check to make sure the SPlayer object does not reside in the lost Souls hash
		 * this is incase someone connected but never logged into thier house or never 
		 * registered and enrolled.
		 */
		cm.releaseLostSoul(name);
		// set save, then log the player out of his house
		toLogout.setSave();
		toLogout.getMyHouse().doLogout(toLogout);// hacky.

		// clear the addon and send the new logged out status to all players
		this.doSendToAllOnlinePlayers("PI|CS|" + name + "|"
				+ SPlayer.STATUS_LOGGEDOUT, false);
		toUser(
				"[*] You've logged out of the campaign.",
				name, true);
	}

	public MWServ getServer() {
		return this.myServer;
	}

	public String getPlayerUpdateString(SPlayer p) {

		StringBuffer result = new StringBuffer();
		if (p == null)
			return result.toString();

		// Hide Reserve and Active Status
		int Status = p.getDutyStatus();
		if (Status == SPlayer.STATUS_RESERVE
				&& Boolean.parseBoolean(getConfig("HideActiveStatus")))
			Status = SPlayer.STATUS_ACTIVE;

		result.append(p.getName());
		result.append("|");
		result.append(p.getExperience());
		result.append("#");
		if (Boolean.parseBoolean(getConfig("HideELO")))
			result.append("0");
		else
			result.append(p.getRatingRounded());

		result.append("#");
		result.append(Status);
		result.append("#");
		if (p.getFluffText().equals(""))
			result.append(" #");
		else{
			result.append(p.getFluffText()); 
			result.append("#");
		}

		result.append(p.getHouseFightingFor().getName());
		result.append("#");
		result.append(p.getMyHouse().isMercHouse());
		result.append("#");
		result.append(p.getSubFactionName());
		return result.toString();
	}

	/**
	 * This sends status updates of Player p to all players
	 * 
	 * @param p
	 */
	public void sendPlayerStatusUpdate(SPlayer p, boolean sendToAll) {

		// get the player's actual status
		int realStatus = p.getDutyStatus();
		int sendStatus = realStatus;

		// if obfuscating active/deactive status, change sendstatus
		if (realStatus == SPlayer.STATUS_RESERVE
				&& Boolean.parseBoolean(getConfig("HideActiveStatus")))
			sendStatus = SPlayer.STATUS_ACTIVE;

		// send the obfuscated status to everyone, and real status to player
		if (sendToAll)
			this.doSendToAllOnlinePlayers("PI|CS|" + p.getName() + "|"
					+ sendStatus, false);
		this.toUser("CS|" + realStatus, p.getName(), false);
	}

	/**
	 * Get an SPlayer, by name. This searches the reserve, active and fighting
	 * hashes of all factions until the player is found or factions are
	 * exhausted.
	 * 
	 * If a player is not in a faction, check the to-save hash. Its entirely
	 * possible that the player is already in memory, but logged out and is
	 * awaiting a purge.
	 * 
	 * If no matching player is found online, the server will attempt to read
	 * one in from a text file. If even this fails, a null is returned.
	 * 
	 * NOTE: A player brought into memory using getPlayer is not automatically
	 * logged into his house. Temporary loads (ex: commands targetted at offline
	 * players) will put the player directly into the save queue, as if he was
	 * logged out. This is why the save queue is/must be searched prior to*
	 * reading the text file.
	 */
	public SPlayer getPlayer(String pName) {
		return getPlayer(pName, true);
	}

	public SPlayer getPlayer(String pName, boolean save) {

		// Fix for Draw games.
		if (pName.equalsIgnoreCase("DRAW") || pName.toUpperCase().startsWith("DRAW#"))
			return null;

		
		if ( lostSouls.containsKey(pName.toLowerCase()) )
			return lostSouls.get(pName.toLowerCase());
		
		// look for faction players
		SPlayer result = null;
		for (House vh : data.getAllHouses()) {
			SHouse h = (SHouse) vh;
			result = h.getPlayer(pName);
			if (result != null){
				//MWServ.mwlog.debugLog(pName+" Found in house data");
				return result;
			}
		}

		/* look for players awaiting purge
		String lowerName = pName.toLowerCase();
		if (savePlayers.containsKey(lowerName))
			return savePlayers.get(lowerName);
		 */
		
		/*
		 * no online player, so try to read from a file. if we do pull a file,
		 * set it's save value to true. this will make sure that temp-loads are
		 * saved/purged quickly.
		 */


		result = this.loadPlayerFile(pName, false);

		if ( result != null )
			lostSouls.put(pName.toLowerCase(),result);	

		return result;
	}

	/**
	 * Method which loads a player file from text.
	 * 
	 * THIS SHOULD NOT BE USED. CampaignMain.getPlayer(String name) will check
	 * to see if a player is already in memory, and then call this loader if the
	 * player needs to be brought in from text. If you need to get a player,
	 * always use .getPlayer(String name) instead.
	 * 
	 * A player who is loaded is put into the CampaignMain
	 */
	private SPlayer loadPlayerFile(String name, boolean explicitName) {

		if (!name.startsWith("[Dedicated]") && !name.startsWith("War Bot")) {

			try {
				if(CampaignMain.cm.isUsingMySQL()) {
					boolean playerFound = false;
					if(CampaignMain.cm.MySQL.playerExists(name)) {
						playerFound = true;
					}
					if(playerFound) {
						SPlayer p = new SPlayer();
						int pid = CampaignMain.cm.MySQL.getPlayerIDByName(name);
						p.fromDB(pid);
						return p;
					}
				}
				// log the load attempt & create readers
				MWServ.mwlog.mainLog("Loading pfile for: " + name);

				File pFile = null;
				if (explicitName)
					pFile = new File("./campaign/players/" + name);
				else
					pFile = new File("./campaign/players/" + name.toLowerCase()
							+ ".dat");

				FileInputStream fis = new FileInputStream(pFile);
				BufferedReader dis = new BufferedReader(new InputStreamReader(
						fis));

				// create player from string read by dis
				SPlayer p = new SPlayer();

				String pString = dis.readLine();
				p.fromString(pString);

				// close the streams and return player
				dis.close();
				fis.close();
				return p;
			} catch (FileNotFoundException fnf) {

				if (!name.toLowerCase().startsWith("nobody")
						&& !name.equals("SERVER")
						&& !name.toLowerCase().startsWith("war bot")
						&& !name.toLowerCase().startsWith("[dedicated]")){
					MWServ.mwlog.errLog("could not find a pfile for " + name);
					MWServ.mwlog.debugLog(fnf);
					MWServ.mwlog.debugLog("could not find a pfile for " + name);
				}
				return null;
			} catch (Exception ex) {
				MWServ.mwlog.errLog(ex);
				MWServ.mwlog.errLog("Unable to load pfile for " + name);
				return null;
			}
		}

		return null;

	}

	public void toUser(String txt, String Username) {
		toUser(txt, Username, true);
	}

	public void toUser(String txt, String Username, boolean isChat) {
		if (isChat)
			myServer.fromCampaignMod("CH|" + txt, Username);
		else
			myServer.fromCampaignMod(txt, Username);
	}

	public void init() {
		server.MWServ.mwlog.modLog("SERVER STARTED");

		// Fill the commands Table
		Commands.put("ACCEPTATTACKFROMRESERVE",
				new AcceptAttackFromReserveCommand());
		Commands.put("ACCEPTCONTRACT", new AcceptContractCommand());
		Commands.put("ACTIVATE", new ActivateCommand());
		Commands.put("ADDLEADER", new AddLeaderCommand());
		Commands.put("ADDOMNIVARIANTMOD", new AddOmniVariantModCommand());
		Commands.put("ADDPARTS", new AddPartsCommand());
		Commands.put("ADDSONG", new AddSongCommand());
		Commands.put("ADDTRAIT", new AddTraitCommand());
		Commands.put("ADMINADDSERVEROPFLAGS",
				new AdminAddServerOpFlagsCommand());
		Commands.put("ADMINALLOWHOUSEDEFECTION",
				new AdminAllowHouseDefectionCommand());
		Commands.put("ADMINCALCULATEHOUSERANKINGS",
				new AdminCalculateHouseRankingsCommand());
		Commands.put("ADMINCHANGEFACTIONCONFIG",
				new AdminChangeFactionConfigCommand());
		Commands.put("ADMINCHANGEPLANETOWNER",
				new AdminChangePlanetOwnerCommand());
		Commands.put("ADMINCHANGESERVERCONFIG",
				new AdminChangeServerConfigCommand());
		Commands.put("ADMINCREATEFACTION", new AdminCreateFactionCommand());
		Commands.put("ADMINCREATEPLANET", new AdminCreatePlanetCommand());
		Commands.put("ADMINCREATEFACTORY", new AdminCreateFactoryCommand());
		Commands.put("ADMINCREATESOLARIS", new AdminCreateSolarisCommand());
		Commands.put("ADMINCREATETERRAIN", new AdminCreateTerrainCommand());
		Commands.put("ADMINDESTROYFACTORY", new AdminDestroyFactoryCommand());
		Commands.put("ADMINDESTROYPLANET", new AdminDestroyPlanetCommand());
		Commands.put("ADMINDESTROYTERRAIN", new AdminDestroyTerrainCommand());
		Commands.put("ADMINDONATE", new AdminDonateCommand());
		Commands.put("ADMINEXCHANGEPLANETOWNERSHIP",
				new AdminExchangePlanetOwnershipCommand());
		Commands.put("ADMINGETUNITCOMPONENTS",
				new AdminGetUnitComponentsCommand());
		Commands.put("ADMINGRANTCOMPONENTS", new AdminGrantComponentsCommand());
		Commands.put("ADMINHOUSEPILOTS", new AdminHousePilotsCommand());
		Commands.put("ADMINHOUSESTATUS", new AdminHouseStatusCommand());
		Commands.put("ADMINLOCKCAMPAIGN", new AdminLockCampaignCommand());
		Commands.put("ADMINLOCKFACTORY", new AdminLockFactoryCommand());
		Commands.put("ADMINLISTANDREMOVEOMG",
				new AdminListAndRemoveOMGCommand());
		Commands.put("ADMINLISTHOUSEBANNEDAMMO",
				new AdminListHouseBannedAmmoCommand());
		Commands.put("ADMINLISTSERVERBANNEDAMMO",
				new AdminListServerBannedAmmoCommand());
		Commands.put("ADMINMOVEPLANET", new AdminMovePlanetCommand());
		Commands.put("ADMINPASSWORD", new AdminPasswordCommand());
		Commands.put("ADMINPLAYERSTATUS", new AdminPlayerStatusCommand());
		Commands.put("ADMINPURGEHOUSEBAYS", new AdminPurgeHouseBaysCommand());
		Commands.put("ADMINPURGEHOUSECONFIGS",
				new AdminPurgeHouseConfigsCommand());
		Commands.put("ADMINRELOADHOUSECONFIGS",
				new AdminReloadHouseConfigsCommand());
		Commands.put("ADMINREMOVEALLFACTORIES",
				new AdminRemoveAllFactoriesCommand());
		Commands.put("ADMINREMOVEALLTERRAIN",
				new AdminRemoveAllTerrainCommand());
		Commands.put("ADMINREMOVEPLANETOWNERSHIP",
				new AdminRemovePlanetOwnershipCommand());
		Commands.put("ADMINREMOVESERVEROPFLAGS",
				new AdminRemoveServerOpFlagsCommand());
		Commands.put("ADMINREMOVEUNITSONMARKET",
				new AdminRemoveUnitsOnMarketCommand());
		Commands.put("ADMINREQUESTBUILDTABLE", new AdminRequestBuildTableCommand());
		Commands.put("ADMINRESETHOUSERANKINGS",
				new AdminResetHouseRankingsCommand());
		Commands.put("ADMINRESETPLAYER", new AdminResetPlayerCommand());
		Commands.put("ADMINRETURNPLANETSTOORIGINALOWNERS",
				new AdminReturnPlanetsToOriginalOwnersCommand());
		Commands.put("ADMINSAVE", new AdminSaveCommand());
		Commands.put("ADMINSAVEBLACKMARKETCONFIGS",
				new AdminSaveBlackMarketConfigsCommand());
		Commands.put("ADMINSAVECOMMANDLEVELS",
				new AdminSaveCommandLevelsCommand());
		Commands.put("ADMINSAVEFACTIONCONFIGS",
				new AdminSaveFactionConfigsCommand());
		Commands.put("ADMINSAVEPLANETSTOXML",
				new AdminSavePlanetsToXMLCommand());
		Commands.put("ADMINSAVESERVERCONFIGS",
				new AdminSaveServerConfigsCommand());
		Commands.put("ADMINSETAMMOCOST", new AdminSetAmmoCostCommand());
		Commands.put("ADMINSETBANTARGETING", new AdminSetBanTargetingCommand());
		Commands.put("ADMINSETBLACKMARKETSETTING",
				new AdminSetBlackMarketSettingCommand());
		Commands.put("ADMINSETCOMMANDLEVEL", new AdminSetCommandLevelCommand());
		Commands.put("ADMINSETHOMEWORLD", new AdminSetHomeWorldCommand());
		Commands.put("ADMINSETHOUSEABBREVIATION", new AdminSetHouseAbbreviationCommand());
		Commands.put("ADMINSETHOUSEFLUFILE", new AdminSetHouseFluFileCommand());
		Commands.put("ADMINSETHOUSEPLAYERCOLOR",
				new AdminSetHousePlayerColorCommand());
		Commands.put("ADMINSETHOUSETECHLEVEL",
				new AdminSetHouseTechLevelCommand());
		Commands.put("ADMINSETPLANETBOARDSIZE",
				new AdminSetPlanetBoardSizeCommand());
		Commands.put("ADMINSETPLANETGRAVITY",
				new AdminSetPlanetGravityCommand());
		Commands.put("ADMINSETPLANETOPFLAGS",
				new AdminSetPlanetOpFlagsCommand());
		Commands.put("ADMINSETPLANETORIGINALOWNER",
				new AdminSetPlanetOriginalOwnerCommand());
		Commands.put("ADMINSETPLANETMAPSIZE",
				new AdminSetPlanetMapSizeCommand());
		Commands.put("ADMINSETPLANETTEMPERATURE",
				new AdminSetPlanetTemperatureCommand());
		Commands.put("ADMINSETPLANETVACUUM", new AdminSetPlanetVacuumCommand());
		Commands.put("ADMINSETHOUSEAMMOBAN", new AdminSetHouseAmmoBanCommand());
		Commands.put("ADMINSETSERVERAMMOBAN",
				new AdminSetServerAmmoBanCommand());
		Commands.put("ADMINSCRAP", new AdminScrapCommand());
		Commands.put("ADMINSPOOF", new AdminSpoofCommand());
		Commands.put("ADMINTERMINATEALL", new AdminTerminateAllCommand());
		Commands.put("ADMINTRANSFER", new AdminTransferCommand());
		Commands.put("ADMINUNLOCKCAMPAIGN", new AdminUnlockCampaignCommand());
		Commands.put("ADMINUPDATECLIENTPARAM",
				new AdminUpdateClientParamCommand());
		Commands.put("ADMINUPDATEPLANETOWNERSHIP",
				new AdminUpdatePlanetOwnershipCommand());
		Commands.put("ADMINUPLOADBUILDTABLE", new AdminUploadBuildTableCommand());
		Commands.put("ADMINVIEWLOG", new AdminViewLogCommand());
		Commands.put("ALL", new ArmyLowerLimiterCommand());
		Commands.put("AOFS", new ArmyOpForceSizeCommand());
		Commands.put("AUL", new ArmyUpperLimiterCommand());
		Commands.put("ATTACK", new AttackCommand());
		Commands.put("ATTACKFROMRESERVE", new AttackFromReserveCommand());
		Commands.put("AUTOPLANETSTATUS", new AutoPlanetStatusCommand());
		Commands.put("BID", new BidCommand());
		Commands.put("BMSTATUS", new BMStatusCommand());
		Commands.put("BUILDTABLELIST", new BuildTableListCommand());
		Commands.put("BUILDTABLEVALIDATOR", new BuildTableValidatorCommand());
		Commands.put("BUYBAYS", new BuyBaysCommand());
		Commands.put("BUYPARTS", new BuyPartsCommand());
		Commands.put("BUYPILOTSFROMHOUSE", new BuyPilotsFromHouseCommand());
		Commands.put("CALCDIST", new CalcDistCommand());
		Commands.put("CAMPAIGNCONFIG", new CampaignConfigCommand());
		Commands.put("CANCELOFFER", new CancelOfferCommand());
		Commands.put("CHANGEHOUSECOLOR", new ChangeHouseColorCommand());
		Commands.put("CHANGENAME", new ChangeNameCommand());
		// Double CA
		Commands.put("CHECKATTACK", new CheckAttackCommand());
		Commands.put("CA", new CheckAttackCommand());
		//
		Commands.put("CHECK", new CheckCommand());
		Commands.put("CHECKARMYELIGIBILITY", new CheckArmyEligibilityCommand());
		Commands.put("CHECKARMYLINK", new CheckArmyLinkCommand());
		Commands.put("CHECKDIST", new CheckDistCommand());
		Commands.put("COMMENCEOPERATION", new CommenceOperationCommand());
		// Double CRL
		Commands.put("CREATEARMY", new CreateArmyCommand());
		Commands.put("CRA", new CreateArmyCommand());
		//
		Commands.put("CREATEARMYFROMMUL", new CreateArmyFromMulCommand());
		Commands.put("CREATEMERCFACTION", new CreateMercFactionCommand());
		Commands.put("CREATESUBFACTION", new CreateSubFactionCommand());
		Commands.put("CREATEUNIT", new CreateUnitCommand());
		Commands.put("CYCLOPSCHECKUP", new CyclopsCheckupCommand());
		Commands.put("CYCLOPSOPTIMIZE", new CyclopsOptimizeCommand());
		Commands.put("CYCLOPSPOSTMAXSIZE", new CyclopsPostMaxSizeCommand());
		Commands.put("CYCLOPSRESET", new CyclopsResetCommand());
		Commands.put("CYCLOPSTEMPLATELOADER",
				new CyclopsTemplateLoaderCommand());
		Commands.put("CYCLOPSVERSION", new CyclopsVersionCommand());
		Commands.put("DEACTIVATE", new DeactivateCommand());
		Commands.put("DECLINEATTACKFROMRESERVE",
				new DeclineAttackFromReserveCommand());
		Commands.put("DEFECT", new DefectCommand());
		Commands.put("DEFEND", new DefendCommand());
		Commands.put("DELETEACCOUNT", new DeleteAccountCommand());
		Commands.put("DEMOTEPLAYER", new DemotePlayerCommand());
		Commands.put("DIRECTSELLUNIT", new DirectSellUnitCommand());
		Commands.put("DISPLAYPLAYERPERSONALPILOTQUEUE",
				new DisplayPlayerPersonalPilotQueueCommand());
		Commands.put("DISPLAYUNITREPAIRJOBS",
				new DisplayUnitRepairJobsCommand());
		Commands.put("DONATE", new DonateCommand());
		Commands.put("DONATEPILOT", new DonatePilotCommand());
		// Double EHM
		Commands.put("EHM", new EmployeeHouseMailCommand());
		Commands.put("EMPLOYEEHOUSEMAIL", new EmployeeHouseMailCommand());
		//
		Commands.put("ENROLL", new EnrollCommand());
		// Double EXU
		Commands.put("EXCHANGEUNIT", new ExchangeUnitCommand());
		Commands.put("EXU", new ExchangeUnitCommand());
		Commands.put("EXM", new ExchangeUnitCommand());
		// Exchange Pilots
		Commands.put("EXCHANGEPILOTINUNIT", new ExchangePilotInUnitCommand());
		Commands.put("EXP", new ExchangePilotInUnitCommand());
		Commands.put("FACTION", new HouseCommand());// alias for house command
		Commands.put("FACTIONLEADERFLUFF", new FactionLeaderFluffCommand());
		Commands.put("FLF", new FactionLeaderFluffCommand());
		Commands.put("FACTIONLEADERMUTE", new FactionLeaderMuteCommand());
		Commands.put("FLM", new FactionLeaderMuteCommand());
		Commands.put("FIRETECHS", new FireTechsCommand());
		Commands.put("FIXAMMO", new FixAmmoCommand());
		Commands.put("FLUFF", new FluffCommand());
		Commands.put("FORCEDDEFECT", new ForcedDefectCommand());
		Commands.put("FORCEUPDATE", new ForceUpdateCommand());
		Commands.put("GAMES", new GamesCommand());
		Commands.put("GETFACTIONCONFIGS", new GetFactionConfigsCommand());
		Commands.put("GETMODLOG", new GetModLogCommand());
		Commands.put("GETPLAYERUNITS", new GetPlayerUnitsCommand());
		Commands.put("GETSERVEROPFLAGS", new GetServerOpFlagsCommand());
		Commands.put("GOOSE", new GooseCommand());
		Commands.put("GRANTEXP", new GrantEXPCommand());
		Commands.put("GRANTINFLUENCE", new GrantInfluenceCommand());
		Commands.put("GRANTMONEY", new GrantMoneyCommand());
		Commands.put("GRANTREWARD", new GrantRewardCommand());
		Commands.put("HARDTERMINATE", new HardTerminateCommand());
		Commands.put("HIREANDMAINTAIN", new HireAndMaintainHelper());
		Commands.put("HIREANDREQUESTNEW", new HireAndRequestNewHelper());
		Commands.put("HIREANDREQUESTUSED", new HireAndRequestUsedHelper());
		Commands.put("HIRETECHS", new HireTechsCommand());
		Commands.put("HOUSE", new HouseCommand());
		Commands.put("HOUSECONTRACTS", new HouseContractsCommand());
		// Double HM
		Commands.put("HOUSEMAIL", new HouseMailCommand());
		Commands.put("HM", new HouseMailCommand());
		//
		Commands.put("HOUSERANKING", new HouseRankingCommand());
		Commands.put("HOUSESTATUS", new HouseStatusCommand());
		// Double IC
		Commands.put("INCHARACTER", new InCharacterCommand());
		Commands.put("IC", new InCharacterCommand());
		Commands.put("INVIS", new InvisCommand());
		// ISS
		Commands.put("ISSTATUS", new ISStatusCommand());// legace commands for
														// the client
		Commands.put("ISS", new ISStatusCommand());
		Commands.put("US", new ISStatusCommand());
		Commands.put("UNIVERSESTATUS", new ISStatusCommand());
		//
		Commands.put("JOINATTACK", new JoinAttackCommand());
		Commands.put("LASTONLINE", new LastOnlineCommand());
		Commands.put("LINKUNIT", new LinkUnitCommand());
		Commands.put("LISTCOMMANDS", new ListCommandsCommand());
		Commands.put("LISTMULS", new ListMulsCommand());
		Commands.put("LISTMULTIPLAYERGROUPS",
				new ListMultiPlayerGroupsCommand());
		Commands.put("LISTSERVEROPFLAGS", new ListServerOpFlagsCommand());
		Commands.put("LISTSUBFACTIONS", new ListSubFactionCommand());
		Commands.put("LOGIN", new LoginCommand());
		Commands.put("LOGOUT", new LogoutCommand());
		// Double MStatus
		Commands.put("MERCSTATUS", new MercStatusCommand());
		Commands.put("MSTATUS", new MercStatusCommand());
		Commands.put("MMOTD", new MMOTDCommand());
		//
		// Double MM
		Commands.put("MODERATORMAIL", new ModeratorMailCommand());
		Commands.put("MM", new ModeratorMailCommand());
		//
		Commands.put("MODDEACTIVATE", new ModDeactivateCommand());
		Commands.put("MODGAMES", new ModGamesCommand());
		Commands.put("MODFULLREPAIR", new ModFullRepairCommand());
		Commands.put("MODLOG", new ModLogCommand());
		Commands.put("MODNOPLAY", new ModNoPlayCommand());
		Commands.put("MODREFRESHFACTORY", new ModRefreshFactoryCommand());
		Commands.put("MODTERMINATE", new ModTerminateCommand());
		Commands.put("MOTD", new MOTDCommand());
		Commands.put("MYBIDS", new MyBidsCommand());
		Commands.put("MYSTATUS", new MyStatusCommand());
		Commands.put("MYVOTES", new MyVotesCommand());
		Commands.put("NAMEARMY", new NameArmyCommand());
		Commands.put("NAMEPILOT", new NamePilotCommand());
		Commands.put("NOPLAY", new NoPlayCommand());
		Commands.put("NOTIFYFIGHTING", new NotifyFightingCommand());
		Commands.put("OFFERCONTRACT", new OfferContractCommand());
		Commands.put("PLANET", new PlanetCommand());
		Commands.put("PLAYERLOCKARMY", new PlayerLockArmyCommand());
		Commands.put("PLAYERS", new PlayersCommand());
		Commands.put("PLAYERUNLOCKARMY", new PlayerUnlockArmyCommand());
		Commands.put("PROMOTEPLAYER", new PromotePlayerCommand());
		Commands.put("RANGE", new RangeCommand());
		Commands.put("RECALL", new RecallCommand());
		Commands.put("RECALLBID", new RecallBidCommand());
		Commands.put("REPOD", new RepodCommand());
		Commands.put("REFRESHFACTORY", new RefreshFactoryCommand());
		Commands.put("REFUSECONTRACT", new RefuseContractCommand());
		Commands.put("RELOADALLAMMO", new ReloadAllAmmoCommand());
		Commands.put("REMOVEANDADDNOPLAY", new RemoveAndAddNoPlayHelper());
		// Double RML
		Commands.put("REMOVEARMY", new RemoveArmyCommand());
		Commands.put("RMA", new RemoveArmyCommand());
		//
		Commands.put("REMOVEFACTIONPILOT", new RemoveFactionPilotCommand());
		Commands.put("REMOVELEADER", new RemoveLeaderCommand());
		Commands.put("REMOVEPARTS", new RemovePartsCommand());
		Commands.put("REMOVEPILOT", new RemovePilotCommand());
		Commands.put("REMOVESONG", new RemoveSongCommand());
		Commands.put("REMOVESUBFACTION", new RemoveSubFactionCommand());
		Commands.put("REMOVETRAIT", new RemoveTraitCommand());
		Commands.put("REMOVEVOTE", new RemoveVoteCommand());
		Commands.put("REPAIRUNIT", new RepairUnitCommand());
		Commands.put("REQUEST", new RequestCommand());
		Commands.put("REQUESTDONATED", new RequestDonatedCommand());
		Commands.put("REQUESTSERVERMAIL", new RequestServerMailCommand());
		Commands.put("REQUESTSUBFACTIONPROMOTION", new RequestSubFactionPromotionCommand());
		Commands.put("RESTARTREPAIRTHREAD", new RestartRepairThreadCommand());
		Commands.put("RETRIEVEALLOPERATIONS",
				new RetrieveAllOperationsCommand());
		Commands.put("RETRIEVEOPERATION", new RetrieveOperationCommand());
		Commands.put("RETRIEVEMUL", new RetrieveMulCommand());
		Commands.put("RETRIEVEALLMULS", new RetrieveAllMulsCommand());
		Commands.put("RETIREPILOT", new RetirePilotCommand());
		Commands.put("SALVAGEUNIT", new SalvageUnitCommand());
		Commands.put("SCRAP", new ScrapCommand());
		Commands.put("SENDCLIENTDATA", new SendClientDataCommand());
		Commands.put("SELL", new SellCommand());
		Commands.put("SELLBAYS", new SellBaysCommand());
		Commands.put("SENDTOMISC", new SendToMiscCommand());
		Commands.put("SERVERVERSION", new ServerVersionCommand());
		Commands.put("SERVERGAMEOPTIONS", new ServerGameOptionsCommand());
		Commands.put("SETADVANCEDPLANETTERRAIN",
				new SetAdvancedPlanetTerrainCommand());
		Commands.put("SETAUTOEJECT", new SetAutoEjectCommand());
		Commands.put("SETAUTOREORDER", new SetAutoReorderCommand());
		Commands.put("SETCLIENTVERSION", new SetClientVersionCommand());
		Commands.put("SETEDGESKILLS", new SetEdgeSkillsCommand());
		Commands.put("SETELO", new SetEloCommand());
		Commands.put("SETHOUSEBASEPILOTSKILLS",
				new SetHouseBasePilotSkillsCommand());
		Commands.put("SETHOUSEBASEPILOTINGSKILLS",
				new SetHouseBasePilotingSkillsCommand());
		Commands.put("SETHOUSELOGO", new SetHouseLogoCommand());
		Commands.put("SETHOUSECONQUER", new SetHouseConquerCommand());
		Commands.put("SETHOUSEINHOUSEATTACKS",
				new SetHouseInHouseAttacksCommand());
		Commands.put("SETOPERATION", new SetOperationCommand());
		Commands.put("SETMAINTAINED", new SetMaintainedCommand());
		Commands.put("SETMMOTD", new SetMMOTDCommand());
		Commands.put("SETMOTD", new SetMOTDCommand());
		Commands.put("SETMULTIPLAYERGROUP", new SetMultiPlayerGroupCommand());
		Commands.put("SETMYLOGO", new SetMyLogoCommand());
		Commands.put("SETPLANETCONQUER", new SetPlanetConquerCommand());
		Commands.put("SETPLANETCONQUERPOINTS",
				new SetPlanetConquerPointsCommand());
		Commands.put("SETPLANETMINOWNERSHIP",
				new SetPlanetMinOwnerShipCommand());
		Commands.put("SETPLANETWAREHOUSE", new SetPlanetWareHouseCommand());
		Commands.put("SETPLANETCOMPPRODUCTION",
				new SetPlanetCompProductionCommand());
		Commands.put("SETSEARCHLIGHT", new SetSearchLightCommand());
		Commands.put("SETSUBFACTIONCONFIG", new SetSubFactionConfigCommand());
		Commands.put("SETTARGETSYSTEMTYPE", new SetTargetSystemTypeCommand());
		Commands.put("SETUNITAMMO", new SetUnitAmmoCommand());
		Commands.put("SETUNITAMMOBYCRIT", new SetUnitAmmoByCritCommand());
		Commands.put("SETUNITBURST", new SetUnitBurstCommand());
		Commands.put("SETUNITCOMMANDER", new SetUnitCommanderCommand());
		Commands.put("SETUNMAINTAINED", new SetUnmaintainedCommand());
		// Double ShowToHouse
		Commands.put("SHOWTOHOUSE", new ShowToHouseCommand());
		Commands.put("STH", new ShowToHouseCommand());
		Commands.put("SIMPLEREPAIR", new SimpleRepairCommand());
		// Double SingASong
		Commands.put("SINGASONG", new SingASongCommand());
		Commands.put("SAS", new SingASongCommand());
		Commands.put("STOPREPAIRJOB", new StopRepairJobCommand());
		Commands.put("STRIPALLPARTSCACHE", new StripAllPartsCacheCommand());
		Commands.put("STRIPUNITS", new StripUnitsCommand());
		Commands.put("TERMINATE", new TerminateCommand());
		Commands.put("TERMINATECONTRACT", new TerminateContractCommand());
		Commands.put("TICK", new TickCommand());
		Commands.put("TOUCH", new TouchCommand());
		Commands.put("TRANSFERMONEY", new TransferMoneyCommand());
		Commands.put("TRANSFERPILOT", new TransferPilotCommand());
		Commands.put("TRANSFERUNIT", new TransferUnitCommand());
		Commands.put("UPDATEOPERATIONS", new UpdateOperationsCommand());
		Commands.put("UPLOADMUL", new UploadMulCommand());
		Commands.put("UNEMPLOYEDMERCS", new UnemployedMercsCommand());
		Commands.put("UNENROLL", new UnenrollCommand());
		Commands.put("UNITPOSITION", new UnitPositionCommand());
		Commands.put("UNLOCKLANCES", new UnlockLancesCommand());
		Commands.put("USEREWARDPOINTS", new UseRewardPointsCommand());
		Commands.put("VIEWPLAYERPARTS", new ViewPlayerPartsCommand());
		Commands.put("VIEWPLAYERPERSONALPILOTQUEUE",
				new ViewPlayerPersonalPilotQueueCommand());
		Commands.put("VIEWPLAYERUNIT", new ViewPlayerUnitCommand());
		Commands.put("VOTE", new VoteCommand());

		// Old / comamds move to be usable by /c or /
		Commands.put("AM", new ServerAnnouncementCommand());
		Commands.put("SA", new ServerAnnouncementCommand());
		Commands.put("SERVERANNOUNCEMENT", new ServerAnnouncementCommand());
		Commands.put("BAN", new BanCommand());
		Commands.put("BANIP", new BanIPCommand());
		Commands.put("BANLIST", new BanListCommand());
		Commands.put("COLOR", new ColorCommand());
		Commands.put("COLOUR", new ColorCommand());
		Commands.put("CONFIG", new ConfigCommand());
		Commands.put("GETSAVEDMAIL", new GetSavedMailCommand());
		Commands.put("IGNORE", new IgnoreCommand());
		Commands.put("IGNORELIST", new IgnoreListCommand());
		Commands.put("IPLIST", new IPListCommand());
		Commands.put("KICK", new KickCommand());
		Commands.put("MAIL", new MailCommand());
		Commands.put("ME", new MeCommand());
		Commands.put("ROLL", new RollCommand());
		Commands.put("REGISTER", new RegisterCommand());
		Commands.put("SHUTDOWN", new ShutdownCommand());
		Commands.put("SETSMOTD", new SetSMOTDCommand());
		Commands.put("SIGNOFF", new SignOffCommand());
		Commands.put("SMOTD", new SMOTDCommand());
		Commands.put("UNBAN", new UnBanCommand());
		Commands.put("UNBANIP", new UnBanIPCommand());

		// ok we've put all the commands in the command hash now lets set the
		// levels
		try {
			File configFile = new File("./data/commands/commands.dat");
			FileInputStream fis = new FileInputStream(configFile);
			BufferedReader dis = new BufferedReader(new InputStreamReader(fis));
			while (dis.ready()) {
				StringTokenizer command = new StringTokenizer(dis.readLine(),
						"#");
				String commandName = command.nextToken();
				if (Commands.containsKey(commandName))
					(Commands.get(commandName)).setExecutionLevel(Integer
							.parseInt(command.nextToken()));
			}
		} catch (Exception ex) {
			MWServ.mwlog
					.errLog("Unable to find commands.dat. Continuing with defaults in place");
			TreeMap<String, Command> commandTable = new TreeMap<String, Command>(
					cm.getServerCommands());

			try {

				File fp = new File("./data/commands");
				if (!fp.exists())
					fp.mkdir();

				FileOutputStream out = new FileOutputStream(
						"./data/commands/commands.dat");
				PrintStream p = new PrintStream(out);

				String commandName = "";
				for (Iterator i = commandTable.keySet().iterator(); i.hasNext(); commandName = (String) i
						.next()) {
					Command commandMethod = CampaignMain.cm.getServerCommands()
							.get(commandName);
					if (commandName == null || commandMethod == null)
						continue;
					p.println(commandName.toUpperCase() + "#"
							+ commandMethod.getExecutionLevel());
				}
			} catch (Exception ex1) {
				MWServ.mwlog.errLog(ex1);
				MWServ.mwlog.errLog("Unable to save command levels");
			}

		}

		// Is the server data already there? (config files)? if not, create one
		if (data.getAllPlanets().size() > 0 && data.getAllHouses().size() > 0)
			return;

		// No SHouse Data yet? Parse the XML file and creathe them
		if ((!CampaignMain.cm.isUsingMySQL() && data.getAllHouses().size() == 0) || (cm.isUsingMySQL() && cm.MySQL.countFactions() == 0)) {
			try {
				XMLFactionDataParser parser = new XMLFactionDataParser(
						"./data/factions.xml");
				for (SHouse h : parser.getFactions()) {
					addHouse(h);
				}
			} catch (Exception ex) {
				MWServ.mwlog
						.errLog("Error while reading faction data -- bailing out");
				MWServ.mwlog.errLog(ex);
				MWServ.mwlog.mainLog("Error while reading Faction Data!");
				System.exit(1);
			}

			// Add the Newbie-SHouse
			SHouse solaris = new NewbieHouse(data.getUnusedHouseID(),
					CampaignMain.cm.getConfig("NewbieHouseName"), "#33CCCC", 4,
					5, "SOL");
			addHouse(solaris);
		}

		// No Planets Data yet? Parse XML and create world.
		if (data.getAllPlanets().size() == 0) {

			// First, clear out the factions' initialrankings.
			for (House h : data.getAllHouses()) {
				SHouse sh = (SHouse) h;
				sh.setInitialHouseRanking(0);
			}

			try {

				XMLPlanetDataParser parser = new XMLPlanetDataParser(
						"./data/planets.xml");
				for (SPlanet p : parser.getPlanets()) {

					// add the planet
					addPlanet(p);

					// set initial influences
					for (House h : p.getInfluence().getHouses()) {

						SHouse sh = (SHouse) h;
						if (sh == null) {
							MWServ.mwlog
									.errLog("Null faction found while loading Planets.xml. Planet: "
											+ p.getName());
							continue;
						}

						if (p.getInfluence().getOwner() != null
								&& sh.getId() == p.getInfluence().getOwner()
										.intValue())
							sh.addPlanet(p);

						sh.setInitialHouseRanking(sh.getInitialHouseRanking()
								+ p.getInfluence().getInfluence(sh.getId()));
					}
				}
			} catch (Exception ex) {
				MWServ.mwlog
						.errLog("Error while reading planet data -- bailing out");
				MWServ.mwlog.errLog(ex);
				MWServ.mwlog.mainLog("Error while reading Planet Data!");
				System.exit(1);
			}

			HashMap<Integer, Integer> solFlu = new HashMap<Integer, Integer>();
			solFlu.put(
					new Integer(CampaignMain.cm.getHouseFromPartialString(
							CampaignMain.cm.getConfig("NewbieHouseName"), null)
							.getId()), new Integer(100));
			SPlanet newbieP = new SPlanet(0, "Solaris VII", new Influences(
					solFlu), 0, 0, -3, -2);
			if (data.getPlanetByName("Solaris VII") == null) {
				addPlanet(newbieP);
				CampaignMain.cm.getHouseFromPartialString(
						CampaignMain.cm.getConfig("NewbieHouseName"), null)
						.addPlanet(newbieP);
			}
		}

		// Save it on startup
		this.toFile();
	}

	public void addHouse(SHouse s) {
		data.addHouse(s);
	}

	public void addPlanet(SPlanet p) {
		
		if ( p.getOriginalOwner().trim().equals("") ){
			if ( p.getOwner() == null )
				p.setOriginalOwner(cm.getConfig("NewbieHouseName"));
			p.setOriginalOwner(p.getOwner().getName());
		}
		data.addPlanet(p);
	}

	public synchronized void userRoll(String text, String Username) {

		// added by VEGETA 2/8/2003
		//Random random = new Random();
		int dice = 2;
		int sides = 6;
		int total = 0;
		int roll = 0;
		String x = "";

		if (text.trim().length() > 0) {

			StringTokenizer ST = new StringTokenizer(text, "d");
			try {
				if (ST.hasMoreElements()) {
					x = (String) ST.nextElement();
					dice = Integer.parseInt(x.trim());
				}
				if (ST.hasMoreElements()) {
					x = (String) ST.nextElement();
					sides = Integer.parseInt(x.trim());
				}

			} catch (NumberFormatException ex) {
				toUser("/roll: error parsing arguments.", Username, true);
				return;
			} catch (StringIndexOutOfBoundsException ex) {
				toUser("/roll: error parsing arguments.", Username, true);
				return;
			}
		}

		if (dice < 1 || sides < 2) {
			this.doSendToAllOnlinePlayers(Username
					+ " loves the smell of napalm in the morning.", true);
			return;
		}

		if (dice > 20 || sides > 100) {
			this.doSendToAllOnlinePlayers(Username + " is a stupid haxx0r!",
					true);
			return;
		}

		StringBuilder diceBuffer = new StringBuilder();

		for (int i = 0; i < dice; i++) {
			//roll = random.nextInt(sides) + 1;
			roll = cm.getRandomNumber(sides) + 1;
			total += roll;

			// for one die, we're all set
			if (dice < 2) {
				diceBuffer.append(roll);
				continue;
			}

			// 2+ dice, use commas and "and"
			if (i < dice - 1) {
				diceBuffer.append(roll);
				diceBuffer.append(", ");
			} else {
				diceBuffer.append("and ");
				diceBuffer.append(roll);
			}
		}
		if (text != "")
			this.doSendToAllOnlinePlayers(Username
					+ " rolled " + diceBuffer + " for a total of " + total
					+ ", using " + text + ".", true);
		else
			this.doSendToAllOnlinePlayers(Username
					+ " rolled " + diceBuffer + " for a total of " + total
					+ ", using 2d6.", true);
	}

	public void addMechStat(String Filename, int mechsize, int gameplayed,
			int gamewon, int scrapped) {
		addMechStat(Filename, mechsize, gameplayed, gamewon, scrapped, 0);
	}

	public void addMechStat(String Filename, int mechsize, int gameplayed,
			int gamewon, int scrapped, int destroyed) {
		MechStatistics m = null;
		if (this.MechStats.get(Filename) == null) {
			m = new MechStatistics(Filename, mechsize);
		} else
			m = this.MechStats.get(Filename);
		SUnit unit = new SUnit();
		m.setOriginalBV(unit.loadMech(Filename).calculateBattleValue());
		unit = null;// clear the unused unit

		m.addStats(gameplayed, gamewon, m.getOriginalBV());
		m.setTimesScrapped(m.getTimesScrapped() + scrapped);
		m.setTimesDestroyed(m.getTimesDestroyed() + destroyed);
		MechStats.put(Filename, m);
	}

	/**
	 * Private method that sends KI| (kick) commands to idle players. Broken
	 * into a seperate method to reduce code repetitiveness in slice().
	 */
	private void checkAndRemoveIdle(SPlayer p, long maxIdleTime) {

		// dont boot mods
		if (this.getServer().isModerator(p.getName()))
			return;

		// if he's already logged out, who cares?
		if (p.getDutyStatus() <= SPlayer.STATUS_LOGGEDOUT)
			return;

		// redundant, but never boot fighting players
		if (p.getDutyStatus() == SPlayer.STATUS_FIGHTING)
			return;

		// reserve or active player. check his times.
		// NOTE: KI| command is actualy campaign logout. GBB| a disco/kill.
		if (System.currentTimeMillis() - p.getLastTimeCommandSent() > maxIdleTime) {
			CampaignMain.cm.toUser(
					"You were logged out by the server (excessive idle time).",
					p.getName(), true);
			CampaignMain.cm.toUser("KI|idler", p.getName(), false);
		}
	}

	/**
	 * Slicer. Called by SliceThread @ the end of its config.txt defined wait
	 * duration. Gives influence to active players, checks for (and kicks) idle
	 * players, and saves player files.
	 * 
	 * Slices are generally much shorter than ticks, and involve players and
	 * player data much more heavily than factions/high-end campaign structures.
	 * This is the exact opposite of the .tick() (see below).
	 */
	public synchronized void slice(int sliceID) {

		// write log header
		MWServ.mwlog.mainLog("Slice #" + sliceID + " Started");
		MWServ.mwlog.cmdLog("Slice #" + sliceID + " Started");
		MWServ.mwlog.infoLog("Slice #" + sliceID + " Started: "
				+ System.currentTimeMillis());

		// loop through all houses
		for (House vh : data.getAllHouses()) {
			SHouse currH = (SHouse) vh;

			// load max idle time, converted to ms
			long maxIdleTime = Long.parseLong(CampaignMain.cm
					.getConfig("MaxIdleTime")) * 60000;

			// for reserve players, we only check idleness
			if (maxIdleTime > 0) {
				for (SPlayer currP : currH.getReservePlayers().values())
					this.checkAndRemoveIdle(currP, maxIdleTime);
			}

			/*
			 * Active players get the whole shebang - influence addition,
			 * maintainance, and an idle check (if enabled).
			 */
			for (SPlayer currP : currH.getActivePlayers().values()) {
				currP.doMaintainance();
				this.toUser(currP.addInfluenceAtSlice(), currP.getName(), true);
				if (maxIdleTime > 0)
					checkAndRemoveIdle(currP, maxIdleTime);
			}

			// fighters only have maint. they get influence grants post-game.
			for (SPlayer currP : currH.getFightingPlayers().values()) {
				currP.doMaintainance();
				// People fighting are always up to date
				if (maxIdleTime > 0)
					currP.setLastTimeCommandSent(System.currentTimeMillis()
							+ maxIdleTime);
			}

		}// end all houses

		// check to see if we should save on this slice
		int saveOnSlice = CampaignMain.cm.getIntegerConfig("SaveEverySlice");
		if (saveOnSlice < 1)
			saveOnSlice = 1;
		if (sliceID % saveOnSlice == 0) {
			this.savePlayers();// Once all of the saving is done clear
								// everything for the next tick.
			this.saveTopUnitID();
		}

		// write log header
		MWServ.mwlog.mainLog("Slice #" + sliceID + " Finished");
		MWServ.mwlog.cmdLog("Slice #" + sliceID + " Finished");
		MWServ.mwlog.infoLog("Slice #" + sliceID + " Finished: "
				+ System.currentTimeMillis());

	}// end the slice...

	/**
	 * Tick is the main timekeeping unit of the server. At each tick, various
	 * statistics are checked and shown to players (ex: house ranking) and
	 * various portions of the campaign are cleaned up or finalized (ex: market
	 * sales).
	 * 
	 * Most tick actions involve meta-functions, houses, the market, and so on.
	 * The only tick mechanic that acts directly on players is Mezzo (pricemod)
	 * drain.
	 */
	public synchronized void tick(boolean real, int tickid) {

		// add header to log
		MWServ.mwlog.mainLog("Tick #" + tickid + " Started");
		MWServ.mwlog.cmdLog("Tick #" + tickid + " Started");
		MWServ.mwlog.infoLog("Tick #" + tickid + " Started");

		// log the number of games underway
		int gameCount = 0;
		for (ShortOperation currO : this.getOpsManager().getRunningOps()
				.values())
			if (currO.getStatus() == ShortOperation.STATUS_INPROGRESS) {
				gameCount++;
			}
		MWServ.mwlog.tickLog(gameCount + " games in progress.");

		// tick all houses
		int totalPlayersOnline = 0;
		for (House vh : data.getAllHouses()) {

			// we can safely cast to SHouse
			SHouse currH = (SHouse) vh;

			/*
			 * Total faction load for logs.
			 */
			int activePs = currH.getActivePlayers().size();
			int fightingPs = currH.getFightingPlayers().size();
			int totalFactionPlayers = currH.getReservePlayers().size()
					+ activePs + fightingPs;
			MWServ.mwlog.tickLog(currH.getName() + " has "
					+ totalFactionPlayers + " members online (" + activePs
					+ " active, " + fightingPs + " fighting)");

			// if there are any faction players online, tick the house
			if (totalFactionPlayers > 0 || real == false) {

				String houseTickInfo = "";
				try {
					MWServ.mwlog.debugLog("Starting Faction Tick");
					houseTickInfo = currH.tick(real, tickid);
					MWServ.mwlog.debugLog("Finished Faction Tick");
				} catch (Exception e) {
					MWServ.mwlog.errLog("Problems with faction tick.");
					MWServ.mwlog.errLog(e);
				}

				// do some things (reset scraps, etc) for players
				for (SPlayer currP : currH.getAllOnlinePlayers().values()) {

					// Clear up any users that the server still thinks is
					// connected.
					if (this.getServer().getClient(currP.getName()) == null) {
						MWServ.mwlog.debugLog("Logging out Player "+currP.getName());
						this.doLogoutPlayer(currP.getName());
						continue;
					}

					totalPlayersOnline++;
					MWServ.mwlog.debugLog("Setting Scraps This tick for "+currP.getName());
					currP.setScrapsThisTick(0);
					MWServ.mwlog.debugLog("Setting Donations This tick for "+currP.getName());
					currP.setDonatonsThisTick(0);
					MWServ.mwlog.debugLog("Healing pilots This tick for "+currP.getName());
					currP.healPilots();

					MWServ.mwlog.debugLog("Updating faction info for "+currP.getName());
					// return the result of the faction tick to everyone, to
					// misc tab.
					toUser("SM|" + houseTickInfo, currP.getName(), false);
				}

			}// end if(there is a player in the faction)
		}// end for(all houses)

		// append the total player count to the logs
		MWServ.mwlog.tickLog("Total players: "
				+ this.getServer().userCount(true) + " online, "
				+ totalPlayersOnline + " logged in.");

		/*
		 * Send the latest game reports to the players, and increment the
		 * removal-counters.
		 */
		String generalResult = "<br>";
		String opsTick = this.opsManager.tick();
		if (opsTick.length() > 0)
			generalResult += opsTick + "<br><br>";

		// if the relative house rankings should be shown, do so.
		String rankTick = "";
		if (Boolean.parseBoolean(this.getConfig("ShowFactionRanks")))
			rankTick = Statistics.getReadableHouseRanking(true);

		if (rankTick.length() > 0)
			generalResult += rankTick + "<br><br>";

		// send the combined & spaced string to players
		if (generalResult.toLowerCase().replace("<br>", " ").trim().length() > 0)
			this.doSendToAllOnlinePlayers(generalResult, true);

		/*
		 * Tick the market. This will resolve any auctions w/ 0 ticks remaining
		 * and decrement all others.
		 * 
		 */
		market.tick();

		MWServ.mwlog.tickLog("Parts Market Tick Started");
		partsmarket.tick();
		MWServ.mwlog.tickLog("Parts Market Tick Finished");

		MWServ.mwlog.tickLog("doRanking");
		// output player stats to HTML, if enabled.
		if (Boolean.parseBoolean(getConfig("HTMLOUTPUT")))
			Statistics.doRanking();

		MWServ.mwlog.tickLog("PurgePlayersFiles");
		// purge old player files
		purgePlayerFiles();

		MWServ.mwlog.tickLog("Automated Backup");
		/*
		 * finally, check to see if we should back up. note that the thread will
		 * die immediately if it is not time to back up (last was written within
		 * offset).
		 */
		aub = new AutomaticBackup(System.currentTimeMillis());
		// new Thread(aub).start();
		aub.run();

		MWServ.mwlog.tickLog("GC");
		// force a GC. this may not be necessary anymore?
		System.gc();

		// mainlog footer
		MWServ.mwlog.mainLog("Tick #" + tickid + " Finished");
		MWServ.mwlog.cmdLog("Tick #" + tickid + " Finished");
		MWServ.mwlog.infoLog("Tick #" + tickid + " Finished");
	}

	/* The Planetary Control Way */
	public TreeSet<HouseRankingHelpContainer> getHouseRanking() {

		Hashtable<String, HouseRankingHelpContainer> factionContainer = new Hashtable<String, HouseRankingHelpContainer>();
		for (House currHouse : data.getAllHouses()) {
			SHouse h = (SHouse) currHouse;
			if (!h.isMercHouse() && !h.isNewbieHouse()) {
				HouseRankingHelpContainer hrc = new HouseRankingHelpContainer(h);
				factionContainer.put(h.getName(), hrc);
			}
		}

		for (Planet p : data.getAllPlanets()) {

			for (House currH : p.getInfluence().getHouses()) {
				SHouse hs = (SHouse) currH;
				if (!hs.isNewbieHouse() && !hs.isMercHouse())
					factionContainer.get(hs.getName()).addAmount(
							p.getInfluence().getInfluence(hs.getId()));
			}

		}

		TreeSet<HouseRankingHelpContainer> s = new TreeSet<HouseRankingHelpContainer>();
		for (HouseRankingHelpContainer currContainer : factionContainer
				.values())
			s.add(currContainer);

		return s;
	}

	/**
	 * Send a bit of text to all players who are currently online. Can be chat,
	 * or a command/message.
	 */
	public void doSendToAllOnlinePlayers(String text, boolean isChat) {

		for (House vh : data.getAllHouses()) {
			SHouse h = (SHouse) vh;
			for (String currName : h.getReservePlayers().keySet())
				this.toUser(text, currName, isChat);

			for (String currName : h.getActivePlayers().keySet())
				this.toUser(text, currName, isChat);

			for (String currName : h.getFightingPlayers().keySet())
				this.toUser(text, currName, isChat);
		}
	}

	/**
	 * Send a bit of text to all players in a given faction. Can be chat, or a
	 * command/message.
	 */
	public void doSendToAllOnlinePlayers(SHouse h, String text, boolean isChat) {

		for (String currName : h.getReservePlayers().keySet())
			this.toUser(text, currName, isChat);

		for (String currName : h.getActivePlayers().keySet())
			this.toUser(text, currName, isChat);

		for (String currName : h.getFightingPlayers().keySet())
			this.toUser(text, currName, isChat);
	}

	/**
	 * Update all player armies that are online This is normally called after
	 * operations have been updated.
	 */
	public void updateAllOnlinePlayerArmies() {

		for (House vh : data.getAllHouses()) {
			SHouse h = (SHouse) vh;
			for (SPlayer currPlayer : h.getReservePlayers().values()) {
				for (SArmy a : currPlayer.getArmies())
					CampaignMain.cm.getOpsManager().checkOperations(a, true);
			}

			for (SPlayer currPlayer : h.getActivePlayers().values()) {
				for (SArmy a : currPlayer.getArmies())
					CampaignMain.cm.getOpsManager().checkOperations(a, true);
			}

			for (SPlayer currPlayer : h.getFightingPlayers().values()) {
				for (SArmy a : currPlayer.getArmies())
					CampaignMain.cm.getOpsManager().checkOperations(a, true);
			}
		}
	}

	/**
	 * Method that returns the SHouse that contains a player with a given name.
	 * If no factions has such a player online, return a null.
	 */
	public SHouse getHouseForPlayer(String Username) {
		String lowerName = Username.toLowerCase();
		for (House vh : data.getAllHouses()) {
			SHouse h = (SHouse) vh;
			if (h.getReservePlayers().containsKey(lowerName))
				return h;
			if (h.getActivePlayers().containsKey(lowerName))
				return h;
			if (h.getFightingPlayers().containsKey(lowerName))
				return h;
		}
		return null;
	}

	/**
	 * Check to see if the server is currently using cyclops if so then check to
	 * make sure the link is turned on in case the SO's have turned in on while
	 * the server was already running. Also the link is nulled if the SO's turn
	 * off cyclops while the server is running.
	 * 
	 * @return
	 */
	public boolean isUsingCyclops() {
		boolean isUsing = Boolean.parseBoolean(myServer
				.getConfigParam("USECYCLOPS"));

		if (isUsing && mwcc == null) {
			mwcc = new MWCyclopsComm(myServer.getConfigParam("CYCLOPSIP"),
					myServer.getConfigParam("SERVERNAME"), myServer
							.getConfigParam("CYCLOPSURL"), Boolean
							.parseBoolean(myServer
									.getConfigParam("CYCLOPSDEBUG")));
			mwcc.start();
		} else if (!isUsing && mwcc != null) {
			mwcc.interrupt();
			mwcc = null;
		}// restart the thread if need be.
		else if (isUsing && mwcc != null
				&& (mwcc.isInterrupted() || !mwcc.isAlive()))
			mwcc.start();

		return isUsing;
	}

	public boolean isUsingMySQL() {
		boolean isUsing = Boolean.parseBoolean(myServer
				.getConfigParam("USEMYSQL"));

		if (isUsing && MySQL == null) {
			MySQL = new mysqlHandler();
		} else if (!isUsing && MySQL != null) {
			MySQL.closeMySQL();
			MySQL = null;
		}
		return isUsing;
	}

	public boolean isUsingIncreasedTechs() {
		return (Boolean.parseBoolean(CampaignMain.cm.getConfig("UseNonFactionUnitsIncreasedTechs")) && !CampaignMain.cm.isUsingAdvanceRepair());
	}
	
	public boolean isSynchingBB() {
		if (validBBVersion)
			return Boolean.parseBoolean(myServer.getConfigParam("MYSQL_SYNCHPHPBB"));
		return false;
	}
	
	public boolean requireEmailForRegistration() {
		return (isUsingMySQL() && isSynchingBB() && Boolean.parseBoolean(cm.getConfig("REQUIREEMAILFORREGISTRATION")));
	}
	
	public void turnOffBBSynch() {
		this.validBBVersion = false;
	}
	
	/*
	 * Checks to see if the campaign is using advanced repairs and starts up the
	 * thread if it is null
	 */
	public boolean isUsingAdvanceRepair() {
		boolean isUsing = Boolean
				.parseBoolean(cm.getConfig("UseAdvanceRepair"))
				|| Boolean.parseBoolean(cm.getConfig("UseSimpleRepair"));
		if (isUsing && RTT == null) {
			RTT = new RepairTrackingThread(Long.parseLong(cm
					.getConfig("TimeForEachRepairPoint")) * 1000);
			RTT.start();
		} else if (!isUsing && RTT != null) {
			RTT.interrupt();
			RTT = null;
		}

		return isUsing;
	}

	public void restartRTT(){
		boolean isUsing = Boolean
		.parseBoolean(cm.getConfig("UseAdvanceRepair"))
		|| Boolean.parseBoolean(cm.getConfig("UseSimpleRepair"));
		if ( isUsing ){
			RTT = null;
			RTT = new RepairTrackingThread(Long.parseLong(cm.getConfig("TimeForEachRepairPoint")) * 1000);
			RTT.start();
		}
	}
	
	public Random getR() {
		return r;
	}

	public int getRandomNumber(int seed){
		
		if( seed < 1 )
			return seed;
		
		
		float answer = r.nextFloat() * (float)seed;

		return (int)Math.floor(answer);
	}
	
	synchronized public void addToNewsFeed(String s) {
		addToNewsFeed(s, "");
	}

	synchronized public void addToNewsFeed(String title, String body) {
		String dateTimeFormat = "yyyy/MM/dd HH:mm:ss z";
		SimpleDateFormat sDF = new SimpleDateFormat(dateTimeFormat);
		Date date = new Date(System.currentTimeMillis());
		String dateTime = "[" + sDF.format(date) + "] ";
		StringBuffer msgBody = new StringBuffer(body);

		// Delete any html tags in the body
		while (msgBody.indexOf("<") > -1) {
			msgBody.delete(msgBody.indexOf("<"), msgBody.indexOf(">") + 1);
		}

		title = dateTime + title;
		this.NewsFeed.put(title, msgBody.toString());
		if (this.NewsFeed.size() > 200)
			this.NewsFeed.remove(this.NewsFeed.firstKey());

		try {
			FileOutputStream out = new FileOutputStream(getConfig("NewsPath"));
			PrintStream ps = new PrintStream(out);
			ps.println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>");
			ps.println("<rdf:RDF");
			ps
					.println("xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"");
			ps.println("xmlns=\"http://my.netscape.com/rdf/simple/0.9/\">");
			ps.println("<channel>");
			ps.println("<title>" + getServer().getConfigParam("SERVERNAME")
					+ " News Feed</title>");
			ps.println("<link>" + getServer().getConfigParam("TRACKERLINK")
					+ "</link>");
			ps.println("<description>Campaign News</description>");
			ps.println("</channel>");

			for (String header : NewsFeed.keySet()) {
				String newsBody = NewsFeed.get(header);
				ps.println("<item>");
				ps.println("<title>" + header + "</title>");
				ps.println("<description>" + newsBody + "</description>");
				ps.println("<link>" + getServer().getConfigParam("TRACKERLINK")
						+ "</link>");
				ps.println("</item>");
			}
			ps.println("</rdf:RDF>");
			ps.close();
		} catch (FileNotFoundException efnf) {
			// ignore
		} catch (Exception ex) {
			MWServ.mwlog.errLog("Problems writing the news feed");
			/*
			 * MWServ.mwlog.errLog(ex); MWServ.addToErrorLog(ex);
			 */
		}

	}

	public Market2 getMarket() {
		return market;
	}

	public PartsMarket getPartsMarket() {
		return partsmarket;
	}

	public void initializePilotSkills() {
		// PilotSkills
		pilotSkills.put(new Integer(PilotSkill.DodgeManeuverSkillID),
				(new DodgeManeuverSkill(PilotSkill.DodgeManeuverSkillID)));
		pilotSkills.put(new Integer(PilotSkill.ManeuveringAceSkillID),
				(new ManeuveringAceSkill(PilotSkill.ManeuveringAceSkillID)));
		pilotSkills.put(new Integer(PilotSkill.MeleeSpecialistSkillID),
				(new MeleeSpecialistSkill(PilotSkill.MeleeSpecialistSkillID)));
		pilotSkills.put(new Integer(PilotSkill.PainResistanceSkillID),
				(new PainResistanceSkill(PilotSkill.PainResistanceSkillID)));
		pilotSkills.put(new Integer(PilotSkill.AstechSkillID),
				(new AstechSkill(PilotSkill.AstechSkillID)));
		pilotSkills.put(new Integer(PilotSkill.NaturalAptitudeGunnerySkillID),
				(new NaturalAptitudeGunnerySkill(
						PilotSkill.NaturalAptitudeGunnerySkillID)));
		pilotSkills.put(new Integer(PilotSkill.NaturalAptitudePilotingSkillID),
				(new NaturalAptitudePilotingSkill(
						PilotSkill.NaturalAptitudePilotingSkillID)));
		pilotSkills.put(new Integer(PilotSkill.IronManSkillID),
				(new IronManSkill(PilotSkill.IronManSkillID)));
		pilotSkills
				.put(new Integer(PilotSkill.GunneryBallisticSkillID),
						(new GunneryBallisticSkill(
								PilotSkill.GunneryBallisticSkillID)));
		pilotSkills.put(new Integer(PilotSkill.GunneryLaserSkillID),
				(new GunneryLaserSkill(PilotSkill.GunneryLaserSkillID)));
		pilotSkills.put(new Integer(PilotSkill.GunneryMissileSkillID),
				(new GunneryMissileSkill(PilotSkill.GunneryMissileSkillID)));
		pilotSkills.put(new Integer(PilotSkill.TacticalGeniusSkillID),
				(new TacticalGeniusSkill(PilotSkill.TacticalGeniusSkillID)));
		pilotSkills
				.put(new Integer(PilotSkill.WeaponSpecialistSkillID),
						(new WeaponSpecialistSkill(
								PilotSkill.WeaponSpecialistSkillID)));
		pilotSkills.put(new Integer(PilotSkill.SurvivalistSkillID),
				(new SurvivalistSkill(PilotSkill.SurvivalistSkillID)));
		pilotSkills.put(new Integer(PilotSkill.TraitID), (new TraitSkill(
				PilotSkill.TraitID)));
		pilotSkills.put(new Integer(PilotSkill.EnhancedInterfaceID),
				(new EnhancedInterfaceSkill(PilotSkill.EnhancedInterfaceID)));
		pilotSkills.put(new Integer(PilotSkill.QuickStudyID),
				(new QuickStudySkill(PilotSkill.QuickStudyID)));
		pilotSkills.put(new Integer(PilotSkill.GiftedID), (new GiftedSkill(
				PilotSkill.GiftedID)));
		pilotSkills.put(new Integer(PilotSkill.MedTechID), (new MedTechSkill(
				PilotSkill.MedTechID)));
		pilotSkills.put(new Integer(PilotSkill.EdgeSkillID), (new EdgeSkill(
				PilotSkill.EdgeSkillID)));
		pilotSkills.put(new Integer(PilotSkill.ClanPilotTraingID),
				(new ClanPilotTrainingSkill(PilotSkill.ClanPilotTraingID)));
	}

	public Properties getConfig() {
		return config;
	}

	public Hashtable<String, Command> getServerCommands() {
		return Commands;
	}

	public Hashtable<String, String> getServerBannedAmmo() {
		return cm.getData().getServerBannedAmmo();
	}

	public Hashtable<Long, Integer> getAmmoCost() {
		return cm.getData().getAmmoCost();
	}

	/**
	 * @return the campaign's VoteManager
	 */
	public VoteManager getVoteManager() {
		return voteManager;
	}

	/**
	 * This retuns the blackMarketEquipmentCostTable This hashTable keeps track
	 * of all the mix/max costs and parts production for the Black market. This
	 * is used to allow players to buy spare parts to repair Their units.
	 * 
	 * @return blackMarketEquipmentCostTable
	 */
	public Hashtable<String, Equipment> getBlackMarketEquipmentTable() {
		return blackMarketEquipmentCostTable;
	}

	public TickThread getTThread() {
		return TThread;
	}

	public ImmunityThread getIThread() {
		return IThread;
	}

	public Vector getUnresolvedContracts() {
		return unresolvedContracts;
	}

	public SPilotSkill getRandomSkill(SPilot p, int unitType) {
		int total = 0;

		Iterator<SPilotSkill> it = pilotSkills.values().iterator();
		Hashtable<Integer, Integer> skilltable = new Hashtable<Integer, Integer>();
		if (p.getSkills().has(PilotSkill.TraitID)) {
			// SPilotSkill skill =
			// (SPilotSkill)p.getSkills().getPilotSkill(SPilotSkill.TraitID);
			String trait = p.getTraitName();
			if (trait.indexOf("*") > -1)
				trait = trait.substring(0, trait.indexOf("*"));
			Vector<String> traitsList = this.getFactionTraits(p
					.getCurrentFaction());
			traitsList.trimToSize();
			for (String traitNames : traitsList) {
				StringTokenizer traitName = new StringTokenizer(traitNames, "*");
				String traitString = traitName.nextToken();
				if (traitString.equalsIgnoreCase(trait))
					while (traitName.hasMoreElements()) {
						int traitid = Integer.parseInt(traitName.nextToken());
						int traitMod = Integer.parseInt(traitName.nextToken());
						skilltable.put(traitid, traitMod);
					}
			}
		}

		// check for trait mods and add them
		while (it.hasNext()) {
			SPilotSkill skill = it.next();
			total += skill.getChance(unitType, p);
		}

		if (total == 0)
			return null;
		/*
		 * int rnd = 1;
		 * 
		 * if (total > 1) rnd = getR().nextInt(total) + 1;
		 */
		it = pilotSkills.values().iterator();
		Vector<SPilotSkill> skillBuilder = new Vector<SPilotSkill>(total,1);

		try {
			while (it.hasNext()) {
				SPilotSkill skill = it.next();
				int chance = skill.getChance(unitType, p);
				if (skilltable.get(new Integer(skill.getId())) != null)
					chance += skilltable.get(skill.getId());

				for (int pos = 0; pos < chance; pos++) {
					skillBuilder.add(skill);
				}
				skillBuilder.trimToSize();
				/*
				 * //MWServ.mwlog.errLog("Pilot: "+p.getName()+" Skill:
				 * "+skill.getName()+" Rnd "+rnd+ " chance: "+chance); if ( rnd <=
				 * chance ) return skill;
				 * 
				 * //else rnd -= skill.getChance(unitType,p);
				 */
			}

			return skillBuilder.elementAt(cm.getRandomNumber(skillBuilder.size()));
		} catch (Exception ex) {
			MWServ.mwlog
					.errLog("Problems during skill earning! Skill Table Size = "
							+ skillBuilder.size() + " total = " + total);
			return null;
		}
	}

	/**
	 * Create a skill from a string. Used by CreateUnitCommand.
	 */
	public SPilotSkill getPilotSkill(String skill) {

		for (SPilotSkill pSkill : pilotSkills.values()) {
			if (pSkill.getName().equalsIgnoreCase(skill)
					|| pSkill.getAbbreviation().equalsIgnoreCase(skill))
				return pSkill;
		}

		return null;
	}

	/**
	 * Get a pilot skill by ID number. Used to unstring SPilots in pfiles.
	 */
	public SPilotSkill getPilotSkill(int id) {
		return pilotSkills.get(new Integer(id));
	}

	/*
	 * Replace original readible time (which oddly adjusted times from MechStats
	 * into seconds, but used ms from System.currentTime() for comparison) with
	 * similar code from MWTracker.java.
	 * 
	 * This produces abbreviated timenames.
	 * 
	 * @urgru 8.6.05
	 */
	public static String readableTime(long elapsed) {

		// to return
		String result = "";

		long elapsedDays = (elapsed / 86400000);
		long elapsedHours = (elapsed % 86400000) / 3600000;
		long elapsedMinutes = (elapsed % 3600000) / 60000;

		if (elapsedDays > 0)
			result += elapsedDays + "d ";

		if (elapsedHours > 0 || elapsedDays > 0)
			result += elapsedHours + "h ";

		result += elapsedMinutes + "m";

		return result;
	}

	/**
	 * Method which generates human readible times from miliseconds. Useful only
	 * for times which are known to be minutes or seconds in length.
	 * 
	 * Produces full-word output.
	 */
	public static String readableTimeWithSeconds(long elapsed) {

		// to return
		String result = "";

		long elapsedMinutes = elapsed / 60000;
		long elapsedSeconds = (elapsed % 60000) / 1000;

		if (elapsedMinutes > 0)
			result += elapsedMinutes + " min";

		if (elapsedSeconds > 0 && elapsedMinutes > 0)
			result += ", " + elapsedSeconds + " sec";

		else if (elapsedSeconds > 0)
			result += elapsedSeconds + " sec";

		return result;
	}

	/**
	 * @return Returns the currentUnitID.
	 */
	public int getCurrentUnitID() {
		return currentUnitID;
	}

	/**
	 * @param currentUnitID
	 *            The currentUnitID to set.
	 */
	public void setCurrentUnitID(int currentUnitID) {
		this.currentUnitID = currentUnitID;
	}

	public synchronized int getAndUpdateCurrentUnitID() {
		currentUnitID++;
		return currentUnitID - 1;
	}

	public int getCurrentPilotID() {
		return currentPilotID;
	}

	public synchronized int getAndUpdateCurrentPilotID() {
		return ++currentPilotID;
	}

	public void setCurrentPilotID(int id) {
		this.currentPilotID = id;
	}

	public SHouse getHouseFromPartialString(String HouseString) {
		return getHouseFromPartialString(HouseString, null);
	}

	public SHouse getHouseFromPartialString(String HouseString, String Username) {

		// store matches so we can tell player if there's more than one
		int numMatches = 0;
		SHouse theMatch = null;

		for (House currH : data.getAllHouses()) {
			SHouse sh = (SHouse) currH;

			// exact match
			if (sh.getName().equals(HouseString))
				return sh;

			// store all matches
			if (sh.getName().startsWith(HouseString)) {
				theMatch = sh;
				numMatches++;
			}
		}

		// too many matches
		if (numMatches > 1) {
			if (Username != null)
				toUser("\"" + HouseString + "\" is not unique [" + numMatches
						+ " matches]. Please be more specific.", Username);
			return null;
		}

		if (numMatches == 0) {
			if (Username != null)
				toUser("Couldn't find a factions whose name begins with \""
						+ HouseString + "\". Try again.", Username, true);
			return null;
		}

		// only one match! send it back.
		return theMatch;
	}

/*	protected void addSavePlayer(SPlayer p) {
		savePlayers.put(p.getName().toLowerCase(), p);
	}

	protected void removeSavePlayer(SPlayer p) {
		savePlayers.remove(p.getName().toLowerCase());
	}
*/
	// return the mwcyclopscomm class
	public MWCyclopsComm getMWCC() {
		return mwcc;
	}

	/**
	 * Private method which writes out players who need to be saved and purges
	 * logged out/removable players from RAM.
	 * 
	 * Should be called only from .slice() or forceSave. See
	 * this.forceSavePlayers() for more info on admin-initiated player saves.
	 */
	private void savePlayers() {

		// go into sleep while the server is archiving player files
		while (this.isArchiving()) {
			try {
				Thread.sleep(125);
			} catch (Exception ex) {
				// do nothing
			}
		}

		// add log header
		Date d = new Date(System.currentTimeMillis());
		MWServ.mwlog.infoLog(d + ": Starting Player Saving cycle");
		for (House vh : CampaignMain.cm.getData().getAllHouses()) {
			SHouse currH = (SHouse) vh;
			for (SPlayer currP : currH.getAllOnlinePlayers().values()) {
				if(CampaignMain.cm.isUsingMySQL())
					currP.toDB();
				else
					this.savePlayerFile(currP);
			}
		}

		// write out log footer
		d = new Date(System.currentTimeMillis());
		MWServ.mwlog.mainLog(d + ": Player save cycle completed.");
		MWServ.mwlog.infoLog(d + ": Player saves finished.");

		/*
		 * Everyone in the save pile has been saved. This is nice, but not the
		 * end of the line. Now we need to purge the savePlayers hash.
		 * 
		 * Loop through and remove everyone we can (some players are not
		 * removable b/c of ongoing repairs). If the player is removable AND
		 * logged out, we can null his player and save some memory space @ next
		 * gc().
		 
		Iterator<SPlayer> i = savePlayers.values().iterator();
		while (i.hasNext()) {
			SPlayer p = i.next();
			if (p.isRemoveable()) {
				i.remove();
				if (p.getDutyStatus() == SPlayer.STATUS_LOGGEDOUT)
					p = null;
			}
		}*/

	}

	/**
	 * Public save method. Used by admins to save all online players and all
	 * players who are in the save queue. Is called from /save, /shutdown, and
	 * /c adminsave.
	 */
	public void forceSavePlayers(String Username) {

		// first, save everyone online
		for (House vh : CampaignMain.cm.getData().getAllHouses()) {
			SHouse currH = (SHouse) vh;
			for (SPlayer currP : currH.getAllOnlinePlayers().values()) {
				if(CampaignMain.cm.isUsingMySQL())
					currP.toDB();
				else
					this.savePlayerFile(currP);
				if (Username != null)
					CampaignMain.cm.toUser("AM:"+currP.getName() + " saved",
							Username, true);
			}
		}
	}

	/**
	 * Public save method to save one player Used by changename and defect
	 * commands This is used so the players have a Pfile created right away
	 */
	public void forceSavePlayer(SPlayer p) {
		if(CampaignMain.cm.isUsingMySQL())
			p.toDB();
		else
			savePlayerFile(p);
	}

	/**
	 * Private method which writes a player to the disc. This code was housed in
	 * SPlayer; however, it is only called from CampaignMain and (from an OO
	 * standpoint) only CMain should know the hardcoded paths which are used.
	 * 
	 * @author nmorris 1/13/06
	 */
	protected void savePlayerFile(SPlayer p) {

		try {
			if(CampaignMain.cm.isUsingMySQL()) {
				p.toDB();
				return;
			}
			String fileName = p.getName().toLowerCase();
			FileOutputStream pout = new FileOutputStream("./campaign/players/"
					+ fileName.toLowerCase() + ".dat");
			PrintStream pfile = new PrintStream(pout);

			/*
			 * Put a lock on the player while saving. Do NOT allow .toString()
			 * to set a lock, or we'll get deadlocks.
			 */
			synchronized (p) {
				pfile.println(p.toString(false));
			}

			pfile.close();
			pout.close();
		}

		catch ( FileNotFoundException fnfe ){
			//Since we are saving to disk do nothing. 
			//The proccess is most likely already being used.
			return;
		}
		catch (Exception ex) {
			MWServ.mwlog.errLog(ex);
			MWServ.mwlog.errLog("Unable to save " + p.getName().toLowerCase());
		}
	}

	public void loadBanAmmo(String line) {

		try {
			StringTokenizer st = new StringTokenizer(line, "#");
			String HouseName = (String) st.nextElement();
			SHouse faction = null;
			if (!HouseName.equalsIgnoreCase("server")) {
				faction = CampaignMain.cm.getHouseFromPartialString(HouseName,
						null);
				while (st.hasMoreTokens())
					faction.getBannedAmmo().put(st.nextToken(), "Banned");
			} else {
				while (st.hasMoreElements())
					CampaignMain.cm.getServerBannedAmmo().put(st.nextToken(),
							"Banned");
			}
		} catch (Exception ex) {
		}// make it compatible with people that had the old format,without
			// the timestamp on the first line, the first time and now dont.
	}

	public void loadAmmoCosts(String line) {

		try {
			StringTokenizer st = new StringTokenizer(line, "#");
			while (st.hasMoreTokens())
				this.getAmmoCost().put(Long.parseLong(st.nextToken()),
						Integer.parseInt(st.nextToken()));
		} catch (Exception ex) {
		}// make it compatible with people that had the old format,without
			// the timestamp on the first line, the first time and now dont.
	}

	/**
	 * Load the black market settings from file.
	 * 
	 */
	public void loadBlackMarketSettings() {

		try {
			File bmFile = new File("./data/blackmarketsettings.dat");

			if (!bmFile.exists())
				return;

			FileInputStream fis = new FileInputStream(bmFile);
			BufferedReader dis = new BufferedReader(new InputStreamReader(fis));

			// Ignore Time Stamp
			dis.readLine();

			while (dis.ready()) {
				Equipment bme = new Equipment();
				String line = dis.readLine();
				StringTokenizer data = new StringTokenizer(line, "#");

				bme.setEquipmentInternalName(data.nextToken());
				bme.setMinCost(Double.parseDouble(data.nextToken()));
				bme.setMaxCost(Double.parseDouble(data.nextToken()));
				bme.setMinProduction(Integer.parseInt(data.nextToken()));
				bme.setMaxProduction(Integer.parseInt(data.nextToken()));

				cm.getBlackMarketEquipmentTable().put(
						bme.getEquipmentInternalName(), bme);
			}

		} catch (Exception ex) {
			MWServ.mwlog.errLog(ex);
		}
	}

	public void saveTopUnitID() {

		int topID = cm.getCurrentUnitID();

		try {
			FileOutputStream pout = new FileOutputStream(
					"./campaign/topserverid.dat");
			PrintStream unitIDFile = new PrintStream(pout);
			unitIDFile.println(topID);
			unitIDFile.println(cm.getCurrentPilotID());
			unitIDFile.close();
			pout.close();
		} catch (Exception ex) {
			MWServ.mwlog.errLog(ex);
		}
	}

	public void loadTopUnitID() {
		try {
			File idFile = new File("./campaign/topserverid.dat");
			FileInputStream fis = new FileInputStream(idFile);
			BufferedReader dis = new BufferedReader(new InputStreamReader(fis));

			cm.setCurrentUnitID(Integer.parseInt(dis.readLine()));
			cm.setCurrentPilotID(Integer.parseInt(dis.readLine()));

			dis.close();
			fis.close();
		} catch (FileNotFoundException FNFE) {
			// Do nothing.
			MWServ.mwlog
					.errLog("Unable to fine/open ./campaign/topserverid.dat. moving on.");
		} catch (Exception ex) {
			MWServ.mwlog.errLog(ex);
		}
	}

	public void setGamesCompleted(int i) {
		gamesCompleted = i;
	}

	public void addGamesCompleted(int i) {
		this.setGamesCompleted(this.getGamesCompleted() + i);
	}

	public int getGamesCompleted() {
		return gamesCompleted;
	}

	public int getMachineGunCount(ArrayList<Mounted> weaponList) {
		int count = 0;

		for (Mounted weapons : weaponList) {
			WeaponType weapon = (WeaponType) weapons.getType();
			if (weapon.hasFlag(WeaponType.F_MG))
				count++;
		}
		return count;
	}

	/**
	 * Use to load a factions trait file.
	 * 
	 * @author Torren (Jason Tighe)
	 * @param faction
	 * @return
	 */
	public Vector<String> getFactionTraits(String faction) {
		Vector<String> traits = new Vector<String>(1,1);
		File traitNames = new File("./data/pilotnames/" + faction.toLowerCase()
				+ "traitnames.txt");

		if (!traitNames.exists())
			traitNames = new File("./data/pilotnames/commontraitnames.txt");

		try {

			FileInputStream fis = new FileInputStream(traitNames);
			BufferedReader dis = new BufferedReader(new InputStreamReader(fis));

			while (dis.ready())
				traits.addElement(dis.readLine());

			dis.close();
			fis.close();

		} catch (FileNotFoundException nf) {
			MWServ.mwlog.errLog("File Not Found: " + traitNames);
		} catch (Exception ex) {
			MWServ.mwlog.errLog("Error loading Faction Traits: " + faction);
			MWServ.mwlog.errLog(ex);
		}

		traits.trimToSize();
		return traits;
	}

	public void saveFactionTraits(String faction, Vector traits) {

		File traitFile = new File("./data/pilotnames/" + faction.toLowerCase()
				+ "traitnames.txt");

		try {

			if (!traitFile.exists())
				traitFile.createNewFile();

			FileOutputStream fos = new FileOutputStream(traitFile);
			PrintStream p = new PrintStream(fos);

			for (int pos = 0; pos < traits.size(); pos++) {
				String tempTrait = (String) traits.elementAt(pos);
				p.println(tempTrait);
			}

			p.close();
			fos.close();

		} catch (Exception ex) {
			MWServ.mwlog.errLog("Error while saving trait file for faction: "
					+ faction);
			MWServ.mwlog.errLog(ex);
		}
	}

	public void setOmniVariantMods(Hashtable<String, String> table) {
		this.omniVariantMods = table;
	}

	public Hashtable<String, String> getOmniVariantMods() {
		return this.omniVariantMods;
	}

	public void saveOmniVariantMods() {

		if (this.omniVariantMods.size() < 1)
			return;

		try {

			FileOutputStream out = new FileOutputStream(
					"./campaign/omnivariantmods.dat");
			PrintStream p = new PrintStream(out);

			for (String currKey : cm.getOmniVariantMods().keySet()) {
				String currMod = cm.getOmniVariantMods().get(currKey);
				p.println(currKey + "#" + currMod);
			}

			p.close();
			out.close();
		} catch (Exception ex) {
			MWServ.mwlog.errLog("Error while saving omnivariantmods.dat");
			MWServ.mwlog.errLog(ex);
		}
	}

	/**
	 * @author Torren (Jason Tighe)
	 * 
	 * This method will go through and check all the player files and forceible
	 * unenroll anyone that is over <code>days</code> idle.
	 * 
	 */
	public void purgePlayerFiles() {
		long days = Long.parseLong(CampaignMain.cm
				.getConfig("PurgePlayerFilesDays"));
		if(CampaignMain.cm.isUsingMySQL()) {
			CampaignMain.cm.MySQL.purgeStalePlayers(days);
			return;
		}
		// Turn purging off by setting it to 0 or less days
		if (days <= 0)
			return;
		// convert days to milliseconds
		days *= 24;
		days *= 60;
		days *= 60;
		days *= 1000;

		File[] playerList = new File("./campaign/players").listFiles();

		for (File player : playerList) {
			if (player.isDirectory())
				continue;
			if (player.lastModified() + days < System.currentTimeMillis()) {
				String playerName = player.getName().substring(0,
						player.getName().indexOf(".dat"));
				SPlayer p = this.getPlayer(playerName, false);
				p.addExperience(100, true);
				Command c = CampaignMain.cm.getServerCommands().get("UNENROLL");
				c.process(new StringTokenizer("CONFIRMED", "#"), playerName);
				MWServ.mwlog.infoLog(playerName + " purged.");
			}
		}
	}

	/**
	 * @author Torren (Jason Tighe)
	 * @param money
	 * @param shortname
	 * @param amount
	 * @return String
	 * 
	 * Hokey function to return the correct syntax for long and short money/flu
	 * messages to the user.
	 */
	public String moneyOrFluMessage(boolean money, boolean shortname, int amount) {
		return moneyOrFluMessage(money, shortname, amount, false);
	}

	public String moneyOrFluMessage(boolean money, boolean shortname,
			int amount, boolean showSign) {
		String result = Integer.toString(amount);
		String moneyShort = cm.getConfig("MoneyShortName").toLowerCase();
		String moneyLong = cm.getConfig("MoneyLongName");
		String fluShort = cm.getConfig("FluShortName").toLowerCase();
		String fluLong = cm.getConfig("FluLongName");

		String sign = "+";

		if (amount < 0) {
			amount *= -1;
			sign = "-";
			result = Integer.toString(amount);
		}

		if (!shortname)
			result += " ";

		if (money) {
			if (shortname) {
				if (amount == 1 && moneyShort.endsWith("s"))
					result += moneyShort.substring(0, moneyShort.length() - 1);
				else if (amount > 1 && !moneyShort.endsWith("s"))
					result += moneyShort + "s";
				else
					result += moneyShort;
			}// end shortname if
			else {
				if (amount == 1 && moneyLong.endsWith("s"))
					result += moneyLong.substring(0, moneyLong.length() - 1);
				else if (amount > 1 && !moneyLong.endsWith("s"))
					result += moneyLong + "s";
				else
					result += moneyLong;
			}// end shortname else
		}// end money if
		else {
			if (shortname) {
				result += fluShort;
			}// end shortname if
			else {
				result += fluLong;
			}// end shortname else
		}// end money else

		// add sign, if set
		if (showSign)
			return sign + result;

		return result.trim();
	}

	public void updateISPLists(SPlayer player) {
		try {
			File file = new File("./data/Providers");
			if (!file.exists())
				file.mkdir();

			file = new File("./data/Providers/" + player.getLastISP() + ".prv");

			if (!file.exists()) {
				saveToISPLists(player);
				return;
			}

			FileInputStream in = new FileInputStream(file);
			BufferedReader buff = new BufferedReader(new InputStreamReader(in));

			while (buff.ready()) {
				String name = buff.readLine();

				if (name.equalsIgnoreCase(player.getName())) {
					buff.close();
					in.close();
					return;
				}
			}
			saveToISPLists(player);

		} catch (Exception ex) {
		}

	}

	public void saveToISPLists(SPlayer player) {
		try {
			FileOutputStream out = new FileOutputStream("./data/Providers/"
					+ player.getLastISP() + ".prv", true);
			PrintStream p = new PrintStream(out);
			p.println(player.getName());
			p.close();
			out.close();
		} catch (Exception ex) {
		}

	}

	public void loadOmniVariantMods() {
		try {
			File configFile = new File("./campaign/omnivariantmods.dat");
			FileInputStream fis = new FileInputStream(configFile);
			BufferedReader dis = new BufferedReader(new InputStreamReader(fis));
			while (dis.ready()) {
				StringTokenizer line = new StringTokenizer(dis.readLine(), "#");
				cm.getOmniVariantMods().put(line.nextToken(), line.nextToken());
			}
			dis.close();
			fis.close();
		} catch (Exception ex) {
		}
	}

	public void setArchiving(boolean archive) {
		this.isArchiving = archive;
	}

	public boolean isArchiving() {
		return this.isArchiving;
	}

	@SuppressWarnings("unchecked")
	public void saveConfigureFile(Properties config, String fileName) {

		try {
			PrintStream ps = new PrintStream(new FileOutputStream(fileName));
			ps.println("#Timestamp=" + System.currentTimeMillis());
			config.store(ps, "Server Config");
			ps.close();
		} catch (FileNotFoundException fe) {
			MWServ.mwlog.errLog(fileName + " not found");
		} catch (Exception ex) {
			MWServ.mwlog.errLog(ex);
		}
	}// end saveConfigureFile

	public UnitCosts getUnitCostLists() {
		return cm.unitCostLists;
	}

	public Client getMegaMekClient() {
		return megaMekClient;
	}

	public void setMegaMekClient(Client mmClient) {
		cm.megaMekClient = mmClient;
	}

	public RepairTrackingThread getRTT() {
		return RTT;
	}

	public int getTotalRepairCosts(Entity unit, Vector<Integer> techs,
			Vector<Integer> rolls, int pilotLevel, SHouse house) {
		double cost = 0;
		double totalArmorCost = 0;
		double internalCost = 0;
		double systemsCost = 0;
		double equipmentCost = 0;
		double weaponsCost = 0;
		double engineCost = 0;

		int techType = techs.elementAt(UnitUtils.ARMOR);
		int baseRoll = rolls.elementAt(UnitUtils.ARMOR);

		double pointsToRepair = 0;
		double armorCost = SUnit.getArmorCost(unit);
		double techCost = 0;
		double techWorkMod = 0;

		if (techType != UnitUtils.TECH_PILOT) {
			techCost = Integer.parseInt(cm.getConfig(UnitUtils
					.techDescription(techType)
					+ "TechRepairCost"));
			techWorkMod = UnitUtils.getTechRoll(unit, 0,
					UnitUtils.LOC_FRONT_ARMOR, techType, true, house
							.getTechLevel())
					- baseRoll;
		} else
			techType = pilotLevel;

		techWorkMod = Math.max(techWorkMod, 0);

		for (int location = 0; location < unit.locations(); location++) {
			if (unit.getArmor(location) < unit.getOArmor(location)) {
				pointsToRepair += unit.getOArmor(location)
						- unit.getArmor(location);
				totalArmorCost += armorCost * pointsToRepair;
				totalArmorCost += techCost * Math.abs(techWorkMod);
				totalArmorCost += techCost;
			}

			if (unit.hasRearArmor(location)) {
				pointsToRepair += unit.getOArmor(location, true)
						- unit.getArmor(location, true);
				totalArmorCost += armorCost * pointsToRepair;
				totalArmorCost += techCost * Math.abs(techWorkMod);
				totalArmorCost += techCost;
			}
		}

		// Base on what they assigned as the base roll we increase the payout so
		// that it covers the chances of failures. not the greatest but better
		// then nothing.
		totalArmorCost *= payOutIncreaseBasedOnRoll(baseRoll);
		totalArmorCost = Math.max(0, totalArmorCost);

		techType = techs.elementAt(UnitUtils.INTERNAL);
		baseRoll = rolls.elementAt(UnitUtils.INTERNAL);
		pointsToRepair = 0;
		armorCost = SUnit.getStructureCost(unit);
		techCost = 0;
		techWorkMod = 0;

		if (techType != UnitUtils.TECH_PILOT) {
			techCost = Integer.parseInt(cm.getConfig(UnitUtils
					.techDescription(techType)
					+ "TechRepairCost"));
		}

		for (int location = 0; location < unit.locations(); location++) {
			if (unit.getInternal(location) < unit.getOInternal(location)) {
				if (techType != UnitUtils.TECH_PILOT) {
					techWorkMod = UnitUtils.getTechRoll(unit, location,
							UnitUtils.LOC_INTERNAL_ARMOR, techType, true, house
									.getTechLevel())
							- baseRoll;
				}

				techWorkMod = Math.max(techWorkMod, 0);
				pointsToRepair = unit.getOInternal(location)
						- unit.getInternal(location);
				internalCost += armorCost * pointsToRepair;
				internalCost += techCost * Math.abs(techWorkMod);
				internalCost += techCost;
			}
		}

		// Base on what they assigned as the base roll we increase the payout so
		// that it covers the chances of failures. not the greatest but better
		// then nothing.
		internalCost *= payOutIncreaseBasedOnRoll(baseRoll);
		internalCost = Math.max(0, internalCost);

		techType = techs.elementAt(UnitUtils.SYSTEMS);
		baseRoll = rolls.elementAt(UnitUtils.SYSTEMS);
		pointsToRepair = 0;
		double critCost = 0;
		techCost = 0;
		techWorkMod = 0;

		if (techType != UnitUtils.TECH_PILOT) {
			techCost = Integer.parseInt(cm.getConfig(UnitUtils
					.techDescription(techType)
					+ "TechRepairCost"));
		}

		for (int location = 0; location < unit.locations(); location++) {
			for (int slot = 0; slot < unit.getNumberOfCriticals(location); slot++) {
				CriticalSlot cs = unit.getCritical(location, slot);
				if (cs == null)
					continue;
				if (!cs.isBreached() && !cs.isDamaged())
					continue;
				if (cs.getType() == CriticalSlot.TYPE_SYSTEM
						&& cs.getIndex() != Mech.SYSTEM_ENGINE) {
					if (techType != UnitUtils.TECH_PILOT) {
						techWorkMod = UnitUtils.getTechRoll(unit, location,
								slot, techType, true, house.getTechLevel())
								- baseRoll;
					}

					critCost = SUnit.getCritCost(unit, cs);
					techWorkMod = Math.max(techWorkMod, 0);
					pointsToRepair = UnitUtils.getNumberOfCrits(unit, cs);
					critCost += techCost;
					systemsCost += critCost * pointsToRepair;
					systemsCost += techCost * Math.abs(techWorkMod);
					systemsCost += techCost;

					// move the slot ahead if the Crit is more then 1 in size.
					slot += pointsToRepair - 1;
				}
			}
		}

		// Base on what they assigned as the base roll we increase the payout so
		// that it covers the chances of failures. not the greatest but better
		// then nothing.
		systemsCost *= payOutIncreaseBasedOnRoll(baseRoll);
		systemsCost = Math.max(0, systemsCost);

		techType = techs.elementAt(UnitUtils.WEAPONS);
		baseRoll = rolls.elementAt(UnitUtils.WEAPONS);
		pointsToRepair = 0;
		critCost = 0;
		techCost = 0;
		techWorkMod = 0;

		if (techType != UnitUtils.TECH_PILOT) {
			techCost = Integer.parseInt(cm.getConfig(UnitUtils
					.techDescription(techType)
					+ "TechRepairCost"));
		}

		for (int location = 0; location < unit.locations(); location++) {
			for (int slot = 0; slot < unit.getNumberOfCriticals(location); slot++) {
				CriticalSlot cs = unit.getCritical(location, slot);
				if (cs == null)
					continue;
				if (!cs.isBreached() && !cs.isDamaged())
					continue;
				if (cs.getType() == CriticalSlot.TYPE_EQUIPMENT) {
					Mounted mounted = unit.getEquipment(cs.getIndex());

					if (mounted.getType() instanceof WeaponType) {
						if (techType != UnitUtils.TECH_PILOT) {
							techWorkMod = UnitUtils.getTechRoll(unit, location,
									slot, techType, true, house.getTechLevel())
									- baseRoll;
						}

						critCost = SUnit.getCritCost(unit, cs);
						techWorkMod = Math.max(techWorkMod, 0);
						pointsToRepair = UnitUtils.getNumberOfCrits(unit, cs);
						critCost += techCost;
						weaponsCost += critCost * pointsToRepair;
						weaponsCost += techCost * Math.abs(techWorkMod);
						weaponsCost += techCost;

						// move the slot ahead if the Crit is more then 1 in
						// size.
						slot += pointsToRepair - 1;
					}
				}
			}
		}

		// Base on what they assigned as the base roll we increase the payout so
		// that it covers the chances of failures. not the greatest but better
		// then nothing.
		weaponsCost *= payOutIncreaseBasedOnRoll(baseRoll);
		weaponsCost = Math.max(0, weaponsCost);

		techType = techs.elementAt(UnitUtils.EQUIPMENT);
		baseRoll = rolls.elementAt(UnitUtils.EQUIPMENT);
		pointsToRepair = 0;
		critCost = 0;
		techCost = 0;
		techWorkMod = 0;

		if (techType != UnitUtils.TECH_PILOT) {
			techCost = Integer.parseInt(cm.getConfig(UnitUtils
					.techDescription(techType)
					+ "TechRepairCost"));
		}

		for (int location = 0; location < unit.locations(); location++) {
			for (int slot = 0; slot < unit.getNumberOfCriticals(location); slot++) {
				CriticalSlot cs = unit.getCritical(location, slot);
				if (cs == null)
					continue;
				if (!cs.isBreached() && !cs.isDamaged())
					continue;
				if (cs.getType() == CriticalSlot.TYPE_EQUIPMENT) {
					Mounted mounted = unit.getEquipment(cs.getIndex());

					if (!(mounted.getType() instanceof WeaponType)) {
						if (techType != UnitUtils.TECH_PILOT) {
							techWorkMod = UnitUtils.getTechRoll(unit, location,
									slot, techType, true, house.getTechLevel())
									- baseRoll;
						}

						critCost = SUnit.getCritCost(unit, cs);
						techWorkMod = Math.max(techWorkMod, 0);
						pointsToRepair = UnitUtils.getNumberOfCrits(unit, cs);
						critCost += techCost;
						equipmentCost += critCost * pointsToRepair;
						equipmentCost += techCost * Math.abs(techWorkMod);
						equipmentCost += techCost;
						// move the slot ahead if the Crit is more then 1 in
						// size.
						slot += pointsToRepair - 1;
					}
				}
			}
		}

		// Base on what they assigned as the base roll we increase the payout so
		// that it covers the chances of failures. not the greatest but better
		// then nothing.
		equipmentCost *= payOutIncreaseBasedOnRoll(baseRoll);
		equipmentCost = Math.max(0, equipmentCost);

		techType = techs.elementAt(UnitUtils.ENGINES);
		baseRoll = rolls.elementAt(UnitUtils.ENGINES);
		pointsToRepair = 0;
		critCost = 0;
		techCost = 0;
		techWorkMod = 0;

		boolean found = false;
		int location = 0, slot = 0;
		CriticalSlot cs = null;

		if (techType != UnitUtils.TECH_PILOT) {
			techCost = Integer.parseInt(cm.getConfig(UnitUtils
					.techDescription(techType)
					+ "TechRepairCost"));
		}

		for (int x = UnitUtils.LOC_CT; x <= UnitUtils.LOC_LT; x++) {
			for (int y = 0; y < unit.getNumberOfCriticals(x); y++) {
				cs = unit.getCritical(x, y);

				if (cs == null)
					continue;

				if (!cs.isDamaged() && !cs.isBreached())
					continue;

				if (!UnitUtils.isEngineCrit(cs))
					continue;

				location = x;
				slot = y;
				found = true;
				break;

			}
			if (found)
				break;
		}

		if (techType != UnitUtils.TECH_PILOT) {
			techWorkMod = UnitUtils.getTechRoll(unit, location, slot, techType,
					true, house.getTechLevel())
					- baseRoll;
		}

		critCost = SUnit.getCritCost(unit, cs);
		techWorkMod = Math.max(techWorkMod, 0);
		pointsToRepair = UnitUtils.getNumberOfCrits(unit, cs);
		critCost += techCost;
		engineCost += critCost * pointsToRepair;
		engineCost += techCost * Math.abs(techWorkMod);
		engineCost += techCost;

		// Base on what they assigned as the base roll we increase the payout so
		// that it covers the chances of failures. not the greatest but better
		// then nothing.
		engineCost *= payOutIncreaseBasedOnRoll(baseRoll);
		engineCost = Math.max(0, engineCost);

		if (!found)
			engineCost = 0;

		cost = totalArmorCost + engineCost + systemsCost + internalCost
				+ weaponsCost + equipmentCost;
		return (int) cost;
	}

	private double payOutIncreaseBasedOnRoll(int roll) {
		if (roll <= 2) {
			return 1.0;
		} else if (roll > 12) {
			return 36.0;
		}
		final double[] payout = { 1.0, 1.0, 1.0, 1.03, 1.09, 1.20, 1.38, 1.72,
				2.40, 3.60, 5.92, 12.0, 36.0 };
		return payout[roll];
	}

	public int getRepairCost(Entity unit, int critLocation, int critSlot,
			int techType, boolean armor, int techWorkMod) {
		return getRepairCost(unit, critLocation, critSlot, techType, armor,
				techWorkMod, false);
	}

	public int getRepairCost(Entity unit, int critLocation, int critSlot,
			int techType, boolean armor, int techWorkMod, boolean salvage) {
		double totalCost = 1;
		double techCost = 0;
		double cost = 1;
		int totalCrits = 1;

		if (techType < UnitUtils.TECH_PILOT)
			techCost = CampaignMain.cm.getIntegerConfig(UnitUtils
					.techDescription(techType)
					+ "TechRepairCost");

		if (Boolean.parseBoolean(cm.getConfig("UseRealRepairCosts"))) {
			double realCost = UnitUtils.getPartCost(unit, critLocation,
					critSlot, armor);
			if (Boolean.parseBoolean(cm.getConfig("UsePartsRepair")))
				realCost = 0;

			double costMod = Double.parseDouble(cm
					.getConfig("RealRepairCostMod"));
			// modify the cost
			if (costMod > 0)
				realCost *= costMod;

			cost += (techCost * Math.abs(techWorkMod)) + techCost;
		} else {
			if (armor) {
				if (critSlot == UnitUtils.LOC_FRONT_ARMOR) {
					cost = SUnit.getArmorCost(unit);
					if (unit.getArmor(critLocation) > unit
							.getOArmor(critLocation)) {
						// remove the repairing armor so we can get the real
						// cost.
						UnitUtils.removeArmorRepair(unit,
								UnitUtils.LOC_FRONT_ARMOR, critLocation);
						cost *= unit.getOArmor(critLocation)
								- unit.getArmor(critLocation);
						// Add the repairing armor flag back on.
						UnitUtils.setArmorRepair(unit,
								UnitUtils.LOC_FRONT_ARMOR, critLocation);
					} else
						cost *= unit.getOArmor(critLocation)
								- unit.getArmor(critLocation);

					cost += techCost * Math.abs(techWorkMod);
					cost += techCost;
					cost = Math.max(1, cost);
				} else if (critSlot == UnitUtils.LOC_REAR_ARMOR) {
					// tell the repair command its using rear external armor
					cost = SUnit.getArmorCost(unit);
					if (critLocation >= UnitUtils.LOC_CTR)
						critLocation -= 7;
					if (unit.getArmor(critLocation, true) > unit.getOArmor(
							critLocation, true)) {
						// remove the repairing armor so we can get the real
						// cost.
						UnitUtils.removeArmorRepair(unit,
								UnitUtils.LOC_REAR_ARMOR, critLocation);
						cost *= unit.getOArmor(critLocation, true)
								- unit.getArmor(critLocation, true);
						// Add the repairing armor flag back on.
						UnitUtils.setArmorRepair(unit,
								UnitUtils.LOC_REAR_ARMOR, critLocation);
					} else
						cost *= unit.getOArmor(critLocation, true)
								- unit.getArmor(critLocation, true);

					cost += techCost * Math.abs(techWorkMod);
					cost += techCost;
					cost = Math.max(1, cost);
				} else {
					cost = SUnit.getStructureCost(unit);
					if (unit.getInternal(critLocation) > unit
							.getOInternal(critLocation)) {
						// remove the repairing armor so we can get the real
						// cost.
						UnitUtils.removeArmorRepair(unit,
								UnitUtils.LOC_INTERNAL_ARMOR, critLocation);
						cost *= unit.getOInternal(critLocation)
								- unit.getInternal(critLocation);
						// Add the repairing armor flag back on.
						UnitUtils.setArmorRepair(unit,
								UnitUtils.LOC_INTERNAL_ARMOR, critLocation);
					} else
						cost *= unit.getOInternal(critLocation)
								- unit.getInternal(critLocation);

					cost += techCost * Math.abs(techWorkMod);
					cost += techCost;
					cost = Math.max(1, cost);
				}
			} else {
				CriticalSlot cs = unit.getCritical(critLocation, critSlot);
				if (salvage)
					totalCrits = UnitUtils.getNumberOfCrits(unit, cs)
							- UnitUtils.getNumberOfDamagedCrits(unit, critSlot,
									critLocation, armor);
				else
					totalCrits = UnitUtils.getNumberOfDamagedCrits(unit,
							critSlot, critLocation, armor);
				cost = SUnit.getCritCost(unit, cs);
				totalCost = (int) (totalCrits * cost);
				totalCost += (int) (totalCrits * techCost);
				totalCost += techCost;
				totalCost += techCost * Math.abs(techWorkMod);
				cost = Math.max(1, totalCost);
			}// end critslot else
		}

		if (Boolean.parseBoolean(cm.getConfig("AllowCritRepairsForRewards"))
				&& techType == UnitUtils.TECH_REWARD_POINTS) {
			cost = totalCrits
					* Double.parseDouble(cm
							.getConfig("RewardPointsForCritRepair"));
			cost = Math.max(Math.ceil(cost), 1);
		}

		return (int) cost;
	}

	public void saveBannedAmmo() {

		// Save banned ammo
		try {
			FileOutputStream out = new FileOutputStream(
					"./campaign/banammo.dat");
			PrintStream p = new PrintStream(out);

			// server banned ammo
			p.println(System.currentTimeMillis());
			p.print("server#");
			for (String ammo : CampaignMain.cm.getServerBannedAmmo().keySet()) {
				p.print(ammo);
				p.print("#");
			}
			p.println();

			// faction banned ammo
			for (House currH : data.getAllHouses()) {

				SHouse h = (SHouse) currH;
				if (h.getBannedAmmo().size() < 1)
					continue;

				p.print(h.getName() + "#");
				for (String ammo : h.getBannedAmmo().keySet()) {
					p.print(ammo);
					p.print("#");
				}
				p.println();

			}
			p.close();
			out.close();

		} catch (Exception ex) {
			MWServ.mwlog.errLog("Error saving banned ammo.");
			MWServ.mwlog.errLog(ex);
		}
	}

	public void saveAmmoCosts() {
		// Save ammo costs
		try {
			FileOutputStream out = new FileOutputStream(
					"./campaign/ammocosts.dat");
			PrintStream p = new PrintStream(out);
			p.println(System.currentTimeMillis());
			for (long ammo : this.getAmmoCost().keySet()) {
				int cost = this.getAmmoCost().get(ammo);
				p.print(ammo);
				p.print("#");
				p.print(cost);
				p.print("#");
			}
			p.close();
			out.close();
		} catch (Exception ex) {
			MWServ.mwlog.errLog("Error saving ammo costs.");
			MWServ.mwlog.errLog(ex);
		}
	}

	public void loadBannedTargetingSystems() {

		try {

			File configFile = new File("./campaign/bantargeting.dat");
			FileInputStream fis = new FileInputStream(configFile);
			BufferedReader dis = new BufferedReader(new InputStreamReader(fis));

			dis.readLine();// Time Stamp
			StringTokenizer st = new StringTokenizer(dis.readLine(), "#");
			while (st.hasMoreTokens())
				cm.getData().getBannedTargetingSystems().put(
						Integer.parseInt(st.nextToken()), "Banned");

			dis.close();
			fis.close();

		} catch (Exception ex) {
			saveBannedTargetingSystems();
		}
	}

	public void saveBannedTargetingSystems() {
		// Save banned targeting systems
		try {
			FileOutputStream out = new FileOutputStream(
					"./campaign/bantargeting.dat");
			PrintStream p = new PrintStream(out);
			p.println(System.currentTimeMillis());
			for (Integer targetingSytem : CampaignMain.cm.getData()
					.getBannedTargetingSystems().keySet()) {
				p.print(targetingSytem);
				p.print("#");
			}
			p.close();
			out.close();
		} catch (Exception ex) {
			MWServ.mwlog.errLog("Error saving banned targetting systems.");
			MWServ.mwlog.errLog(ex);
		}
	}

	public void loadPlanetOpFlags() {

		File configFile = new File("./campaign/planetOpFlags.dat");
		if (!configFile.exists()) {
			MWServ.mwlog.errLog("No planetOpFlags.dat. Skipping.");
			return;
		}

		try {

			FileInputStream fis = new FileInputStream(configFile);
			BufferedReader dis = new BufferedReader(new InputStreamReader(fis));

			dis.readLine();// Time Stamp

			String nextLine = dis.readLine();
			if (nextLine == null) {
				MWServ.mwlog
						.errLog("Timestamp-only planetOpFlags.dat. Skipping.");
				return;
			}

			StringTokenizer st = new StringTokenizer(nextLine, "#");
			while (st.hasMoreTokens())
				data.getPlanetOpFlags().put(st.nextToken(), st.nextToken());

			dis.close();
			fis.close();

		} catch (Exception ex) {
			MWServ.mwlog.errLog("Error loading Planet Op Flags.");
			MWServ.mwlog.errLog(ex);
		}
	}

	public void savePlanetOpFlags() {
		// Save Planet Op Flags
		try {
			FileOutputStream out = new FileOutputStream(
					"./campaign/planetOpFlags.dat");
			PrintStream p = new PrintStream(out);
			p.println(System.currentTimeMillis());
			for (String key : CampaignMain.cm.getData().getPlanetOpFlags()
					.keySet()) {
				p.print(key);
				p.print("#");
				p.print(CampaignMain.cm.getData().getPlanetOpFlags().get(key));
				p.print("#");
			}
			p.close();
			out.close();
		} catch (Exception ex) {
			MWServ.mwlog.errLog("Error saving Planet Op Flags.");
			MWServ.mwlog.errLog(ex);
		}
	}

	public void loadFactionData() {
		if(CampaignMain.cm.isUsingMySQL()) {
			CampaignMain.cm.MySQL.loadFactions(data);
			return;
		}
		File factionFile = new File("./campaign/factions");

		// Check for new faction save location
		if (!factionFile.exists() || factionFile.listFiles().length < 1) {
			MWServ.mwlog.errLog("Unable to find and load faction data");
			MWServ.mwlog.errLog("Going to create from XML");
			return;
		}

		// filter out .bak's
		FilenameFilter filter = new datFileFilter();
		File[] factionFileList = factionFile.listFiles(filter);

		// load each file
		for (int pos = 0; pos < factionFileList.length; pos++) {
			try {
				File faction = factionFileList[pos];
				FileInputStream fis = new FileInputStream(faction);
				BufferedReader dis = new BufferedReader(new InputStreamReader(
						fis));
				String line = dis.readLine();
				SHouse h;
				if (line.startsWith("[N][C]")) {
					line = line.substring(6);
					h = new NewbieHouse(data.getUnusedHouseID());
				} else if (line.startsWith("[N]")) {
					line = line.substring(3);
					h = new NewbieHouse(data.getUnusedHouseID());
				} else if (line.startsWith("[M]")) {
					line = line.substring(3);
					h = new MercHouse(data.getUnusedHouseID());
				} else
					h = new SHouse(data.getUnusedHouseID());
				h.fromString(line, r);
				if(isUsingIncreasedTechs())
					h.addCommonUnitSupport();
				addHouse(h);
				dis.close();
				fis.close();
			} catch (Exception ex) {
				MWServ.mwlog.errLog("Unable to load "
						+ factionFileList[pos].getName());
			}
		}

		// load the various construction modifiers for the houses added above
		factionFile = new File("./campaign/costmodifiers");
		if (!factionFile.exists())
			return;// done

		for (House currH : data.getAllHouses()) {

			String saveName = currH.getName().toLowerCase().trim() + ".dat";
			File faction = new File("./campaign/costmodifiers/" + saveName);

			if (!faction.exists())
				continue;
			try {

				FileInputStream fis = new FileInputStream(faction);
				BufferedReader dis = new BufferedReader(new InputStreamReader(
						fis));

				String currLine = null;
				while ((currLine = dis.readLine()) != null) {

					StringTokenizer tokenizer = new StringTokenizer(currLine,
							"$");
					String cost = tokenizer.nextToken();
					int type = Integer.parseInt(tokenizer.nextToken());
					int weight = Integer.parseInt(tokenizer.nextToken());
					int mod = Integer.parseInt(tokenizer.nextToken());

					if (cost.equals("Price")) {
						currH.setHouseUnitPriceMod(type, weight, mod);
					} else if (cost.equals("Flu")) {
						currH.setHouseUnitFluMod(type, weight, mod);
					} else if (cost.equals("Comp")) {
						currH.setHouseUnitComponentMod(type, weight, mod);
					}

				}

			} catch (Exception e) {
				MWServ.mwlog.errLog("Unable to load cost modifiers for "
						+ currH.getName());
			}

		}

	}

	// Save Houses
	public void saveFactionData() {

		if(CampaignMain.cm.isUsingMySQL()) {
			for (House currH : data.getAllHouses()) {
				SHouse h = (SHouse) currH;
				h.toDB();
				
				// For right now, we're going to save units to the faction file
				// I can save them fine to the database, but they're not loading.
				
			}
			return;
		}
		
		// Standard faction saves
		File factionFile = new File("./campaign/factions");
		if (!factionFile.exists()) {
			factionFile.mkdir();
			if(isUsingIncreasedTechs()){
				File supportFile = new File("./campaign/factions/support");
				supportFile.mkdir();
			}
		}

		synchronized (data.getAllHouses()) {
			for (House currH : data.getAllHouses()) {
				SHouse h = (SHouse) currH;

				String saveName = h.getName().toLowerCase().trim() + ".dat";
				String backupName = h.getName().toLowerCase().trim() + ".bak";

				// standard save
				try {

					File faction = new File("./campaign/factions/" + saveName);

					if (faction.exists()) {

						File backupFile = new File("./campaign/factions/"
								+ backupName);
						if (backupFile.exists())
							backupFile.delete();

						faction.renameTo(backupFile);
					}

					FileOutputStream out = new FileOutputStream(
							"./campaign/factions/" + saveName);
					PrintStream p = new PrintStream(out);

					p.println(h.toString());
					p.close();
					out.close();
				} catch (Exception ex) {
					MWServ.mwlog.errLog("Unable to save Faction: " + saveName);
					MWServ.mwlog.errLog(ex);
				}
			}
		}

		synchronized (data.getAllHouses()) {
			// Cost modifier saves TODO: move these values into normal save
			// stream
			factionFile = new File("./campaign/costmodifiers");
			if (!factionFile.exists())
				factionFile.mkdir();

			for (House currH : data.getAllHouses()) {

				SHouse h = (SHouse) currH;
				String saveName = h.getName().toLowerCase().trim() + ".dat";
				String backupName = h.getName().toLowerCase().trim() + ".bak";

				// standard save
				try {

					File faction = new File("./campaign/costmodifiers/"
							+ saveName);

					if (faction.exists()) {

						File backupFile = new File("./campaign/costmodifiers/"
								+ backupName);
						if (backupFile.exists())
							backupFile.delete();

						faction.renameTo(backupFile);
					}

					FileOutputStream out = new FileOutputStream(
							"./campaign/costmodifiers/" + saveName);
					PrintStream p = new PrintStream(out);

					for (int type = 0; type < 5; type++) {
						for (int weight = 0; weight < 4; weight++) {

							if (h.getHouseUnitPriceMod(type, weight) != 0)
								p.println("Price$" + type + "$" + weight + "$"
										+ h.getHouseUnitPriceMod(type, weight));
							if (h.getHouseUnitFluMod(type, weight) != 0)
								p.println("Flu$" + type + "$" + weight + "$"
										+ h.getHouseUnitFluMod(type, weight));
							if (h.getHouseUnitComponentMod(type, weight) != 0)
								p.println("Comp$"
										+ type
										+ "$"
										+ weight
										+ "$"
										+ h.getHouseUnitComponentMod(type,
												weight));

						}
					}

					p.close();
					out.close();

				} catch (Exception ex) {
					MWServ.mwlog
							.errLog("Unable to save Faction Modifiers For: "
									+ saveName);
					MWServ.mwlog.errLog(ex);
				}
			}
		}
	}

	public void loadPlanetData() {

		loadPlanetOpFlags();

		File planetFile = new File("./campaign/planets");
		FilenameFilter filter = new datFileFilter();

		// Check for faction save dir & ensure dat files exist therein
		if (!CampaignMain.cm.isUsingMySQL()
				&& (!planetFile.exists() || planetFile.listFiles(filter).length == 0)) {
			MWServ.mwlog
					.errLog("Unable to find and load /planets, or /planets is empty.");
			MWServ.mwlog.errLog("Planets will be read from XML during init().");
			return;
		} else if (CampaignMain.cm.isUsingMySQL()
				&& CampaignMain.cm.MySQL.countPlanets() == 0) {
			MWServ.mwlog.errLog("Empty planet database.");
			MWServ.mwlog.errLog("Planets will be read from XML during init().");
			return;
		}
		// If we're using the database, load the planets here.

		if (CampaignMain.cm.isUsingMySQL()) {
			CampaignMain.cm.MySQL.loadPlanets(data);
		} else {

			// dir and files exist. read them.
			File[] planetFileList = planetFile.listFiles(filter);
			for (int pos = 0; pos < planetFileList.length; pos++) {

				try {
					File planet = planetFileList[pos];
					FileInputStream fis = new FileInputStream(planet);
					BufferedReader dis = new BufferedReader(
							new InputStreamReader(fis));
					String line = dis.readLine();
					SPlanet p;
					if (line.startsWith("[N]"))
						line = line.substring(3);
					p = new SPlanet();
					p.fromString(line, r, data);
					addPlanet(p);
					dis.close();
					fis.close();
				} catch (Exception ex) {
					MWServ.mwlog.errLog("Unable to load "
							+ planetFileList[pos].getName());
					MWServ.mwlog.errLog(ex);
				}
			}
		}
	}

	private void loadMechStatsFromDB() {
		try {
			Statement stmt = MySQL.getStatement();
			ResultSet rs = stmt.executeQuery("SELECT ID from mechstats ORDER BY ID");
			while(rs.next()) {
				MechStatistics m = new MechStatistics(rs.getInt("ID"));
				MechStats.put(m.getMechFileName(), m);
			}
			rs.close();
			stmt.close();
		} catch(SQLException e) {
			MWServ.mwlog.dbLog("SQL Error in UnitHandler.loadMechStats: " + e.getMessage());
		}		
	}
	
	public void updatePlayersAccessLevel(String playerName, int accessLevel){
		SPlayer player =  cm.getPlayer(playerName);
		
		if ( player == null )
			return;
		try{
			cm.getServer().getClient(playerName).setAccessLevel(accessLevel);
		    cm.getServer().getUser(playerName).setLevel(accessLevel);
		    cm.getServer().sendRemoveUserToAll(playerName,false);
		    cm.getServer().sendNewUserToAll(playerName,false);
			MWPasswd.writeRecord(player.getPassword(),playerName);
		   	cm.doSendToAllOnlinePlayers("PI|DA|" + cm.getPlayerUpdateString(player),false);
		}catch (Exception ex){}
       	player.setSave();
	}

	/**
	 * this removes a SPlayer object form the global hash. This is called when a player
	 * logs into a house, in which case the house now stores the object, or when the 
	 * player logs off, incase they never bothred to register or login.
	 * @param soul
	 */
	public void releaseLostSoul(String soul){
		cm.lostSouls.remove(soul.toLowerCase());
	}

	// Save Planets
	public void savePlanetData() {

		if (cm.isUsingMySQL())
			for (Planet currP : data.getAllPlanets()){
				SPlanet p = (SPlanet) currP;
				p.toDB();
			}

		else {
			savePlanetOpFlags();
			File planetFile = new File("./campaign/planets");
			if (!planetFile.exists())
				planetFile.mkdir();
			synchronized (data.getAllPlanets()) {

				for (Planet currP : data.getAllPlanets()) {
					SPlanet p = (SPlanet) currP;
					String saveName = p.getName().toLowerCase().trim() + ".dat";
					String backupName = p.getName().toLowerCase().trim()
							+ ".bak";
					try {
						File planet = new File("./campaign/planets/" + saveName);

						if (planet.exists()) {

							File backupFile = new File("./campaign/planets/"
									+ backupName);
							if (backupFile.exists())
								backupFile.delete();

							planet.renameTo(backupFile);
						}

						FileOutputStream out = new FileOutputStream(
								"./campaign/planets/" + saveName);
						PrintStream ps = new PrintStream(out);
						ps.println(p.toString());
						ps.close();
						out.close();
					} catch (Exception ex) {
						MWServ.mwlog.errLog("Unable to save planet: "
								+ saveName);
						MWServ.mwlog.errLog(ex);
					}
				}
			}

			/*
			 * All servers, that I'm aware of, have begun using versions of the
			 * software that save to unique planet.dats. The old-file check is
			 * probably not necessary at this point.
			 * 
			 * TODO: Remove this codeblock when we know for sure it's not
			 * needed.
			 */
			planetFile = new File("./campaign/planets.dat");
			if (planetFile.exists())
				planetFile.delete();
			planetFile = new File("./campaign/planets.bak");
			if (planetFile.exists())
				planetFile.delete();

		}
	}

	class datFileFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return (name.endsWith(".dat"));
		}
	}
}