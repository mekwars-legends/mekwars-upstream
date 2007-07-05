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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.Vector;
import java.io.File;

import client.campaign.CUnit;
import client.campaign.CArmy;

import common.AdvanceTerrain;
import common.MegaMekPilotOption;
import common.MMGame;
import common.PlanetEnvironment;
import common.Unit;
import common.campaign.Buildings;
import common.campaign.pilot.skills.PilotSkill;
import common.util.UnitUtils;

import megamek.client.Client;
import megamek.client.bot.BotClient;
import megamek.client.bot.TestBot;
import megamek.client.bot.ui.AWT.BotGUI;
import megamek.client.ui.AWT.ClientGUI;
import megamek.common.BattleArmor;
import megamek.common.Board;
import megamek.common.CriticalSlot;
import megamek.common.Entity;
import megamek.common.event.GameBoardChangeEvent;
import megamek.common.event.GameBoardNewEvent;
import megamek.common.event.GameEndEvent;
import megamek.common.event.GameEntityChangeEvent;
import megamek.common.event.GameEntityNewEvent;
import megamek.common.event.GameEntityNewOffboardEvent;
import megamek.common.event.GameEntityRemoveEvent;
import megamek.common.event.GameListener;
import megamek.common.event.GameEvent;
import megamek.common.event.GameMapQueryEvent;
import megamek.common.event.GameNewActionEvent;
import megamek.common.event.GamePhaseChangeEvent;
import megamek.common.event.GamePlayerChangeEvent;
import megamek.common.event.GamePlayerChatEvent;
import megamek.common.event.GamePlayerConnectedEvent;
import megamek.common.event.GamePlayerDisconnectedEvent;
import megamek.common.event.GameReportEvent;
import megamek.common.event.GameSettingsChangeEvent;
import megamek.common.event.GameTurnChangeEvent;
import megamek.common.MapSettings;
import megamek.common.Mech;
import megamek.common.options.GameOptions;
import megamek.common.options.IBasicOption;
import megamek.common.options.Option;
import megamek.common.options.PilotOptions;
import megamek.common.options.IOption;
import megamek.common.options.IOptionGroup;
import megamek.common.BipedMech;
import megamek.common.Coords;
import megamek.common.IEntityRemovalConditions;
import megamek.common.IGame;
import megamek.common.IOffBoardDirections;
import megamek.common.Pilot;
import megamek.common.Player;
import megamek.common.Protomech;
import megamek.common.QuadMech;
import megamek.common.Tank;
import megamek.common.MechWarrior;
import megamek.common.util.BuildingTemplate;
import megamek.client.CloseClientListener;
import megamek.common.preference.IClientPreferences;
import megamek.common.preference.PreferenceManager;


class ClientThread extends Thread implements GameListener, CloseClientListener  {
	
	//VARIABLES
	private String myname;
	private String serverip;
	private String serverName;
	private int serverport;
	private MWClient mwclient;
	private Client client;
    private ClientGUI gui;
	private int turn = 0;
	private ArrayList<Unit> mechs = new ArrayList<Unit>();
	private ArrayList<CUnit> autoarmy = new ArrayList<CUnit>();//from server's auto army
    BotClient bot = null;
    
    final int N  = 0;
    final int NE = 1;
    final int SE = 2;
    final int S  = 3;
    final int SW = 4;
    final int NW = 5;
    
    //CONSTRUCTOR	
	public ClientThread(String name, String servername, String ip, int port, MWClient mwclient, ArrayList<Unit> mechs, ArrayList<CUnit> autoarmy) {
		myname = name;
		serverName = servername;
		serverip = ip;
		serverport = port;
		this.mwclient = mwclient;
		this.mechs = mechs;
		this.autoarmy = autoarmy;
		if (serverip.indexOf("127.0.0.1") != -1) {
			this.serverip = "127.0.0.1";
		}
	}
	
	//METHODS
	public int getTurn() {
		return turn;
	}

	public Client getClient() {
		return client;
	}
	
