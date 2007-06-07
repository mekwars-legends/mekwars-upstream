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

import java.util.StringTokenizer;
import server.campaign.CampaignMain;
import server.campaign.commands.Command;
import server.MWChatServer.auth.IAuthenticator;

public class AdminUnlockCampaignCommand implements Command {
	
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
		
		if (new Boolean(CampaignMain.cm.getConfig("CampaignLock")).booleanValue() != true) {
			CampaignMain.cm.toUser("Campaign is already unlocked.",Username,true);
			return;
		}
		
		//reset the lock property so players can activate
		CampaignMain.cm.getConfig().setProperty("CampaignLock","false");
		
		//tell the admin he has unlocked the campaign
        CampaignMain.cm.doSendToAllOnlinePlayers(Username + " unlocked the campaign!", true);
		CampaignMain.cm.toUser("You unlocked the campaign. Players may now activate.",Username,true);
		CampaignMain.cm.doSendModMail("NOTE",Username + " unlocked the campaign");
		
	}//end Process()
	
}