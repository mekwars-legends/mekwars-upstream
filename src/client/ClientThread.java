/*
 * MekWars - Copyright (C) 2004 
 * 
 * Derived from MegaMekNET (http://www.sourceforge.net/projects/megameknet)
 * Original author Helge Richter (McWizard)
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

package client;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.Vector;

import megamek.client.Client;
import megamek.client.CloseClientListener;
import megamek.client.bot.BotClient;
import megamek.client.bot.TestBot;
import megamek.client.bot.ui.AWT.BotGUI;
import megamek.client.ui.AWT.ClientGUI;
import megamek.common.Board;
import megamek.common.Coords;
import megamek.common.Entity;
import megamek.common.IGame;
import megamek.common.IOffBoardDirections;
import megamek.common.MapSettings;
import megamek.common.Pilot;
import megamek.common.PlanetaryConditions;
import megamek.common.Player;
import megamek.common.options.IBasicOption;
import megamek.common.preference.IClientPreferences;
import megamek.common.preference.PreferenceManager;
import megamek.common.util.BuildingTemplate;
import client.campaign.CArmy;
import client.campaign.CUnit;

import common.AdvancedTerrain;
import common.CampaignData;
import common.PlanetEnvironment;
import common.Unit;
import common.campaign.Buildings;
import common.util.UnitUtils;

class ClientThread extends Thread implements CloseClientListener {

	// VARIABLES
	private String myname;
	private String serverip;
	private String serverName;
	private int serverport;
	private MWClient mwclient;
	private Client client;
	private ClientGUI awtGui;
	private megamek.client.ui.swing.ClientGUI swingGui;
	private boolean awtGUI = false;

	private ArrayList<Unit> mechs = new ArrayList<Unit>();
	private ArrayList<CUnit> autoarmy = new ArrayList<CUnit>();// from server's
	// auto army
	CArmy army = null;
	BotClient bot = null;

	final int N = 0;
	final int NE = 1;
	final int SE = 2;
	final int S = 3;
	final int SW = 4;
	final int NW = 5;

	// CONSTRUCTOR
	public ClientThread(String name, String servername, String ip, int port, MWClient mwclient, ArrayList<Unit> mechs, ArrayList<CUnit> autoarmy) {
		myname = name.trim();
		serverName = servername;
		serverip = ip;
		serverport = port;
		this.mwclient = mwclient;
		this.mechs = mechs;
		this.autoarmy = autoarmy;
		if (serverip.indexOf("127.0.0.1") != -1) {
			serverip = "127.0.0.1";
		}
	}

	public Client getClient() {
		return client;
	}

	@Override
	public void run() {
		boolean playerUpdate = false;
		boolean nightGame = false;
		awtGUI = mwclient.getConfig().isParam("USEAWTINTERFACE");
		CArmy currA = mwclient.getPlayer().getLockedArmy();
		client = new Client(myname, serverip, serverport);
		client.addCloseClientListener(this);
		mwclient.getserverConfigs("MMTimeStampLogFile");
		mwclient.getserverConfigs("MMShowUnitId");
		mwclient.getserverConfigs("MMKeepGameLog");
		mwclient.getserverConfigs("MMGameLogName");

		try {

			// clear out everything.
			mwclient.getPlayer().setConventionalMinesAllowed(0);
			mwclient.getPlayer().setVibraMinesAllowed(0);
			mwclient.setUsingBots(false);
			// clear out everything from this game
			mwclient.setEnvironment(null, null, 0);
			mwclient.setAdvancedTerrain(null);
			mwclient.setPlayerStartingEdge(Buildings.EDGE_UNKNOWN);
			mwclient.getGameOptions().clear();
			// get rid of any and all bots.

		}// end try
		catch (Exception ex) {
			CampaignData.mwlog.errLog("Error reporting game!");
			CampaignData.mwlog.errLog(ex);
		}

		if (awtGUI) {
			if (awtGui != null) {
				for (Client client2 : awtGui.getBots().values()) {
					client2.die();
				}
				awtGui.getBots().clear();
			}
			awtGui = new ClientGUI(client);
			awtGui.initialize();
			swingGui = null;
		} else {
			if (swingGui != null) {
				for (Client client2 : swingGui.getBots().values()) {
					client2.die();
				}
				swingGui.getBots().clear();
			}
			awtGui = null;
			swingGui = new megamek.client.ui.swing.ClientGUI(client);
			swingGui.initialize();
		}

		if (mwclient.getGameOptions().size() < 1) {
			mwclient.setWaiting(true);

			mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c RequestOperationSettings");
			while (mwclient.isWaiting()) {
				try {
					mwclient.addToChat("Retrieving Operation Data Please Wait..");
					Thread.sleep(1000);
				} catch (Exception ex) {

				}
			}
		}

		// client.game.getOptions().
		Vector<IBasicOption> xmlGameOptions = mwclient.getGameOptions();

		try {
			client.connect();
		} catch (Exception ex) {
			client = null;
			mwclient.showInfoWindow("Couldn't join this game!");
			CampaignData.mwlog.infoLog(serverip + " " + serverport);
			return;
		}
		// client.retrieveServerInfo();
		try {
			while (client.getLocalPlayer() == null) {
				Thread.sleep(50);
			}

			// if game is running, shouldn't do the following, so detect the
			// phase
			for (int i = 0; i < 1000 && client.game.getPhase() == IGame.Phase.PHASE_UNKNOWN; i++) {
				Thread.sleep(50);
			}

			// Lets start with the environment set first then do everything
			// else.
			if (mwclient.getCurrentEnvironment() != null && client.game.getPhase() == IGame.Phase.PHASE_LOUNGE) {
				// creates the playboard*/
				MapSettings mySettings = new MapSettings(mwclient.getMapSize().width, mwclient.getMapSize().height, 1, 1);
				// MapSettings mySettings = new MapSettings(16, 17, 2, 2);
				AdvancedTerrain aTerrain = mwclient.getCurrentAdvancedTerrain();

				if ((aTerrain != null) && aTerrain.isStaticMap()) {

					mySettings = new MapSettings(aTerrain.getXSize(), aTerrain.getYSize(), aTerrain.getXBoardSize(), aTerrain.getYBoardSize());

					// MMClient.mwClientLog.clientErrLog("Board x:
					// "+myClient.getBoardSize().width+"Board y:
					// "+myClient.getBoardSize().height+"Map x:
					// "+myClient.getMapSize().width+"Map y:
					// "+myClient.getMapSize().height);
					ArrayList<String> boardvec = new ArrayList<String>();
					if (aTerrain.getStaticMapName().toLowerCase().endsWith("surprise")) {
						int maxBoards = aTerrain.getXBoardSize() * aTerrain.getYBoardSize();
						for (int i = 0; i < maxBoards; i++) {
							boardvec.add(MapSettings.BOARD_SURPRISE);
						}

						mySettings.setBoardsSelectedVector(boardvec);

						if (aTerrain.getStaticMapName().indexOf("/") > -1) {
							String folder = aTerrain.getStaticMapName().substring(0, aTerrain.getStaticMapName().lastIndexOf("/"));
							mySettings.setBoardsAvailableVector(scanForBoards(aTerrain.getXSize(), aTerrain.getYSize(), folder));
						} else if (aTerrain.getStaticMapName().indexOf("\\") > -1) {
							String folder = aTerrain.getStaticMapName().substring(0, aTerrain.getStaticMapName().lastIndexOf("\\"));
							mySettings.setBoardsAvailableVector(scanForBoards(aTerrain.getXSize(), aTerrain.getYSize(), folder));
						} else {
							mySettings.setBoardsAvailableVector(scanForBoards(aTerrain.getXSize(), aTerrain.getYSize(), ""));
						}
					} else if (aTerrain.getStaticMapName().toLowerCase().endsWith("generated")) {
						PlanetEnvironment env = mwclient.getCurrentEnvironment();
						/* Set the map-gen values */
						mySettings.setElevationParams(env.getHillyness(), env.getHillElevationRange(), env.getHillInvertProb());
						mySettings.setWaterParams(env.getWaterMinSpots(), env.getWaterMaxSpots(), env.getWaterMinHexes(), env.getWaterMaxHexes(), env.getWaterDeepProb());
						mySettings.setForestParams(env.getForestMinSpots(), env.getForestMaxSpots(), env.getForestMinHexes(), env.getForestMaxHexes(), env.getForestHeavyProb());
						mySettings.setRoughParams(env.getRoughMinSpots(), env.getRoughMaxSpots(), env.getRoughMinHexes(), env.getRoughMaxHexes());
						mySettings.setSwampParams(env.getSwampMinSpots(), env.getSwampMaxSpots(), env.getSwampMinHexes(), env.getSwampMaxHexes());
						mySettings.setPavementParams(env.getPavementMinSpots(), env.getPavementMaxSpots(), env.getPavementMinHexes(), env.getPavementMaxHexes());
						mySettings.setIceParams(env.getIceMinSpots(), env.getIceMaxSpots(), env.getIceMinHexes(), env.getIceMaxHexes());
						mySettings.setRubbleParams(env.getRubbleMinSpots(), env.getRubbleMaxSpots(), env.getRubbleMinHexes(), env.getRubbleMaxHexes());
						mySettings.setFortifiedParams(env.getFortifiedMinSpots(), env.getFortifiedMaxSpots(), env.getFortifiedMinHexes(), env.getFortifiedMaxHexes());
						mySettings.setSpecialFX(env.getFxMod(), env.getProbForestFire(), env.getProbFreeze(), env.getProbFlood(), env.getProbDrought());
						mySettings.setRiverParam(env.getRiverProb());
						mySettings.setCliffParam(env.getCliffProb());
						mySettings.setRoadParam(env.getRoadProb());
						mySettings.setCraterParam(env.getCraterProb(), env.getCraterMinNum(), env.getCraterMaxNum(), env.getCraterMinRadius(), env.getCraterMaxRadius());
						mySettings.setAlgorithmToUse(env.getAlgorithm());
						mySettings.setInvertNegativeTerrain(env.getInvertNegativeTerrain());
						mySettings.setMountainParams(env.getMountPeaks(), env.getMountWidthMin(), env.getMountWidthMax(), env.getMountHeightMin(), env.getMountHeightMax(), env.getMountStyle());

						if (env.getTheme().length() > 1) {
							mySettings.setTheme(env.getTheme());
						} else {
							mySettings.setTheme("");
						}

						int maxBoards = aTerrain.getXBoardSize() * aTerrain.getYBoardSize();
						for (int i = 0; i < maxBoards; i++) {
							boardvec.add(MapSettings.BOARD_GENERATED);
						}

						mySettings.setBoardsSelectedVector(boardvec);
						if (aTerrain.getStaticMapName().indexOf("/") > -1) {
							String folder = aTerrain.getStaticMapName().substring(0, aTerrain.getStaticMapName().lastIndexOf("/"));
							mySettings.setBoardsAvailableVector(scanForBoards(aTerrain.getXSize(), aTerrain.getYSize(), folder));
						} else if (aTerrain.getStaticMapName().indexOf("\\") > -1) {
							String folder = aTerrain.getStaticMapName().substring(0, aTerrain.getStaticMapName().lastIndexOf("\\"));
							mySettings.setBoardsAvailableVector(scanForBoards(aTerrain.getXSize(), aTerrain.getYSize(), folder));
						} else {
							mySettings.setBoardsAvailableVector(scanForBoards(aTerrain.getXSize(), aTerrain.getYSize(), ""));
						}

						if (mwclient.getBuildingTemplate() != null && mwclient.getBuildingTemplate().getTotalBuildings() > 0) {
							ArrayList<BuildingTemplate> buildingList = generateRandomBuildings(mySettings, mwclient.getBuildingTemplate());
							mySettings.setBoardBuildings(buildingList);
						} else if (!env.getCityType().equalsIgnoreCase("NONE")) {
							mySettings.setRoadParam(0);
							mySettings.setCityParams(env.getRoads(), env.getCityType(), env.getMinCF(), env.getMaxCF(), env.getMinFloors(), env.getMaxFloors(), env.getCityDensity(), env.getTownSize());
						}
					} else {
						boardvec.add(aTerrain.getStaticMapName());
						mySettings.setBoardsSelectedVector(boardvec);
					}

					PlanetaryConditions planetCondition = new PlanetaryConditions();

					planetCondition.setGravity((float) aTerrain.getGravity());
					planetCondition.setTemperature(aTerrain.getTemperature());
					planetCondition.setAtmosphere(aTerrain.getAtmosphere());
					planetCondition.setEMI(aTerrain.hasEMI());
					planetCondition.setFog(aTerrain.getFog());
					planetCondition.setLight(aTerrain.getLightConditions());
					planetCondition.setShiftingWindDirection(aTerrain.hasShifitingWindDirection());
					planetCondition.setShiftingWindStrength(aTerrain.hasShifitingWindStrength());
					planetCondition.setTerrainAffected(aTerrain.isTerrainAffected());
					planetCondition.setWeather(aTerrain.getWeatherConditions());
					planetCondition.setWindDirection(aTerrain.getWindDirection());
					planetCondition.setWindStrength(aTerrain.getWindStrength());
					planetCondition.setMaxWindStrength(aTerrain.getMaxWindStrength());

					// Check for a night game and set nightGame Variable.
					// This is needed to be done since it was possible that a
					// slow connection
					// would keep the client from getting an update from the
					// server before the
					// entities where added to the game.
					nightGame = aTerrain.getLightConditions() > PlanetaryConditions.L_DUSK;

					client.sendPlanetaryConditions(planetCondition);

					mySettings.setMedium(mwclient.getMapMedium());
					client.sendMapSettings(mySettings);
				} else {
					PlanetEnvironment env = mwclient.getCurrentEnvironment();
					/* Set the map-gen values */
					mySettings.setElevationParams(env.getHillyness(), env.getHillElevationRange(), env.getHillInvertProb());
					mySettings.setWaterParams(env.getWaterMinSpots(), env.getWaterMaxSpots(), env.getWaterMinHexes(), env.getWaterMaxHexes(), env.getWaterDeepProb());
					mySettings.setForestParams(env.getForestMinSpots(), env.getForestMaxSpots(), env.getForestMinHexes(), env.getForestMaxHexes(), env.getForestHeavyProb());
					mySettings.setRoughParams(env.getRoughMinSpots(), env.getRoughMaxSpots(), env.getRoughMinHexes(), env.getRoughMaxHexes());
					mySettings.setSwampParams(env.getSwampMinSpots(), env.getSwampMaxSpots(), env.getSwampMinHexes(), env.getSwampMaxHexes());
					mySettings.setPavementParams(env.getPavementMinSpots(), env.getPavementMaxSpots(), env.getPavementMinHexes(), env.getPavementMaxHexes());
					mySettings.setIceParams(env.getIceMinSpots(), env.getIceMaxSpots(), env.getIceMinHexes(), env.getIceMaxHexes());
					mySettings.setRubbleParams(env.getRubbleMinSpots(), env.getRubbleMaxSpots(), env.getRubbleMinHexes(), env.getRubbleMaxHexes());
					mySettings.setFortifiedParams(env.getFortifiedMinSpots(), env.getFortifiedMaxSpots(), env.getFortifiedMinHexes(), env.getFortifiedMaxHexes());
					mySettings.setSpecialFX(env.getFxMod(), env.getProbForestFire(), env.getProbFreeze(), env.getProbFlood(), env.getProbDrought());
					mySettings.setRiverParam(env.getRiverProb());
					mySettings.setCliffParam(env.getCliffProb());
					mySettings.setRoadParam(env.getRoadProb());
					mySettings.setCraterParam(env.getCraterProb(), env.getCraterMinNum(), env.getCraterMaxNum(), env.getCraterMinRadius(), env.getCraterMaxRadius());
					mySettings.setAlgorithmToUse(env.getAlgorithm());
					mySettings.setInvertNegativeTerrain(env.getInvertNegativeTerrain());
					mySettings.setMountainParams(env.getMountPeaks(), env.getMountWidthMin(), env.getMountWidthMax(), env.getMountHeightMin(), env.getMountHeightMax(), env.getMountStyle());
					if (env.getTheme().length() > 1) {
						mySettings.setTheme(env.getTheme());
					} else {
						mySettings.setTheme("");
					}

					/* select the map */
					ArrayList<String> boardvec = new ArrayList<String>();
					boardvec.add(MapSettings.BOARD_GENERATED);
					mySettings.setBoardsSelectedVector(boardvec);

					if (mwclient.getBuildingTemplate() != null && mwclient.getBuildingTemplate().getTotalBuildings() > 0) {
						ArrayList<BuildingTemplate> buildingList = generateRandomBuildings(mySettings, mwclient.getBuildingTemplate());
						mySettings.setBoardBuildings(buildingList);
					} else if (!env.getCityType().equalsIgnoreCase("NONE")) {
						mySettings.setRoadParam(0);
						mySettings.setCityParams(env.getRoads(), env.getCityType(), env.getMinCF(), env.getMaxCF(), env.getMinFloors(), env.getMaxFloors(), env.getCityDensity(), env.getTownSize());
					}

					mySettings.setMedium(mwclient.getMapMedium());
					/* sent to server */
					client.sendMapSettings(mySettings);

					if (aTerrain != null) {
						PlanetaryConditions planetCondition = new PlanetaryConditions();

						planetCondition.setGravity((float) aTerrain.getGravity());
						planetCondition.setTemperature(aTerrain.getTemperature());
						planetCondition.setAtmosphere(aTerrain.getAtmosphere());
						planetCondition.setEMI(aTerrain.hasEMI());
						planetCondition.setFog(aTerrain.getFog());
						planetCondition.setLight(aTerrain.getLightConditions());
						planetCondition.setShiftingWindDirection(aTerrain.hasShifitingWindDirection());
						planetCondition.setShiftingWindStrength(aTerrain.hasShifitingWindStrength());
						planetCondition.setTerrainAffected(aTerrain.isTerrainAffected());
						planetCondition.setWeather(aTerrain.getWeatherConditions());
						planetCondition.setWindDirection(aTerrain.getWindDirection());
						planetCondition.setWindStrength(aTerrain.getWindStrength());
						planetCondition.setMaxWindStrength(aTerrain.getMaxWindStrength());

						// Check for a night game and set nightGame Variable.
						// This is needed to be done since it was possible that
						// a slow connection
						// would keep the client from getting an update from the
						// server before the
						// entities where added to the game.
						nightGame = aTerrain.getLightConditions() > PlanetaryConditions.L_DUSK;

						client.sendPlanetaryConditions(planetCondition);
					}
				}

			}

			/*
			 * Add bots, if being used in this game.
			 */
			if (mwclient.isUsingBots()) {
				String name = "War Bot" + client.getLocalPlayer().getId();
				bot = new TestBot(name, client.getHost(), client.getPort());
				bot.game.addGameListener(new BotGUI(bot));
				try {
					bot.connect();
					Thread.sleep(125);
					while (bot.getLocalPlayer() == null) {
						Thread.sleep(50);
					}
					// if game is running, shouldn't do the following, so detect
					// the phase
					for (int i = 0; i < 1000 && bot.game.getPhase() == IGame.Phase.PHASE_UNKNOWN; i++) {
						Thread.sleep(50);
					}
				} catch (Exception ex) {
					CampaignData.mwlog.errLog("Bot Error!");
					CampaignData.mwlog.errLog(ex);
				}
				bot.retrieveServerInfo();
				Thread.sleep(125);

				if (awtGUI) {
					awtGui.getBots().put(name, bot);
				} else {
					swingGui.getBots().put(name, bot);
				}

				if (mwclient.isBotsOnSameTeam()) {
					bot.getLocalPlayer().setTeam(5);
				}
				Random r = new Random();

				bot.getLocalPlayer().setStartingPos(r.nextInt(11));
				bot.sendPlayerInfo();
				Thread.sleep(125);
			}

			if ((client.game != null && client.game.getPhase() == IGame.Phase.PHASE_LOUNGE)) {

				client.game.getOptions().loadOptions();
				if (mechs.size() > 0 && xmlGameOptions.size() > 0) {
					client.sendGameOptions("", xmlGameOptions);
				}

				IClientPreferences cs = PreferenceManager.getClientPreferences();
				cs.setStampFilenames(Boolean.parseBoolean(mwclient.getserverConfigs("MMTimeStampLogFile")));
				cs.setShowUnitId(Boolean.parseBoolean(mwclient.getserverConfigs("MMShowUnitId")));
				cs.setKeepGameLog(Boolean.parseBoolean(mwclient.getserverConfigs("MMKeepGameLog")));
				cs.setGameLogFilename(mwclient.getserverConfigs("MMGameLogName"));
				if (mwclient.getConfig().getParam("UNITCAMO").length() > 0) {
					client.getLocalPlayer().setCamoCategory(Player.ROOT_CAMO);
					client.getLocalPlayer().setCamoFileName(mwclient.getConfig().getParam("UNITCAMO"));
					playerUpdate = true;
				}

				if (bot != null) {
					bot.getLocalPlayer().setNbrMFConventional(mwclient.getPlayer().getConventionalMinesAllowed());
					bot.getLocalPlayer().setNbrMFVibra(mwclient.getPlayer().getVibraMinesAllowed());
				} else {
					client.getLocalPlayer().setNbrMFConventional(mwclient.getPlayer().getConventionalMinesAllowed());
					client.getLocalPlayer().setNbrMFVibra(mwclient.getPlayer().getVibraMinesAllowed());
				}

				for (Unit unit : mechs) {
					// Get the Mek
					CUnit mek = (CUnit) unit;
					// Get the Entity
					Entity entity = mek.getEntity();
					// Set the TempID for autoreporting
					entity.setExternalId(mek.getId());
					// entity.setId(mek.getId());
					// Set the owner
					entity.setOwner(client.getLocalPlayer());
					// Set if unit is a commander in this army.
					entity.setCommander(currA.isCommander(mek.getId()));

					// Set slights based on games light conditions.
					entity.setSpotlight(nightGame);
					entity.setSpotlightState(nightGame);

					// Set the correct home edge for off board units
					if (entity.isOffBoard()) {
						int direction = IOffBoardDirections.NORTH;
						switch (mwclient.getPlayerStartingEdge()) {
						case 4:
						case 14:
							direction = IOffBoardDirections.EAST;
							break;
						case 5:
						case 6:
						case 7:
						case 15:
						case 16:
						case 17:
							direction = IOffBoardDirections.SOUTH;
							break;
						case 8:
						case 18:
							direction = IOffBoardDirections.WEST;
							break;
						default:
							direction = IOffBoardDirections.NORTH;
							break;
						}
						entity.setOffBoard(entity.getOffBoardDistance(), direction);
					}

					// Add Pilot to entity
					entity.setCrew(UnitUtils.createEntityPilot(mek));
					// Add Mek to game
					client.sendAddEntity(entity);
					// Wait a few secs to not overuse bandwith
					Thread.sleep(125);
				}

				/*
				 * Army mechs already loaded (see previous for loop). Now try to
				 * load the artillery units generated by the server (see
				 * AutoArmy.java in the server.campaign pacakage for generation
				 * details).
				 */
				Iterator<CUnit> autoIt = autoarmy.iterator();
				while (autoIt.hasNext()) {

					// get the unit
					CUnit autoUnit = autoIt.next();

					// get the entity
					Entity entity = autoUnit.getEntity();

					// Set slights based on games light conditions.
					entity.setSpotlight(nightGame);
					entity.setSpotlightState(nightGame);

					// Had issues with Id's so we are now setting them.
					// entity.setId(autoUnit.getId());
					entity.setExternalId(autoUnit.getId());

					// Set the owner
					if (bot != null) {
						entity.setOwner(bot.getLocalPlayer());
					} else {
						entity.setOwner(client.getLocalPlayer());
					}

					if (entity.getCrew().getName().equalsIgnoreCase("Unnamed") || entity.getCrew().getName().equalsIgnoreCase("vacant")) {
						// set the pilot
						Pilot pilot = new Pilot("AutoArtillery", 4, 5);
						entity.setCrew(pilot);
					} else {
						entity.setCrew(UnitUtils.createEntityPilot(autoUnit));
					}

					// CampaignData.mwlog.errLog(entity.getModel()+"
					// direction "+entity.getOffBoardDirection());
					// add the unit to the game.
					if (bot != null) {
						bot.sendAddEntity(entity);
					} else {
						client.sendAddEntity(entity);
					}

					// Wait a few secs to not overuse bandwith
					Thread.sleep(125);
				}// end while(more autoarty)

				if (mwclient.getPlayerStartingEdge() != Buildings.EDGE_UNKNOWN) {
					client.getLocalPlayer().setStartingPos(mwclient.getPlayerStartingEdge());
					playerUpdate = true;
				}

				if (mechs.size() > 0) {
					// check armies for C3Network mechs

					synchronized (currA) {

						if (currA.getC3Network().size() > 0) {
							// Thread.sleep(125);
							playerUpdate = true;
							for (int slave : currA.getC3Network().keySet()) {
								linkMegaMekC3Units(currA, slave, currA.getC3Network().get(slave));
							}

							if (awtGUI) {
								awtGui.chatlounge.refreshEntities();
							} else {
								swingGui.chatlounge.refreshEntities();
							}
						}
					}
				}

				if (mwclient.getPlayer().getTeamNumber() > 0) {
					client.getLocalPlayer().setTeam(mwclient.getPlayer().getTeamNumber());
					playerUpdate = true;
				}

				if (playerUpdate) {
					client.sendPlayerInfo();
					if (bot != null) {
						bot.sendPlayerInfo();
					}
				}

			}

		} catch (Exception e) {
			CampaignData.mwlog.errLog(e);
		}
	}

	/*
	 * from megamek.client.CloseClientListener clientClosed() Thanks to MM for
	 * adding the listener. And to MMNet for the poorly documented code change.
	 */
	public void clientClosed() {

		PreferenceManager.getInstance().save();

		if (bot != null) {
			bot.die();
			bot = null;
		}

		// client.die();
		client = null;// explicit null of the MM client. Wasn't/isn't being
		// GC'ed.
		mwclient.closingGame(serverName);
		System.gc();
	}

	/**
	 * @author jtighe
	 * @param army
	 * @param slaveid
	 * @param masterid
	 *            This function goes through and makes sure the slave is linked
	 *            to the master unit
	 */
	public void linkMegaMekC3Units(CArmy army, Integer slaveid, Integer masterid) {
		Entity c3Unit = null;
		Entity c3Master = null;

		while (c3Unit == null || c3Master == null) {
			try {

				for (Entity en : client.game.getEntitiesVector()) {
					if (c3Unit == null && en.getExternalId() == slaveid) {
						c3Unit = en;
					}

					if (c3Master == null && en.getExternalId() == masterid) {
						c3Master = en;
					}
				}
				Thread.sleep(10);// give the queue time to refresh
			} catch (Exception ex) {
				CampaignData.mwlog.errLog("Error in linkMegaMekC3Units");
				CampaignData.mwlog.errLog(ex);
			}
		}

		// catch for some funky stuff
		if (c3Unit == null || c3Master == null) {
			CampaignData.mwlog.errLog("Null Units c3Unit: " + c3Unit + " C3Master: " + c3Master);
			return;
		}

		try {
			CUnit masterUnit = (CUnit) army.getUnit(masterid);
			// CampaignData.mwlog.errLog("Master Unit:
			// "+masterUnit.getModelName());
			// CampaignData.mwlog.errLog("Slave Unit:
			// "+c3Unit.getModel());
			if (!masterUnit.hasC3SlavesLinkedTo(army) && masterUnit.hasBeenC3LinkedTo(army) && (masterUnit.getC3Level() == Unit.C3_MASTER || masterUnit.getC3Level() == Unit.C3_MMASTER)) {
				// CampaignData.mwlog.errLog("Unit:
				// "+c3Master.getModel()+" id: "+c3Master.getExternalId());
				if (c3Master.getC3MasterId() == Entity.NONE) {
					c3Master.setShutDown(false);
					c3Master.setC3Master(c3Master);
					client.sendUpdateEntity(c3Master);
				}
				/*
				 * if ( c3Master.hasC3MM() )
				 * CampaignData.mwlog.errLog("hasC3MM"); else
				 * CampaignData.mwlog.errLog("!hasC3MM");
				 */
			} else if (c3Master.getC3MasterId() != Entity.NONE) {
				c3Master.setShutDown(false);
				c3Master.setC3Master(Entity.NONE);
				client.sendUpdateEntity(c3Master);
			}
			// CampaignData.mwlog.errLog("c3Unit: "+c3Unit.getModel()+"
			// Master: "+c3Master.getModel());
			c3Unit.setShutDown(false);
			c3Unit.setC3Master(c3Master);
			// CampaignData.mwlog.errLog("c3Master Set to
			// "+c3Unit.getC3MasterId()+" "+c3Unit.getC3NetId());
			client.sendUpdateEntity(c3Unit);
		} catch (Exception ex) {
			CampaignData.mwlog.errLog(ex);
			CampaignData.mwlog.errLog("Error in setting up C3Network");
		}
	}

	/*
	 * Taken form Megamek Code for use with MekWars The call was private and was
	 * needed. Thanks to Ben Mazur and all of the MM coders we hope for a long
	 * and happy relation ship. Torren.
	 */

	public static Comparator<? super Object> stringComparator() {
		return new Comparator<Object>() {
			public int compare(Object o1, Object o2) {
				String s1 = ((String) o1).toLowerCase();
				String s2 = ((String) o2).toLowerCase();
				return s1.compareTo(s2);
			}
		};
	}

	/**
	 * Scans the boards directory for map boards of the appropriate size and
	 * returns them.
	 */
	private ArrayList<String> scanForBoards(int boardWidth, int boardHeight, String folder) {
		ArrayList<String> boards = new ArrayList<String>();
		// Board Board = client.game.getBoard();

		File boardDir = new File("data/boards/" + folder);

		// just a check...
		if (!boardDir.isDirectory()) {
			return boards;
		}

		// scan files
		String[] fileList = boardDir.list();
		Vector<String> tempList = new Vector<String>(1, 1);
		Comparator<? super String> sortComp = ClientThread.stringComparator();
		for (String path : fileList) {
			if (path.indexOf(".board") == -1) {
				continue;
			}

			if (folder.trim().length() > 0) {
				path = folder + "/" + path;
			}

			if (Board.boardIsSize(path, boardWidth, boardHeight)) {
				tempList.addElement(path.substring(0, path.lastIndexOf(".board")));
			}
		}

		// if there are any boards, add these:
		if (tempList.size() > 0) {
			boards.add(MapSettings.BOARD_RANDOM);
			boards.add(MapSettings.BOARD_SURPRISE);
			boards.add(MapSettings.BOARD_GENERATED);
			Collections.sort(tempList, sortComp);
			for (int loop = 0; loop < tempList.size(); loop++) {
				boards.add(tempList.elementAt(loop));
			}
		} else {
			boards.add(MapSettings.BOARD_GENERATED);
		}

		return boards;
	}

	private ArrayList<BuildingTemplate> generateRandomBuildings(MapSettings mapSettings, Buildings buildingTemplate) {

		ArrayList<BuildingTemplate> buildingList = new ArrayList<BuildingTemplate>();
		ArrayList<String> buildingTypes = new ArrayList<String>();

		int width = mapSettings.getBoardWidth();
		int height = mapSettings.getBoardHeight();
		int minHeight = 0;
		int minWidth = 0;

		switch (buildingTemplate.getStartingEdge()) {
		case Buildings.NORTH:
			height = 5;
			minHeight = 1;
			break;
		case Buildings.SOUTH:
			if (height > 5) {
				minHeight = height - 5;
			}
			height = 5;
			break;
		case Buildings.EAST:
			if (width > 5) {
				minWidth = width - 5;
			}
			width = 5;
			break;
		case Buildings.WEST:
			width = 5;
			minWidth = 1;
			break;
		default:
			break;
		}

		StringTokenizer types = new StringTokenizer(buildingTemplate.getBuildingType(), ",");

		while (types.hasMoreTokens()) {
			buildingTypes.add(types.nextToken());
		}

		int typeSize = buildingTypes.size();

		Random r = new Random();

		TreeSet<String> tempMap = new TreeSet<String>();
		Coords coord = new Coords();
		String stringCoord = "";

		for (int count = 0; count < buildingTemplate.getTotalBuildings(); count++) {
			int loops = 0;
			boolean CFx2 = false;
			ArrayList<Coords> coordList = new ArrayList<Coords>();
			do {
				if (loops++ > 100) {
					CFx2 = true;
					break;
				}

				int x = r.nextInt(width) + minWidth;
				int y = r.nextInt(height) + minHeight;

				if (x >= mapSettings.getBoardWidth()) {
					x = mapSettings.getBoardWidth() - 2;
				} else if (x <= 1) {
					x = 2;
				}

				if (y >= mapSettings.getBoardHeight()) {
					y = mapSettings.getBoardHeight() - 2;
				} else if (y <= 1) {
					y = 2;
				}

				coord = new Coords(x, y);

				stringCoord = x + "," + y;
			} while (tempMap.contains(stringCoord));

			tempMap.add(stringCoord);
			coordList.add(coord);

			int floors = buildingTemplate.getMaxFloors() - buildingTemplate.getMinFloors();

			if (floors <= 0) {
				floors = buildingTemplate.getMinFloors();
			} else {
				floors = r.nextInt(floors) + buildingTemplate.getMinFloors();
			}

			int totalCF = buildingTemplate.getMaxCF() - buildingTemplate.getMinCF();

			if (totalCF <= 0) {
				totalCF = buildingTemplate.getMinCF();
			} else {
				totalCF = r.nextInt(totalCF) + buildingTemplate.getMinCF();
			}

			if (CFx2) {
				totalCF *= 2;
			}

			int type = 1;
			try {
				if (typeSize == 1) {
					type = Integer.parseInt(buildingTypes.get(0));
				} else {
					type = Integer.parseInt(buildingTypes.get(r.nextInt(typeSize)));
				}
			} catch (Exception ex) {
			} // someone entered a bad building type.

			buildingList.add(new BuildingTemplate(type, coordList, totalCF, floors, -1));
		}

		return buildingList;
	}

}