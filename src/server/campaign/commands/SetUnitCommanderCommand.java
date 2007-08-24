/*
 * MekWars - Copyright (C) 2007 
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

/*
 * Created on 08.22.2007
 *  
 */
package server.campaign.commands;

import java.util.StringTokenizer;

import megamek.common.VTOL;

import common.Unit;

import server.campaign.CampaignMain;
import server.campaign.SHouse;
import server.campaign.SPlayer;
import server.campaign.SUnit;

/**
 * @author Jason Tighe
 *  
 */
public class SetUnitCommanderCommand implements Command {
	
	int accessLevel = 0;
	public int getExecutionLevel(){return accessLevel;}
	public void setExecutionLevel(int i) {accessLevel = i;}
	
	public void process(StringTokenizer command,String Username) {
		
		if (accessLevel != 0) {
			int userLevel = CampaignMain.cm.getServer().getUserLevel(Username);
			if(userLevel < getExecutionLevel()) {
				CampaignMain.cm.toUser("Insufficient access level for command. Level: " + userLevel + ". Required: " + accessLevel + ".",Username,true);
				return;
			}
		}
		
		SPlayer p = CampaignMain.cm.getPlayer(Username);
		SHouse house = p.getMyHouse();
		int mechid = -1;
		boolean commander = false;
		
		try {
			mechid = Integer.parseInt(command.nextToken());
			commander = Boolean.parseBoolean(command.nextToken());
		} catch (Exception e) {
			CampaignMain.cm.toUser("Incorrect syntax. Try: /setUnitCommander unit ID#true/false", Username, true);
			return;
		}
		
		SUnit m = p.getUnit(mechid);
		if (m == null) {
			CampaignMain.cm.toUser("Could not find a unit with the given ID.", Username, true);
			return;
		}
		
		if (m.getStatus() == Unit.STATUS_FORSALE) {
			CampaignMain.cm.toUser("Units that are for sale on the Market.", Username, true);
			return;
		}
		
		if ( m.getPilot() == null || m.getPilot().getName().equalsIgnoreCase("vacant") ){
			CampaignMain.cm.toUser("This unit does not have a pilot to be a commander for!", Username);
			return;
		}
		
		if ( !house.getBooleanConfig("allowUnitCommander"+SUnit.getTypeClassDesc(m.getType())) ){
			CampaignMain.cm.toUser(SUnit.getTypeClassDesc(m.getType())+" units are not allowed to be set as unit commanders!", Username);
			return;
		}
		
		if ( !house.getBooleanConfig("allowUnitCommanderVTOL") && m.getEntity() instanceof VTOL){
			CampaignMain.cm.toUser("VTOL units are not allowed to be set as unit commanders!", Username);
			return;
		}
		
		if ( m.getEntity().isOffBoard() ){
			CampaignMain.cm.toUser("Off board units are not allowed to be set as unit commanders!", Username);
			return;
		}
		
		if ( p.getAmountOfTimesUnitExistsInArmies(m.getId()) < 1){
			CampaignMain.cm.toUser("the "+m.getModelName()+" is not in any armies!", Username);
			return;
		}
		
		m.setUnitCommander(commander);
		if(CampaignMain.cm.isUsingMySQL())
			m.toDB();
		CampaignMain.cm.toUser("PL|UU|"+m.getId()+"|"+m.toString(true),Username,false);
		if ( commander )
			CampaignMain.cm.toUser("Unit #"+m.getId()+" has been set as unit commander", Username);
		else
			CampaignMain.cm.toUser("Unit #"+m.getId()+" has been removed as unit commander", Username);
	}//end process()

}//end ScrapCommand