	@Override
	public void run() {
        boolean playerUpdate = false;
		client = new Client(myname, serverip, serverport);
		client.game.addGameListener(this);
		client.addCloseClientListener(this);
		gui = new ClientGUI(client);
		gui.initialize();
		//client.game.getOptions().
        Vector<IBasicOption> xmlGameOptions = new Vector<IBasicOption>();
        Vector<IOption> loadOptions = client.game.getOptions().loadOptions();
        
        //Load Defaults first.
        Enumeration options = client.game.getOptions().getOptions();
        while ( options.hasMoreElements() ) {
        	IOption option = (IOption)options.nextElement();
        	switch ( option.getType() ) {
        	case IOption.BOOLEAN: 
        		xmlGameOptions.add((new Option(new GameOptions(),option.getName(),(Boolean)option.getDefault())));
        		break;
        	case IOption.FLOAT:
        		xmlGameOptions.add((new Option(new GameOptions(),option.getName(),(Float)option.getDefault())));
        		break;
        	case IOption.STRING:
        		xmlGameOptions.add((new Option(new GameOptions(),option.getName(),(String)option.getDefault())));
        		break;
        	case IOption.INTEGER:
        		xmlGameOptions.add((new Option(new GameOptions(),option.getName(),(Integer)option.getDefault())));
        		break;	
        	case IOption.CHOICE:
        		xmlGameOptions.add((new Option(new GameOptions(),option.getName(),(String)option.getDefault())));
        		break;
        	}
        }
        
        xmlGameOptions.addAll(loadOptions);
        xmlGameOptions.addAll(this.mwclient.getGameOptions());
		
		try {
			client.connect();
		} catch (Exception ex) {
			client = null;
			mwclient.showInfoWindow("Couldn't join this game!");
			MWClient.mwClientLog.clientOutputLog(serverip + " " + serverport);
			return;
		}
		//client.retrieveServerInfo();
		try {
			while (client.getLocalPlayer() == null) {
				sleep(50);
			}
			//if game is running, shouldn't do the following, so detect the phase
			for (int i = 0; i < 1000 && client.game.getPhase() == IGame.PHASE_UNKNOWN; i++) {
				sleep(50);
			}
            

            //Lets start with the environment set first then do everything else.
            if (this.mwclient.getCurrentEnvironment() != null) {
                // creates the playboard*/
                MapSettings mySettings = new MapSettings(mwclient.getMapSize().width,mwclient.getMapSize().height, 1, 1);
                //MapSettings mySettings = new MapSettings(16, 17, 2, 2);
                AdvanceTerrain aTerrain = new AdvanceTerrain();
                aTerrain = this.mwclient.getCurrentAdvanceTerrain();
            
                if ( (aTerrain != null ) && aTerrain.isStaticMap() ){
                    
                    mySettings = new MapSettings(aTerrain.getXSize(),aTerrain.getYSize(), aTerrain.getXBoardSize(),aTerrain.getYBoardSize());
                    
                    //MMClient.mwClientLog.clientErrLog("Board x: "+myClient.getBoardSize().width+"Board y: "+myClient.getBoardSize().height+"Map x: "+myClient.getMapSize().width+"Map y: "+myClient.getMapSize().height);
                    Vector<String> boardvec = new Vector<String>();
                    if (aTerrain.getStaticMapName().equalsIgnoreCase("surprise") )
                    {
                        int maxBoards = aTerrain.getXBoardSize() * aTerrain.getYBoardSize();
                        for (int i = 0; i < maxBoards; i++)
                            boardvec.add(MapSettings.BOARD_SURPRISE);
                        
                        mySettings.setBoardsSelectedVector(boardvec);
                        mySettings.setBoardsAvailableVector(scanForBoards(aTerrain.getXSize(),aTerrain.getYSize()));
                    }
                    else 
                    {
                        boardvec.add(aTerrain.getStaticMapName());
                        mySettings.setBoardsSelectedVector(boardvec);
                    }
                    client.sendMapSettings(mySettings);
                }
                
                else
                {
                    PlanetEnvironment env = this.mwclient.getCurrentEnvironment();
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
                    mySettings.setSpecialFX(env.getFxMod(),env.getProbForestFire(),env.getProbFreeze(), env.getProbFlood(),env.getProbDrought());
                    mySettings.setRiverParam(env.getRiverProb());
                    mySettings.setCliffParam(env.getCliffProb());
                    mySettings.setRoadParam(env.getRoadProb());
                    mySettings.setCraterParam(env.getCraterProb(), env.getCraterMinNum(), env.getCraterMaxNum(), env.getCraterMinRadius(), env.getCraterMaxRadius());
                    mySettings.setAlgorithmToUse(env.getAlgorithm());
                    mySettings.setInvertNegativeTerrain(env.getInvertNegativeTerrain());
                    mySettings.setMountainParams(env.getMountPeaks(), env.getMountWidthMin(),env.getMountWidthMax(), env.getMountHeightMin(), env.getMountHeightMax(), env.getMountStyle());
                    if ( env.getTheme().length() > 1 )
                        mySettings.setTheme(env.getTheme());
                    
                    /* select the map */
                    Vector<String> boardvec = new Vector<String>();
                    boardvec.add(MapSettings.BOARD_GENERATED);
                    mySettings.setBoardsSelectedVector(boardvec);

                    if ( mwclient.getBuildingTemplate() != null && mwclient.getBuildingTemplate().getTotalBuildings() > 0){
                        Vector<BuildingTemplate> buildingList = generateRandomBuildings(mySettings,mwclient.getBuildingTemplate());
                        mySettings.setBoardBuildings(buildingList);
                    }
                    else if ( !env.getCityType().equalsIgnoreCase("NONE") ){
                        mySettings.setRoadParam(0);
                        mySettings.setCityParams(env.getRoads(),env.getCityType(),env.getMinCF(),env.getMaxCF(),env.getMinFloors(),env.getMaxFloors(),env.getCityDensity(),env.getTownSize());
                    }
                    
                    /* sent to server */
                    client.sendMapSettings(mySettings);
                }
                
            }
            
            /*
             * Add bots, if being used in this game.
             */
            if ( mwclient.isUsingBots() ){
                String name = "War Bot"+client.getLocalPlayer().getId();
                bot = new TestBot(name, client.getHost(), client.getPort());
                bot.game.addGameListener(new BotGUI(bot));
                try{
                    bot.connect();
                    sleep(125);
                    while (bot.getLocalPlayer() == null) {
                        sleep(50);
                    }
                    //if game is running, shouldn't do the following, so detect the phase
                    for (int i = 0; i < 1000 && bot.game.getPhase() == IGame.PHASE_UNKNOWN; i++) {
                        sleep(50);
                    }
                }catch (Exception ex){
                    MWClient.mwClientLog.clientErrLog("Bot Error!");
                    MWClient.mwClientLog.clientErrLog(ex);
                }
                bot.retrieveServerInfo();
                sleep(125);
                gui.getBots().put(name, bot);
                if ( mwclient.isBotsOnSameTeam() )
                    bot.getLocalPlayer().setTeam(5);
                Random r = new Random();

                bot.getLocalPlayer().setStartingPos(r.nextInt(11));
                bot.sendPlayerInfo();
                sleep(125);
            }
            
			if ((client.game != null && client.game.getPhase() == IGame.PHASE_LOUNGE)) {
				if (this.mechs.size() > 0)
					/*if (this.mwclient.getGameOptions().size() > 0)
						client.sendGameOptions("", this.mwclient.getGameOptions());*/
                    if ( xmlGameOptions.size() > 0 )
                       client.sendGameOptions("",xmlGameOptions);
				IClientPreferences cs = PreferenceManager.getClientPreferences();
				cs.setStampFilenames(Boolean.parseBoolean(mwclient.getserverConfigs("MMTimeStampLogFile")));
				cs.setShowUnitId(Boolean.parseBoolean(mwclient.getserverConfigs("MMShowUnitId")));
				cs.setKeepGameLog(Boolean.parseBoolean(mwclient.getserverConfigs("MMKeepGameLog")));
				cs.setGameLogFilename(mwclient.getserverConfigs("MMGameLogName"));
				if (mwclient.getConfig().getParam("UNITCAMO").length() > 0 ){
					client.getLocalPlayer().setCamoCategory(Player.ROOT_CAMO);
					client.getLocalPlayer().setCamoFileName(mwclient.getConfig().getParam("UNITCAMO"));
                    playerUpdate = true;
				}
				
                if ( bot != null ){
                    bot.getLocalPlayer().setNbrMFConventional(mwclient.getPlayer().getConventionalMinesAllowed());
                    bot.getLocalPlayer().setNbrMFVibra(mwclient.getPlayer().getVibraMinesAllowed());
                }
                else{
                    client.getLocalPlayer().setNbrMFConventional(mwclient.getPlayer().getConventionalMinesAllowed());
                    client.getLocalPlayer().setNbrMFVibra(mwclient.getPlayer().getVibraMinesAllowed());
                }
                
                for (Iterator i = this.mechs.iterator(); i.hasNext();) {
					// Get the Mek
					CUnit mek = (CUnit) i.next();
					// Get the Entity
					Entity entity = mek.getEntity();
					// Set the TempID for autoreporting
					entity.setExternalId(mek.getId());
					entity.setId(mek.getId());
					//Set the owner
					entity.setOwner(client.getLocalPlayer());
					
                    //Set the correct home edge for off board units
                    if ( entity.isOffBoard() ){
                        int direction = IOffBoardDirections.NORTH;
                        switch (mwclient.getPlayerStartingEdge()) {
                        case 0:
                            break;
                        case 1:
                        case 2:
                        case 3:
                            direction = IOffBoardDirections.NORTH;
                            break;
                        case 4:
                            direction = IOffBoardDirections.EAST;
                            break;
                        case 5:
                        case 6:
                        case 7:
                            direction = IOffBoardDirections.SOUTH;
                            break;
                        case 8:
                            direction = IOffBoardDirections.WEST;
                            break;
                        }
                        entity.setOffBoard(entity.getOffBoardDistance(),direction);
                    }
                    
                    //get and set the options
                    IOptionGroup group = null;
					Pilot pilot = null;
					if (mek.getType() == Unit.MEK || mek.getType() == Unit.VEHICLE)
					    pilot = new Pilot(mek.getPilot().getName(), mek.getPilot().getGunnery(), mek.getPilot().getPiloting());
					else
					    pilot = new Pilot(mek.getPilot().getName(), mek.getPilot().getGunnery(), 5);
                    
                    //Hits defaults to 0 so no reason to keep checking over and over again.
                    pilot.setHits(mek.getPilot().getHits());
					//No reason to keep searching for the same group over and over and over again
					//find it once and serach through it each time for the pilots skill
					for (Enumeration enumeration = pilot.getOptions().getGroups(); enumeration.hasMoreElements();) {
						group = (IOptionGroup) enumeration.nextElement();
					    //MWClient.mwClientLog.clientErrLog("Checking: " + pilot.getName()+" Key: "+group.getKey());
						if (group.getKey().equalsIgnoreCase(PilotOptions.LVL3_ADVANTAGES)) break;
					}
					
					Iterator iter = mek.getPilot().getMegamekOptions().iterator();
					while (iter.hasNext()) {
					    MegaMekPilotOption po = (MegaMekPilotOption) iter.next();
						for (Enumeration j = group.getOptions(); j.hasMoreElements();) {
							IOption option = (IOption) j.nextElement();
							//MWClient.mwClientLog.clientErrLog("Unit: "+mek.getModelName()+" Checking: " + option.getName() + " with " + po.getMmname());
                            if (option.getName().equals(po.getMmname())){
                                if ( po.getMmname().equals("weapon_specialist")){
                                    option.setValue(mek.getPilot().getWeapon());
                                }
                                else if ( po.getMmname().equals("edge")){
                                    option.setValue(mek.getPilot().getSkills().getPilotSkill(PilotSkill.EdgeSkillID).getLevel());
                                }
                                else{
                                    option.setValue(po.isValue());
                                }
                                break;
                            }
						}
					}
                    
                    boolean hasEdge = mek.getPilot().getSkills().has(PilotSkill.EdgeSkillID);
                    
                    if ( hasEdge ){
                        for (Enumeration j = group.getOptions(); j.hasMoreElements();) {
                            IOption option = (IOption) j.nextElement();

                            if ( option.getName().equals("edge_when_tac")){
                                option.setValue(mek.getPilot().getTac());
                            }
                            else if ( option.getName().equals("edge_when_ko")){
                                option.setValue(mek.getPilot().getKO());
                            }
                            else if ( option.getName().equals("edge_when_headhit")){
                                option.setValue(mek.getPilot().getHeadHit());
                            }
                            else if ( option.getName().equals("edge_when_explosion")){
                                option.setValue(mek.getPilot().getExplosion());
                            }//end of edge_else if statements.
                        }
                    }
					//Add Pilot to entity
					entity.setCrew(pilot);
					// Add Mek to game
					client.sendAddEntity(entity);
					// Wait a few secs to not overuse bandwith
					sleep(125);
				}
				
            
				/*
				 * Army mechs already loaded (see previous for loop). Now try to load
				 * the artillery units generated by the server (see AutoArmy.java in
				 * the server.campaign pacakage for generation details).
				 */
				Iterator autoIt = this.autoarmy.iterator();
				while (autoIt.hasNext()) {
					
					//get the unit
					CUnit autoUnit = (CUnit)autoIt.next();
					
					//get the entity
					Entity entity = autoUnit.getEntity();
					
					//ignore the id. allow megamek to assign one. we dont care how these units report.
					
					//Set the owner
                    if ( bot != null )
                        entity.setOwner(bot.getLocalPlayer());
                    else
                        entity.setOwner(client.getLocalPlayer());
					//set the pilot
					Pilot pilot = new Pilot("AutoArtillery", 4, 5);
					entity.setCrew(pilot);
					
                    MWClient.mwClientLog.clientErrLog(entity.getModel()+" direction "+entity.getOffBoardDirection());
					//add the unit to the game.
                    if ( bot != null )
                        bot.sendAddEntity(entity);
                    else
                        client.sendAddEntity(entity);

					// Wait a few secs to not overuse bandwith
					sleep(125);
				}//end while(more autoarty)
				
                if ( mwclient.getPlayerStartingEdge() != Buildings.EDGE_UNKNOWN ){
                    client.getLocalPlayer().setStartingPos(mwclient.getPlayerStartingEdge());
                    playerUpdate = true;
                }
                
                if (this.mechs.size() > 0) {
					//check armies for C3Network mechs
					CArmy currA = mwclient.getPlayer().getLockedArmy();

					synchronized (currA) {
						
						if (currA.getC3Network().size() > 0) {
							//Thread.sleep(125);
							playerUpdate = true;
							for (int slave : currA.getC3Network().keySet()) {
								linkMegaMekC3Units(currA,slave,currA.getC3Network().get(slave));
							}
							
							gui.chatlounge.refreshEntities();
						}
					}
				}

                if ( mwclient.getPlayer().getTeamNumber() > 0 ) {
                	client.getLocalPlayer().setTeam(mwclient.getPlayer().getTeamNumber());
                	playerUpdate = true;
                }
                
                if ( playerUpdate ){
                    client.sendPlayerInfo();
                    if ( bot != null)
                        bot.sendPlayerInfo();
                }
			}


		} catch (Exception e) {
			MWClient.mwClientLog.clientOutputLog(e);
		}
	}
	
