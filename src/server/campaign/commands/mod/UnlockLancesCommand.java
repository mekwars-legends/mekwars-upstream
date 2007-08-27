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

package server.campaign.commands.mod;

import java.util.StringTokenizer;

import server.campaign.commands.Command;
import server.campaign.CampaignMain;
import server.campaign.SArmy;
import server.campaign.SPlayer;

import server.MWChatServer.auth.IAuthenticator;

public class UnlockLancesCommand implements Command {
	
	int accessLevel = IAuthenticator.MODERATOR;
	public int getExecutionLevel(){return accessLevel;}
	public void setExecutionLevel(int i) {accessLevel = i;}
	
	public void process(StringTokenizer command,String Username) {
		
		//access level check
		int userLevel = CampaignMain.cm.getServer().getUserLevel(Username);
		if(userLevel < getExecutionLevel()) {
			CampaignMain.cm.toUser("Insufficient access level for command. Level: " + userLevel + ". Required: " + accessLevel + ".",Username,true);
			return;
		}
		
		//get the player
		SPlayer p = CampaignMain.cm.getPlayer(command.nextToken());
		
		//unlock all of his armies
		int count = p.getArmies().size();
		for (int i = 0; i < count; i++) {
			SArmy currArmy = p.getArmies().elementAt(i);
			currArmy.setLocked(false);
            CampaignMain.cm.toUser("PL|SAL|"+i+"#"+false,p.getName(),false);
		}
		
		CampaignMain.cm.toUser("You unlocked " + p.getName() + "'s armies.",Username,true);
		//server.MWServ.mwlog.modLog(Username + " unlocked " + p.getName() + "'s armies.");
		CampaignMain.cm.doSendModMail("NOTE",Username + " unlocked " + p.getName() + "'s armies.");
		//server.MWServ.mwlog.modLog(Username + " unlocked " + p.getName() + "'s armies.");
		
	}
}