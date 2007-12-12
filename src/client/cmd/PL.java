/*
 * MekWars - Copyright (C) 2004 
 * 
 * Derived from MegaMekNET (http://www.sourceforge.net/projects/megamek)
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

package client.cmd;

import java.util.StringTokenizer;

import common.campaign.pilot.Pilot;
import common.util.UnitUtils;

import client.MWClient;
import client.campaign.CPlayer;
import client.campaign.CUnit;
import client.gui.dialog.AdvancedRepairDialog;

/**
 * @author Imi (immanuel.scholz@gmx.de)
 */

public class PL extends Command {
	
	/**
	 * @param client
	 */
	public PL(MWClient mwclient) {
		super(mwclient);
	}
	
	/**
	 * @see client.cmd.Command#execute(java.lang.String)
	 */
	@Override
	public void execute(String input) {
		StringTokenizer st = decode(input);
		
		String cmd = st.nextToken();
		CPlayer player = mwclient.getPlayer();
		
		if (!st.hasMoreTokens())
			return;

		if (cmd.equals("FCU")){
        	mwclient.updateClient();
        	return;
        }
		
		if (cmd.equals("RA")) // Remove army PL|RA|
			player.removeArmy(Integer.parseInt(st.nextToken()));
		else if (cmd.equals("LA"))
			player.playerLockArmy(Integer.parseInt(st.nextToken()));
		else if (cmd.equals("ULA"))
			player.playerUnlockArmy(Integer.parseInt(st.nextToken()));
		else if (cmd.equals("SAD")) // New army. PL|SAD|army data
			player.setArmyData(st.nextToken());
		else if (cmd.equals("SABV")) // New army bv PL|army ID|new BV
			player.setArmyBV(st.nextToken());
		else if (cmd.equals("AAU")) // add unit to army PL|AAU|armyid$unitid
			player.addArmyUnit(st.nextToken());
		else if (cmd.equals("RAU")) // remove unit from army PL|RAU|armyid$unitid
			player.removeArmyUnit(st.nextToken());
		else if (cmd.equals("HD")) // hangar data feed PL|HD|<hangar string>. also used to add single new units.
			player.setHangarData(st.nextToken());
		else if (cmd.equals("RU")) // Remove hangar unit PL|RHU|id
			player.removeUnit(Integer.parseInt(st.nextToken()));
		else if (cmd.equals("SE")) // set experience PL|SE|<amount>
			player.setExp(Integer.parseInt(st.nextToken()));
		else if (cmd.equals("SM")) // set money PL|SM|<amount>
			player.setMoney(Integer.parseInt(st.nextToken()));
		else if (cmd.equals("SB")) // set bays PL|SB|<amount>
			player.setBays(Integer.parseInt(st.nextToken()));
		else if (cmd.equals("SF")) // set free bays PL|SF|<amount>
			player.setFreeBays(Integer.parseInt(st.nextToken()));
		else if (cmd.equals("SI")) // set influence PL|SI|<amount>
			player.setInfluence(Integer.parseInt(st.nextToken()));
		else if (cmd.equals("SR")) // set rating PL|SR|<amount>
			player.setRating(Double.parseDouble(st.nextToken()));
		else if (cmd.equals("SRP")) // set reward points PL|SRP|<amount>
			player.setRewardPoints(Integer.parseInt(st.nextToken()));
		else if (cmd.equals("SH")) // set faction PL|SH|<faction string>
			player.setHouse(st.nextToken());
		else if (cmd.equals("ST")) // set Technicians PL|ST|<number of techs>
			player.setTechnicians(Integer.parseInt(st.nextToken()));
		else if (cmd.equals("AAA"))//incoming autoarmy
			player.setAutoArmy(st);//give it the whole tokenizer
		else if (cmd.equals("AAM"))//incoming mines
			player.setMines(st);//give it the whole tokenizer
		else if (cmd.equals("GEA"))//incoming GunEmplacementArmy
			player.setAutoGunEmplacements(st);//give it the whole tokenizer
		else if (cmd.equals("SUS"))//set unit status (maintained, unmaintained, for sale, etc)
			player.setUnitStatus(st.nextToken());
		else if (cmd.equals("RNA"))
			player.setArmyName(st.nextToken());
		else if (cmd.equals("SAB")) //set Army bounds PL|SAB|Armyid#lowerlimit#upperlimit
			player.setArmyLimit(st.nextToken());
		else if (cmd.equals("SAL")) //set if the army is locked or not PL|SAL|armyid#true/false
			player.setArmyLock(st.nextToken());
		else if (cmd.equals("UU")) //update unit PL|UU|unitdata
			player.updateUnitData(st);
		else if (cmd.equals("BMW")){ //play a sound someone won the bm.
			if(mwclient.getConfig().isParam("ENABLEBMSOUND"))
				mwclient.doPlaySound(mwclient.getConfig().getParam("SOUNDONBMWIN"));
		}
		else if (cmd.equals("PPQ")) //Personal Pilot Queue update
			player.getPersonalPilotQueue().fromString(st.nextToken());
		else if (cmd.equals("PEU")) //Player Exclude Update
			player.setPlayerExcludes(st.nextToken(),"$");
		else if(cmd.equals("AEU")) //Admin Exclude Update
			player.setAdminExcludes(st.nextToken(),"$");
		else if(cmd.equals("RPU"))//Re-Position Unit
			player.repositionArmyUnit(st.nextToken());
		else if(cmd.equals("UOE"))//update ops eligibility
			player.updateOperations(st.nextToken());
		else if(cmd.equals("UTT"))//update ops eligibility
			player.updateTotalTechs(st.nextToken());
		else if(cmd.equals("UAT"))//update ops eligibility
			player.updateAvailableTechs(st.nextToken());
		else if(cmd.equals("GBB"))//Go bye bye
			mwclient.getConnector().closeConnection();
		else if(cmd.equals("UB"))//Using Bots
			mwclient.setUsingBots(Boolean.parseBoolean(st.nextToken()));
		else if(cmd.equals("BOST"))//Bots On the Same Team
			mwclient.setBotsOnSameTeam(Boolean.parseBoolean(st.nextToken()));
		else if(cmd.equals("SHFF"))//Players house fighting for
			player.setHouseFightingFor(st.nextToken());
		else if(cmd.equals("SUL")){//Players Unit Logo
			player.setLogo(st.nextToken());
			mwclient.getMainFrame().getMainPanel().getPlayerPanel().refresh();
		}
        else if(cmd.equals("AP2PPQ"))//adding a single pilot back to the players PPQ
            player.getPersonalPilotQueue().addPilot(st);
        else if(cmd.equals("RPPPQ"))//Remove a pilot from the players pilot queue
            player.getPersonalPilotQueue().removePilot(st);
        else if(cmd.equals("RSOD"))//Retrieve Short Op Data
            mwclient.retrieveOpData("short",st.nextToken());
        else if(cmd.equals("UCP"))//Update/Set a clients param
            mwclient.updateParam(st);
        else if(cmd.equals("SOFL"))//Server Op Flags
            mwclient.setServerOpFlags(st);
        else if(cmd.equals("SAOFS"))//Set Army Op Force Size
            player.setArmyOpForceSize(st.nextToken());
        else if(cmd.equals("FC"))//Set Faction Configs
            player.setFactionConfigs(st.nextToken());
        else if(cmd.equals("UPBM"))//Set Faction Configs
            mwclient.updatePartsBlackMarket(st.nextToken());
        else if(cmd.equals("UPPC"))//Set Faction Configs
            mwclient.updatePlayerPartsCache(st.nextToken());
        else if (cmd.equals("RPPC"))
        	mwclient.getPlayer().getPartsCache().fromString(st);
        else if (cmd.equals("STN"))
        	mwclient.getPlayer().setTeamNumber(Integer.parseInt(st.nextToken()));
        else if ( cmd.equals("VUI") ){
        	StringTokenizer data = new StringTokenizer(st.nextToken(),"#");
			String filename = data.nextToken();
			int BV = Integer.parseInt(data.nextToken());
			int gunnery = Integer.parseInt(data.nextToken());
			int piloting = Integer.parseInt(data.nextToken());
			String damage = "";
			
			if ( data.hasMoreElements() )
				damage = data.nextToken();
			
			mwclient.getMainFrame().getMainPanel().getHSPanel().showInfoWindow(filename, BV, gunnery, piloting, damage);
        }
        else if ( cmd.equals("VURD") ){
        	StringTokenizer data = new StringTokenizer(st.nextToken(),"#");
			String filename = data.nextToken();
			String damage = data.nextToken();
			CUnit unit = new CUnit(mwclient);
			
			unit.setUnitFilename(filename);
			unit.createEntity();
			unit.setPilot(new Pilot("Jeeves",4,5));
			UnitUtils.applyBattleDamage(unit.getEntity(), damage);
			new AdvancedRepairDialog(mwclient,unit,unit.getEntity(),false);
        }
        else if ( cmd.equals("CPPC") ) {
        	mwclient.getPlayer().getPartsCache().clear();
        }
        else if ( cmd.equals("UDAO") ) {
        	mwclient.updateOpData();
        }
        else if ( cmd.equals("RMF") ) {
        	mwclient.retrieveMul(st.nextToken());
        }
        else if ( cmd.equals("SMFD") ){
        	mwclient.getMainFrame().showMulFileList(st.nextToken());
        }
        else if ( cmd.equals("CAFM") ){
        	mwclient.getMainFrame().createArmyFromMul(st.nextToken());
        }
        else if (cmd.equals("USU") ) {
        	// Update Supported Units
        	while(st.hasMoreTokens()) {
        		boolean addSupport = Boolean.parseBoolean(st.nextToken());
        		String unitName = st.nextToken();
        		if(unitName != null) {
        			if(addSupport) {
        				player.getMyHouse().addUnitSupported(unitName);
        			} else {
        				player.getMyHouse().removeUnitSupported(unitName);
        			}
        		}
        	}
        	MWClient.mwClientLog.clientOutputLog(player.getMyHouse().getSupportedUnits().toString());       	
        }
        else if (cmd.equals("CSU")) {
        	// clear supported units
        	MWClient.mwClientLog.clientOutputLog("Clearing Supported Units");
        	player.getMyHouse().supportedUnits.clear();
        	player.getMyHouse().setNonFactionUnitsCostMore(Boolean.parseBoolean(mwclient.getserverConfigs("UseNonFactionUnitsIncreasedTechs")));
        }
        else if (cmd.equals("SMA")){
        	mwclient.getPlayer().setMULCreatedArmy(st);
        }
		else
			return;
		
		mwclient.refreshGUI(MWClient.REFRESH_HQPANEL);
		mwclient.refreshGUI(MWClient.REFRESH_PLAYERPANEL);
		mwclient.refreshGUI(MWClient.REFRESH_BMPANEL);
	}
}
