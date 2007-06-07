/*
 * MekWars - Copyright (C) 2004 
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

package server.campaign.commands.admin;

import java.util.Iterator;
import java.util.StringTokenizer;
import server.campaign.CampaignMain;
import server.campaign.SHouse;
import server.campaign.SPlayer;
import server.campaign.commands.Command;
import server.MWChatServer.auth.IAuthenticator;

public class AdminLockCampaignCommand implements Command {
	
	int accessLevel = IAuthenticator.ADMIN;
	public int getExecutionLevel(){return accessLevel;}
	public void setExecutionLevel(int i) {accessLevel = i;}
	
	public void process(StringTokenizer command,String Username) {
		
		//access level check
		int userLevel = CampaignMain.cm.getServer().getUserLevel(Username);
		if(userLevel < getExecutionLevel()) {
			CampaignMain.cm.toUser("Insufficient access level for command. Level: " + userLevel + ". Required: " + accessLevel + ".",Username,true);
			return;
		}
		
		if (new Boolean(CampaignMain.cm.getConfig("CampaignLock")).booleanValue() == true) {
			CampaignMain.cm.toUser("Campaign is already locked.",Username,true);
			return;
		}
		
		//deactivate all active players, and tell them why.
		Iterator e = CampaignMain.cm.getData().getAllHouses().iterator();
		while (e.hasNext()) {
			SHouse h = (SHouse)e.next();
			for (SPlayer p : h.getActivePlayers().values()) {
				p.setActive(false);
				CampaignMain.cm.toUser(Username + " locked the campaign. You were deactivated.",p.getName(),true);
				CampaignMain.cm.sendPlayerStatusUpdate(p,!new Boolean(CampaignMain.cm.getConfig("HideActiveStatus")).booleanValue());
			}//end while (act members remain)
			
		}//end while(factions remain)
		
		//set the lock property, so no new players can activate
		CampaignMain.cm.getConfig().setProperty("CampaignLock","true");
		
		//tell the admin he has locked the campaign
		CampaignMain.cm.doSendToAllOnlinePlayers(Username + " locked the campaign!", true);
		CampaignMain.cm.toUser("You locked the campaign. Players can no longer activate, and all active " +
				"players were deactivated. Use 'adminunlockcampaign' to release the activity lock.",Username,true);
		CampaignMain.cm.doSendModMail("NOTE",Username + " locked the campaign.");
		
	}//end Process()
	
}