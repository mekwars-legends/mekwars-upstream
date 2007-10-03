/*
 * MekWars - Copyright (C) 2007 
 *
 * Original author - jtighe (torren@users.sourceforge.net)
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

package server.campaign.commands.mod;

import java.util.StringTokenizer;

import server.campaign.CampaignMain;
import server.campaign.SPlayer;
import server.campaign.commands.Command;
import server.MWChatServer.auth.IAuthenticator;

/**
 * Add a Leader to a faction.
 */
public class AddLeaderCommand implements Command {
	
	int accessLevel = IAuthenticator.MODERATOR;
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
		
		SPlayer player;
		
		try{
			String target = command.nextToken();
			player = CampaignMain.cm.getPlayer(target);
			player.getMyHouse().addLeader(player.getName());
			int level = CampaignMain.cm.getIntegerConfig("factionLeaderLevel");
			if ( player.getPassword().getAccess() < level )
				CampaignMain.cm.updatePlayersAccessLevel(target,level);
			CampaignMain.cm.toUser("You have been promoted to the faction leadership by "+Username+".", target);
			CampaignMain.cm.doSendHouseMail(player.getMyHouse(), "Note", player.getName()+" has been promoted to the faction leadership.");
		}catch(Exception ex){
			CampaignMain.cm.toUser("Invalid syntax: /addleader UserName", Username);
		}
	}		
}