	/**
	 * redundant code since MM does not always send a discon event.
	 */
	public void gamePlayerStatusChange(GameEvent e) {}
	
	public void gameTurnChange(GameTurnChangeEvent e) {
		if (client != null) {
			if (this.getTurn() == 0 && (myname.equals(serverName) || serverName.startsWith("[Dedicated]")))
				mwclient.serverSend("SHS|" + serverName + "|Running");
			turn += 1;
		}
	}
	
	public String serializeEntity (Entity e, boolean fullStatus, boolean forceDevastate) {
		
		String result = "";
        boolean useRepairs = mwclient.isUsingAdvanceRepairs();

		if (fullStatus) {
			if ( !(e instanceof MechWarrior))
			{
				result += e.getExternalId() + "*";
				result += e.getOwner().getName() + "*";
				result += e.getCrew().getHits() + "*";
				
				if (forceDevastate)
					result += IEntityRemovalConditions.REMOVE_DEVASTATED + "*";
				else
					result += e.getRemovalCondition() + "*";
				
				if ( e instanceof BipedMech )
					result += Unit.MEK +"*";
				else if ( e instanceof QuadMech )
					result += Unit.QUAD + "*";
				else if ( e instanceof Tank)
					result += Unit.VEHICLE +"*";
				else if ( e instanceof Protomech)
					result += Unit.PROTOMEK +"*";
				else if ( e instanceof BattleArmor )
					result += Unit.BATTLEARMOR+"*";
				else
					result += Unit.INFANTRY +"*";
				//result += e.getMovementType() + "*"; bad code
				//Collect kills
				Enumeration en = e.getKills();
				//No kills? Add an empty space
				if (!en.hasMoreElements())
					result += " *";
				while (en.hasMoreElements()) {
					Entity kill = (Entity) en.nextElement();
					result += kill.getExternalId();
					if (en.hasMoreElements())
						result += "~";
					else
						result += "*";
				}
			}
			
			if (e instanceof Mech ) {
				result += e.getCrew().isUnconscious() + "*";
				result += e.getInternal(Mech.LOC_CT) + "*";
				result += e.getInternal(Mech.LOC_HEAD) + "*";
				result += e.getInternal(Mech.LOC_LLEG) + "*";
				result += e.getInternal(Mech.LOC_RLEG) + "*";
				result += e.getInternal(Mech.LOC_LARM) + "*";
				result += e.getInternal(Mech.LOC_RARM) + "*";
				result += e.getBadCriticals(CriticalSlot.TYPE_SYSTEM, Mech.SYSTEM_GYRO, Mech.LOC_CT) + "*";
                result += ((Mech)e).getCockpitType()+"*";
                if ( useRepairs ){
                    result += UnitUtils.unitBattleDamage(e)+"*";
                }
			} else if (e instanceof Tank ) {
				result += e.isRepairable() + "*";
                result += e.isImmobile() + "*";
                result += e.getCrew().isDead() + "*";
			}
			else if (e instanceof MechWarrior) {
				MechWarrior mw = (MechWarrior)e;
				result += "MW*";
				result += mw.getOriginalRideExternalId() + "*";
				result += mw.getPickedUpByExternalId() + "*";
				result += mw.isDestroyed()+"*";
			}
			
			if (  e.isOffBoard() ){
			    result += e.getOffBoardDistance()+"*";
			}
		}
		
		/*
		 * FullStatus is used when autoreporting. This status, which 
		 * sends less information, is used for InProgressUpdates.
		 */
		else {
			//if the entity is a mechwarrior, send an IPU command
			//(InProgressUpdate) to the server.
			if (e instanceof MechWarrior) {
				MechWarrior mw = (MechWarrior)e;
				result += "MW*" + mw.getOriginalRideExternalId() + "*";
				result += mw.getPickedUpByExternalId() + "*";
				result += mw.isDestroyed()+"*";
			} 
			
			//else (the entity is a real unit)
			else {
				result += e.getOwner().getName() + "*";
				result += e.getExternalId() + "*";
				
				if (forceDevastate)
					result += IEntityRemovalConditions.REMOVE_DEVASTATED + "*";
				else
					result += e.getRemovalCondition() + "*";
				
				if (e instanceof Mech ) {
					result += e.getInternal(Mech.LOC_CT) + "*";
					result += e.getInternal(Mech.LOC_HEAD) + "*";
				} else {
					result += "1*";
					result += "1*";
				}
				result += e.isRepairable() + "*";
			}
		}//end else(un-full status)
		
		return result;
	}
	
