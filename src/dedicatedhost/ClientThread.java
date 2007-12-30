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

package dedicatedhost;

import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import dedicatedhost.util.SerializeEntity;

import common.MegaMekPilotOption;
import common.MMGame;
import common.Unit;
import common.campaign.pilot.skills.PilotSkill;
import common.util.UnitUtils;

import megamek.client.Client;
import megamek.client.bot.BotClient;
import megamek.client.ui.AWT.ClientGUI;
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
import megamek.common.Mech;
import megamek.common.options.GameOptions;
import megamek.common.options.IBasicOption;
import megamek.common.options.Option;
import megamek.common.options.PilotOptions;
import megamek.common.options.IOption;
import megamek.common.options.IOptionGroup;
import megamek.common.Building;
import megamek.common.IGame;
import megamek.common.Pilot;
import megamek.common.MechWarrior;
import megamek.client.CloseClientListener;
import megamek.common.preference.IClientPreferences;
import megamek.common.preference.PreferenceManager;


class ClientThread extends Thread implements GameListener, CloseClientListener  {
	
	//VARIABLES
	private String myname;
	private String serverip;
	private String serverName;
	private int serverport;
	private MWDedHost mwdedhost;
	private Client client;
    private ClientGUI gui;
	private int turn = 0;
    BotClient bot = null;
    private int currentPhase = IGame.PHASE_DEPLOYMENT;
    
    final int N  = 0;
    final int NE = 1;
    final int SE = 2;
    final int S  = 3;
    final int SW = 4;
    final int NW = 5;

    
    //CONSTRUCTOR	
	public ClientThread(String name, String servername, String ip, int port, MWDedHost mwdedhost) {
		myname = name;
		serverName = servername;
		serverip = ip;
		serverport = port;
		this.mwdedhost = mwdedhost;
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
		client = new Client(myname, serverip, serverport);
		client.game.addGameListener(this);
		client.addCloseClientListener(this);
		gui = new ClientGUI(client);
		gui.initialize();
		//client.game.getOptions().
        Vector<IBasicOption> xmlGameOptions = new Vector<IBasicOption>(1,1);
        Vector<IOption> loadOptions = client.game.getOptions().loadOptions();
        
        //Load Defaults first.
        Enumeration<IOption> options = client.game.getOptions().getOptions();
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
        
        xmlGameOptions = sortAndShrinkGameOptions(xmlGameOptions, loadOptions, this.mwdedhost.getGameOptions());
        
		try {
			client.connect();
		} catch (Exception ex) {
			client = null;
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

			IClientPreferences cs = PreferenceManager.getClientPreferences();
			cs.setStampFilenames(Boolean.parseBoolean(mwdedhost.getserverConfigs("MMTimeStampLogFile")));
			cs.setShowUnitId(Boolean.parseBoolean(mwdedhost.getserverConfigs("MMShowUnitId")));
			cs.setKeepGameLog(Boolean.parseBoolean(mwdedhost.getserverConfigs("MMKeepGameLog")));
			cs.setGameLogFilename(mwdedhost.getserverConfigs("MMGameLogName"));

		} catch (Exception e) {
			MWDedHost.MWDedHostLog.clientErrLog(e);
		}
	}
	
	/**
	 * redundant code since MM does not always send a discon event.
	 */
	public void gamePlayerStatusChange(GameEvent e) {}
	
	public void gameTurnChange(GameTurnChangeEvent e) {
		if (client != null) {
			if (this.getTurn() == 0 && (myname.equals(serverName) || serverName.startsWith("[Dedicated]")))
				mwdedhost.serverSend("SHS|" + serverName + "|Running");
			else if ( client.game.getPhase() != currentPhase
					&& client.game.getOptions().booleanOption("paranoid_autosave") 
					&& !client.getLocalPlayer().isObserver()){
				sendServerGameUpdate();
				currentPhase = client.game.getPhase();
			}
			turn += 1;
			
		}
	}
	
