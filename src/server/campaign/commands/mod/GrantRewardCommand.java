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
import server.campaign.CampaignMain;
import server.campaign.SPlayer;
import server.campaign.commands.Command;
import server.MWChatServer.auth.IAuthenticator;

public class GrantRewardCommand implements Command {
	
	int accessLevel = IAuthenticator.MODERATOR;
	public int getExecutionLevel(){return accessLevel;}
	public void setExecutionLevel(int i) {accessLevel = i;}
	
	public void process(StringTokenizer command,String Username) {
		
		int userLevel = CampaignMain.cm.getServer().getUserLevel(Username);
		if(userLevel < getExecutionLevel()) {
			CampaignMain.cm.toUser("Insufficient access level for command. Level: " + userLevel + ". Required: " + accessLevel + ".",Username,true);
			return;
		}
		
		SPlayer p = CampaignMain.cm.getPlayer(command.nextToken());
		int amount = Integer.parseInt(command.nextToken());
		if (p != null) {
			p.setReward(p.getReward() + amount);
			
			String toRecipient = Username + " granted you " + amount + " Reward Points";
			if (amount > 0)
				toRecipient += " [<a href=\"MWUSERP\">Use RP</a>]";
			toRecipient += ".";
			CampaignMain.cm.toUser(toRecipient,p.getName(),true);
			
			CampaignMain.cm.toUser("You granted " + amount + " Reward Points to " + p.getName(),Username,true);
			//server.MWServ.mwlog.modLog(Username + " granted " + amount + " Reward Points to " + p.getName());
			CampaignMain.cm.doSendModMail("NOTE",Username + " granted " + amount + " Reward Points to " + p.getName());
		}
		
	}//end process()
	
}