	public void gamePhaseChange(GamePhaseChangeEvent e) {
		
		//String result = "";
		//String winnerName ="";
		String name = "";
		
		try{
			
			if (client.game.getPhase() == IGame.PHASE_VICTORY) {

                //Make sure the player is fully connected.
                while (client.getLocalPlayer() == null) {
                    sleep(50);
                }


                //clear out everything.
                mwclient.getPlayer().setConventionalMinesAllowed(0);
                mwclient.getPlayer().setVibraMinesAllowed(0);
                mwclient.setUsingBots(false);
                //clear out everything from this game
                mwclient.setEnvironment(null,null,null);
                mwclient.setAdvanceTerrain(null,null);
                mwclient.setPlayerStartingEdge(Buildings.EDGE_UNKNOWN);
                //get rid of any and all bots.
                for (Iterator i = gui.getBots().values().iterator(); i.hasNext();) {
                    ((Client)i.next()).die();
                }
                gui.getBots().clear();
                
                //observers need not report
				if (client.game.getAllEntitiesOwnedBy(client.getLocalPlayer()) < 1)
					return;

				MMGame toUse = mwclient.getServers().get(serverName);
                mwclient.serverSend("SGR|"+toUse.getHostName());
				MWClient.mwClientLog.clientOutputLog("GAME END");
				
				if (mwclient.getPlayer().getName().equalsIgnoreCase(name) ){
					if (toUse.getHostName().startsWith("[Dedicated]"))
						mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "mail "+toUse.getHostName()+",checkrestartcount");
				}
                //TODO set client = Null here and the maybe GC. after Hotload patch for MM
				
			}//end victory
			
			/*
			 * Reporting phases show deaths - units that try to stand and
			 * blow their ammo, units that have ammo explode from head, etc.
			 * 
			 * This is also an opportune time to correct isses with the
			 * gameRemoveEntity ISU's. Removals happen ASAP, even if the
			 * removal condition and final condition of the unit are not
			 * the same (ie - remove on Engine crits even when a CT core
			 * comes later in the round).
			 */
			else if(client.game.getPhase() == IGame.PHASE_FIRING_REPORT
					|| client.game.getPhase() == IGame.PHASE_PHYSICAL_REPORT
					|| client.game.getPhase() == IGame.PHASE_MOVEMENT_REPORT
					|| client.game.getPhase() == IGame.PHASE_END_REPORT
					|| client.game.getPhase() == IGame.PHASE_OFFBOARD_REPORT) {
				
				//observers need not report
				if (client.getLocalPlayer().isObserver())
					return;
				
				/*
				 * Wrecked entities include all those which were
				 * devastated, ejected, or removed but salvagable
				 * according to MegaMek. We want to check them for
				 * CT-cores.
				 */
				Enumeration en = client.game.getWreckedEntities();
				while (en.hasMoreElements()) {
					Entity currEntity = (Entity)en.nextElement();
					
					/*
					 * MM is now reporting post-attack IS instead of pre-attack IS
					 * in gameEntityRemove, so this check shouldn't be necessary anymore.
					 */
					//if (currEntity instanceof Mech || currEntity instanceof QuadMech) {
					//	//if a mech-type, override grouping if its salvage and CT 0
					//	if (currEntity.getInternal(Mech.LOC_CT) <= 0)
					//		mwclient.serverSend("IPU|" + this.serializeEntity(currEntity, false, true));
					//	
					//}
					
					if (currEntity instanceof MechWarrior)
						mwclient.serverSend("IPU|" + this.serializeEntity(currEntity, false, false));
				}
				
				//constantly update the onfield warriors.
				en = client.game.getEntities();
				while (en.hasMoreElements()) {
					Entity currEntity = (Entity)en.nextElement();
                    if ( currEntity.getOwner().getName().startsWith("War Bot"))
                        continue;
					if (currEntity instanceof MechWarrior)
						mwclient.serverSend("IPU|" + this.serializeEntity(currEntity, false, false));
				}
				
				/*
				 * This is probably extraneous - retreats should be properly handled
				 * in the movement phase and do not involve damage transferal which
				 * could lead to a final status different from the removal status.
				 */
				//en = client.game.getRetreatedEntities();
				//while (en.hasMoreElements()) {
				//	Entity currEntity = (Entity)en.nextElement();
				//	mwclient.serverSend("IPU|" + this.serializeEntity(currEntity, false));
				//}
			}
			
		}//end try
		catch (Exception ex){
			MWClient.mwClientLog.clientErrLog("Error reporting game!");
			MWClient.mwClientLog.clientErrLog(ex);
		}
	}
	
	/*
	 * When an entity is removed from play, check the reason. If 
	 * the unit is ejected, captured or devestated and the player
	 * is invovled in the game at hand, report the removal to the
	 * server.
	 * 
	 * The server stores these reports in pilotTree and deathTree
	 * in order to auto-resolve games after a player disconnects. 
	 * 
	 * NOTE: This send the *first possible* removal condition, which
	 * means that a unit which is simultanously head killed and then
	 * CT cored will show as salvageable.
	 */
	public void gameEntityRemove(GameEntityRemoveEvent e) {//only send if the player is actually involved in the game
		
		if (client.getLocalPlayer().isObserver())
			return;
		
		//get the entity
		megamek.common.Entity removedE = e.getEntity();
        if ( removedE.getOwner().getName().startsWith("War Bot"))
            return;

		String toSend = this.serializeEntity(removedE, false, false);
		mwclient.serverSend("IPU|" + toSend);
	}
	
  public void gamePlayerConnected(GamePlayerConnectedEvent e) {}
  public void gamePlayerDisconnected(GamePlayerDisconnectedEvent e) {}
  public void gamePlayerChange(GamePlayerChangeEvent e) {}
  public void gamePlayerChat(GamePlayerChatEvent e) {}

  public void gameReport(GameReportEvent e) {}
  public void gameEnd(GameEndEvent e) {}

  public void gameBoardNew(GameBoardNewEvent e) {}
  public void gameBoardChanged(GameBoardChangeEvent e) {}
  public void gameSettingsChange(GameSettingsChangeEvent e) {}
  public void gameMapQuery(GameMapQueryEvent e) {}

  public void gameEntityNew(GameEntityNewEvent e) {}
  public void gameEntityNewOffboard(GameEntityNewOffboardEvent e) {}
  public void gameEntityChange(GameEntityChangeEvent e) {}
  public void gameNewAction(GameNewActionEvent e) {}
	
	/* 
	 * from megamek.client.CloseClientListener clientClosed()
	 * Thanks to MM for adding the listener.
	 * And to MMNet for the poorly documented code change.
	 */
	public void clientClosed() {
		
	    PreferenceManager.getInstance().save();
	    
	    if (bot != null){
            bot.die();
            bot = null;
        }

	    //client.die();
	    client = null;//explicit null of the MM client. Wasn't/isn't being GC'ed.
		mwclient.closingGame(serverName);
		System.gc();
	}
	
	/**
	 * @author jtighe
	 * @param army
	 * @param slaveid
	 * @param masterid
	 * 
	 * This function goes through and makes sure the slave is linked to the master unit
	 */
	public void linkMegaMekC3Units(CArmy army, Integer slaveid, Integer masterid){
		Entity c3Unit = null;
		Entity c3Master = null;
		
		while ( c3Unit == null || c3Master == null )
		{
			try {
				if ( c3Unit == null )
					c3Unit = client.game.getEntity(slaveid);
				
				if ( c3Master == null )
					c3Master = client.game.getEntity(masterid);
				
				sleep(10);//give the queue time to refresh
			}
			catch( Exception ex){
			    MWClient.mwClientLog.clientErrLog("Error in linkMegaMekC3Units");
			    MWClient.mwClientLog.clientErrLog(ex);
			}
		}
		
		//catch for some funky stuff
		if ( c3Unit == null || c3Master == null ) {
			MWClient.mwClientLog.clientErrLog("Null Units c3Unit: "+c3Unit+" C3Master: "+c3Master);
		    return;
		}
		
		try {
		    CUnit masterUnit = (CUnit)army.getUnit(masterid.intValue());
		    //MWClient.mwClientLog.clientErrLog("Master Unit: "+masterUnit.getModelName());
//		    MWClient.mwClientLog.clientErrLog("Slave Unit: "+c3Unit.getModel());
			if ( !masterUnit.hasC3SlavesLinkedTo(army) 
			        && masterUnit.hasBeenC3LinkedTo(army) 
			        && (masterUnit.getC3Level() == Unit.C3_MASTER || masterUnit.getC3Level() == Unit.C3_MMASTER) ){
		//		MWClient.mwClientLog.clientErrLog("Unit: "+c3Master.getModel()+" id: "+c3Master.getExternalId());
				if ( c3Master.getC3MasterId() == Entity.NONE ) {
					c3Master.setShutDown(false);
					c3Master.setC3Master(c3Master);
					client.sendUpdateEntity(c3Master);
				}
				/*if ( c3Master.hasC3MM() )
					MWClient.mwClientLog.clientErrLog("hasC3MM");
				else
					MWClient.mwClientLog.clientErrLog("!hasC3MM");*/
			}else if ( c3Master.getC3MasterId() != Entity.NONE){
				c3Master.setShutDown(false);
				c3Master.setC3Master(Entity.NONE);
				client.sendUpdateEntity(c3Master);
			}
//			MWClient.mwClientLog.clientErrLog("c3Unit: "+c3Unit.getModel()+" Master: "+c3Master.getModel());
			c3Unit.setShutDown(false);
			c3Unit.setC3Master(c3Master);
	//		MWClient.mwClientLog.clientErrLog("c3Master Set to "+c3Unit.getC3MasterId()+" "+c3Unit.getC3NetId());
			client.sendUpdateEntity(c3Unit);
		} catch (Exception ex) {
			MWClient.mwClientLog.clientErrLog(ex);
			MWClient.mwClientLog.clientErrLog("Error in setting up C3Network");
		}
	}
	
	/*
	 * Taken form Megamek Code for use with MekWars The call was private
	 * and was needed. 
	 * Thanks to Ben Mazur and all of the MM coders we hope for a long
	 * and happy relation ship.
	 * 
	 * Torren.
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
	 * 
	 * Scans the boards directory for map boards of the appropriate size
	 * and returns them.
	 */
	private Vector scanForBoards(int boardWidth, int boardHeight) {
		Vector<String> boards = new Vector<String>();
		//Board Board = client.game.getBoard();
		File boardDir = new File("data/boards");
		
		// just a check...
		if (!boardDir.isDirectory()) {
			return boards;
		}
		
		// scan files
		String[] fileList = boardDir.list();
		Vector<String> tempList = new Vector<String>();
		Comparator<? super String> sortComp = ClientThread.stringComparator();
		for (int i = 0; i < fileList.length; i++) {
			if (fileList[i].indexOf(".board") == -1) {
				continue;
			}
			if (Board.boardIsSize(fileList[i], boardWidth, boardHeight)) {
				tempList.addElement(fileList[i].substring(0, fileList[i].lastIndexOf(".board")));
			}
		}
		
		// if there are any boards, add these:
		if (tempList.size() > 0) {
			boards.addElement( MapSettings.BOARD_RANDOM );
			boards.addElement( MapSettings.BOARD_SURPRISE );
			boards.addElement(MapSettings.BOARD_GENERATED);
            Collections.sort(tempList, sortComp);
			for ( int loop = 0; loop < tempList.size(); loop++ ) {
				boards.addElement( tempList.elementAt(loop) );
			}
		} else {
			boards.addElement(MapSettings.BOARD_GENERATED);
		}
		
		return boards;
	}

    private Vector<BuildingTemplate> generateRandomBuildings(MapSettings mapSettings, Buildings buildingTemplate){
        
        Vector<BuildingTemplate> buildingList = new Vector<BuildingTemplate>();
        Vector<String> buildingTypes = new Vector<String>();
        
        int width = mapSettings.getBoardWidth();
        int height = mapSettings.getBoardHeight();
        int minHeight = 0;
        int minWidth = 0;
        
        switch(buildingTemplate.getStartingEdge()){
        case Buildings.NORTH:
            height = 5;
            minHeight = 1;
            break;
        case Buildings.SOUTH:
            if ( height > 5)
                minHeight = height - 5;
            height = 5;
            break;
        case Buildings.EAST:
            if ( width > 5)
                minWidth = width - 5;
            width = 5;
            break;
        case Buildings.WEST:
            width = 5;
            minWidth = 1;
            break;
        default:
            break;
        }
        
        StringTokenizer types = new StringTokenizer(buildingTemplate.getBuildingType(),",");
        
        while ( types.hasMoreTokens() )
            buildingTypes.add(types.nextToken());
        
        int typeSize = buildingTypes.size();

        Random r = new Random();

        TreeSet<String> tempMap = new TreeSet<String>();
        Coords coord = new Coords();
        String stringCoord = "";
        
        for ( int count = 0; count < buildingTemplate.getTotalBuildings(); count++){
            int loops = 0;
            boolean CFx2 = false;
            Vector<Coords> coordList = new Vector<Coords>();
            do{
                if ( loops++ > 100 ){
                    CFx2 = true;
                    break;
                }

                int x = r.nextInt(width)+minWidth;
                int y = r.nextInt(height)+minHeight;
                
                
                if ( x >= mapSettings.getBoardWidth() )
                    x = mapSettings.getBoardWidth()-2;
                else if ( x <= 1 )
                    x = 2;
                
                if ( y >= mapSettings.getBoardHeight() )
                    y = mapSettings.getBoardHeight()-2;
                else if ( y <= 1)
                    y = 2;
    
                coord = new Coords(x,y);
                    
                stringCoord = x+","+y;
            }while(tempMap.contains(stringCoord) );
                
            tempMap.add(stringCoord);
            coordList.add(coord);

            int floors = buildingTemplate.getMaxFloors()-buildingTemplate.getMinFloors();
            
            if ( floors <= 0 )
                floors = buildingTemplate.getMinFloors();
            else
                floors = r.nextInt(floors)+buildingTemplate.getMinFloors();
            
            int totalCF = buildingTemplate.getMaxCF()-buildingTemplate.getMinCF();
            
            if ( totalCF <= 0)
                totalCF = buildingTemplate.getMinCF();
            else
                totalCF = r.nextInt(totalCF)+buildingTemplate.getMinCF();
            
            if ( CFx2 )
                totalCF *= 2;
            
            
            int type = 1;
            try{
                if (typeSize == 1 )
                    type = Integer.parseInt(buildingTypes.elementAt(0));
                else 
                    type = Integer.parseInt(buildingTypes.elementAt(r.nextInt(typeSize)));
            }catch(Exception ex){} //someone entered a bad building type.
            
            buildingList.add(new BuildingTemplate(type,coordList,totalCF,floors,-1));
        }
        
        return buildingList;
    }
    
    public int getBuildingsLeft(){
        Enumeration buildings = client.game.getBoard().getBuildings();
        int buildingCount = 0;
        while ( buildings.hasMoreElements() ){
            buildings.nextElement();
            buildingCount++;
        }
        return buildingCount;
    }
    
}