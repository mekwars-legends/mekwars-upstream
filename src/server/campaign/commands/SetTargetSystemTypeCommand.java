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

package server.campaign.commands;


import java.util.StringTokenizer;
import server.campaign.SPlayer;
import server.campaign.CampaignMain;
import server.campaign.SUnit;

import megamek.common.Entity;

public class SetTargetSystemTypeCommand implements Command {
	
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
		
		int unitid = 0;//ID# of the mech which is to set searchlight;
		int targetSystemType = 0;//standard
        
		try {
			unitid= Integer.parseInt(command.nextToken());
		}//end try
		catch (NumberFormatException ex) {
			CampaignMain.cm.toUser("SetTargetSystemType command failed. Check your input. It should be something like this: /c SetTargetSystemType#unitid#targettype",Username,true);
			return;
		}//end catch
		
		try {
            targetSystemType = Integer.parseInt(command.nextToken());
		}//end try
		catch (Exception ex){
			CampaignMain.cm.toUser("SetTargetSystemType command failed. Check your input. It should be something like this: /c SetTargetSystemType#unitid#targettype",Username,true);
			return;
		}//end catch
		
		if ( CampaignMain.cm.getData().getBannedTargetingSystems().containsKey(targetSystemType) ){
            CampaignMain.cm.toUser("Your techs regret to inform you that your unit's electronics cannot support that type of targeting system.",Username,true);
            return;
        }
		SUnit unit = p.getUnit(unitid);
		Entity en = unit.getEntity();
        en.setTargSysType(targetSystemType);
		unit.setEntity(en);
		CampaignMain.cm.toUser("Targeting Sytem set for "+ unit.getModelName(),Username,true);
		
	}//end process() 
}//end SetSearchLightCommand class