	public void gamePhaseChange(GamePhaseChangeEvent e) {
		
		//String result = "";
		//String winnerName ="";
		//String name = "";
		
		try{
			
			if (client.game.getPhase() == IGame.PHASE_VICTORY) {

                //Make sure the player is fully connected.
                while (client.getLocalPlayer() == null) {
                    sleep(50);
                }


                //clear out everything.
                //clear out everything from this game
                //get rid of any and all bots.
                for (Iterator<Client> i = gui.getBots().values().iterator(); i.hasNext();) {
                    i.next().die();
                }
                gui.getBots().clear();
                
                //observers need not report
				if (client.game.getAllEntitiesOwnedBy(client.getLocalPlayer()) < 1)
					return;

				MMGame toUse = mwdedhost.getServers().get(serverName);
                mwdedhost.serverSend("SGR|"+toUse.getHostName());
				MWDedHost.MWDedHostLog.clientOutputLog("GAME END");
				
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
			else if( client.game.getPhase() == IGame.PHASE_END_REPORT ) {
				
				//observers need not report
				if (client.getLocalPlayer().isObserver())
					return;
				

				sendServerGameUpdate();
				
				/*
				 * Wrecked entities include all those which were
				 * devastated, ejected, or removed but salvagable
				 * according to MegaMek. We want to check them for
				 * CT-cores.
				 *
				Enumeration en = client.game.getWreckedEntities();
				while (en.hasMoreElements()) {
					Entity currEntity = (Entity)en.nextElement();
					
					/*
					 * MM is now reporting post-attack IS instead of pre-attack IS
					 * in gameEntityRemove, so this check shouldn't be necessary anymore.
					 *
					//if (currEntity instanceof Mech || currEntity instanceof QuadMech) {
					//	//if a mech-type, override grouping if its salvage and CT 0
					//	if (currEntity.getInternal(Mech.LOC_CT) <= 0)
					//		mwdedhost.serverSend("IPU|" + this.serializeEntity(currEntity, false, true));
					//	
					//}
					
					if (currEntity instanceof MechWarrior)
						mwdedhost.serverSend("IPU|" + this.serializeEntity(currEntity, false, false));
				}
				
				//constantly update the onfield warriors.
				en = client.game.getEntities();
				while (en.hasMoreElements()) {
					Entity currEntity = (Entity)en.nextElement();
                    if ( currEntity.getOwner().getName().startsWith("War Bot"))
                        continue;
					if (currEntity instanceof MechWarrior)
						mwdedhost.serverSend("IPU|" + this.serializeEntity(currEntity, false, false));
				}
				
				/*
				 * This is probably extraneous - retreats should be properly handled
				 * in the movement phase and do not involve damage transferal which
				 * could lead to a final status different from the removal status.
				 *
				//en = client.game.getRetreatedEntities();
				//while (en.hasMoreElements()) {
				//	Entity currEntity = (Entity)en.nextElement();
				//	mwdedhost.serverSend("IPU|" + this.serializeEntity(currEntity, false));
				//}*/
			}
			
		}//end try
		catch (Exception ex){
			MWDedHost.MWDedHostLog.clientErrLog("Error reporting game!");
			MWDedHost.MWDedHostLog.clientErrLog(ex);
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

		String toSend = SerializeEntity.serializeEntity(removedE, true, false,mwdedhost.isUsingAdvanceRepairs());
		mwdedhost.serverSend("IPU|" + toSend);
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
		mwdedhost.closingGame(serverName);
		System.gc();
	}
	
	public static Comparator<? super Object> stringComparator() {
        return new Comparator<Object>() {
            public int compare(Object o1, Object o2) {
                String s1 = ((String) o1).toLowerCase();
                String s2 = ((String) o2).toLowerCase();
                return s1.compareTo(s2);
            }
        };
    }


	public int getBuildingsLeft(){
        Enumeration<Building> buildings = client.game.getBoard().getBuildings();
        int buildingCount = 0;
        while ( buildings.hasMoreElements() ){
            buildings.nextElement();
            buildingCount++;
        }
        return buildingCount;
    }
    
    private void sendServerGameUpdate(){
    	//Only send data for units currently on the board.
    	//any units removed from play will have already sent thier final update.
    	Enumeration<Entity> en = client.game.getEntities();
		while (en.hasMoreElements()) {
			Entity ent = en.nextElement();
			if ( ent.getOwner().getName().startsWith("War Bot")
					|| ( !(ent instanceof MechWarrior) 
							&& !UnitUtils.hasArmorDamage(ent)
							&& !UnitUtils.hasISDamage(ent)
							&& !UnitUtils.hasCriticalDamage(ent)
							&& !UnitUtils.hasLowAmmo(ent)
							&& !UnitUtils.hasEmptyAmmo(ent)))
				continue;
			if (ent instanceof Mech && ent.getInternal(Mech.LOC_CT) <= 0)
				mwdedhost.serverSend("IPU|"+SerializeEntity.serializeEntity(ent, true, true,mwdedhost.isUsingAdvanceRepairs()));          
			else
				mwdedhost.serverSend("IPU|"+SerializeEntity.serializeEntity(ent, true, false,mwdedhost.isUsingAdvanceRepairs()));
		}
    }
    
    private Vector<IBasicOption> sortAndShrinkGameOptions(Vector<IBasicOption> defaults, Vector<IOption>serverGameOptions, Vector<IOption>OperationGameOptions){
    	
    	Vector<IBasicOption> returnedOptions = new Vector<IBasicOption>(OperationGameOptions.size(),1);
    	Hashtable<String, IBasicOption> gameHash = new Hashtable<String, IBasicOption>();
    	

    	//Start with a base of Server options
		for (IOption option: serverGameOptions){
			gameHash.put(option.getName(),option);
		}
		//Over write the server options with the Operation options
    	for (IOption option:OperationGameOptions){
			gameHash.put(option.getName(),option);
    	}
    	
    	//Only add options to the return list that are different from the game defaults.
    	for (IBasicOption option: defaults){
    		
    		IBasicOption currentOption = gameHash.get(option.getName() );
 
    		if ( currentOption != null && !option.getValue().toString().equals(currentOption.getValue().toString()) ){
				returnedOptions.add(currentOption);
    		}
    	}

    	returnedOptions.trimToSize();
    	
    	return returnedOptions;
    }
    
    public Pilot createEntityPilot(Unit mek){
	    //get and set the options
	    IOptionGroup group = null;
		Pilot pilot = null;
	    pilot = new Pilot(mek.getPilot().getName(), mek.getPilot().getGunnery(), mek.getPilot().getPiloting());
	    
	    //Hits defaults to 0 so no reason to keep checking over and over again.
	    pilot.setHits(mek.getPilot().getHits());
		//No reason to keep searching for the same group over and over and over again
		//find it once and search through it each time for the pilots skill
		for (Enumeration<IOptionGroup> enumeration = pilot.getOptions().getGroups(); enumeration.hasMoreElements();) {
			group = enumeration.nextElement();
		    //MWDedHost.MWDedHostLog.clientErrLog("Checking: " + pilot.getName()+" Key: "+group.getKey());
			if (group.getKey().equalsIgnoreCase(PilotOptions.LVL3_ADVANTAGES)) break;
		}
		
		Iterator<?> iter = mek.getPilot().getMegamekOptions().iterator();
		while (iter.hasNext()) {
		    MegaMekPilotOption po = (MegaMekPilotOption) iter.next();
			for (Enumeration<IOption> j = group.getOptions(); j.hasMoreElements();) {
				IOption option = j.nextElement();
				//MWDedHost.MWDedHostLog.clientErrLog("Unit: "+mek.getModelName()+" Checking: " + option.getName() + " with " + po.getMmname());
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
	    
	    
	    return pilot;
    }
}