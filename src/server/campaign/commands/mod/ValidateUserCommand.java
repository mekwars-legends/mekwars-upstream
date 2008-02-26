/*
 * MekWars - Copyright (C) 2008 
 * 
 * Derived from MegaMekNET (http://www.sourceforge.net/projects/megameknet)
 * Original Author - Bob Eldred (BillyPinhead)
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

import server.MWChatServer.auth.IAuthenticator;
import server.campaign.CampaignMain;
import server.campaign.SPlayer;
import server.campaign.commands.Command;

/**
 * Validate a user's account, activating the user on both forums
 * and for defection.  This is only applicable for a database-enabled
 * server that is synching with phpBB.
 */
public class ValidateUserCommand implements Command {
	
	int accessLevel = IAuthenticator.MODERATOR;
	String syntax = "Player Name";
	public int getExecutionLevel(){return accessLevel;}
	public void setExecutionLevel(int i) {accessLevel = i;}
	public String getSyntax() { return syntax;}
	
	public void process(StringTokenizer command,String Username) {
		
		if (accessLevel != 0) {
			int userLevel = CampaignMain.cm.getServer().getUserLevel(Username);
			if(userLevel < getExecutionLevel()) {
				CampaignMain.cm.toUser("AM:Insufficient access level for command. Level: " + userLevel + ". Required: " + accessLevel + ".",Username,true);
				return;
			}
		}
		
		if(!CampaignMain.cm.isUsingMySQL()) {
			CampaignMain.cm.toUser("AM:This server is not database-enabled.", Username, true);
			return;
		}
		
		if(!CampaignMain.cm.isSynchingBB()) {
			CampaignMain.cm.toUser("AM:This server is not synching with phpBB.", Username, true);
			return;
		}
		
		if(!command.hasMoreTokens()) {
			CampaignMain.cm.toUser("AM:Syntax: /ValidateUser Username", Username, true);
			return;
		}
		
		String pName = command.nextToken();
		SPlayer p = CampaignMain.cm.getPlayer(pName);
		if (p == null) {
			CampaignMain.cm.toUser("AM:Unable to find player " + pName + ".", Username, true);
			return;
		}
		
		if(p.isValidated()) {
			CampaignMain.cm.toUser("AM:" + pName + " is already validated.", Username, true);
			return;
		}
		
		// Everything looks good.  Let's validate him.
		p.setUserValidated(true);
		CampaignMain.cm.MySQL.validateUser(p.getForumID());
		p.setSave();
		CampaignMain.cm.toUser("AM:" + pName + " has been validated.", Username, true);
		CampaignMain.cm.toUser("AM: " + Username + " has validated your account.", pName, true);
		CampaignMain.cm.doSendModMail("NOTE", Username + " has validated " + pName + "'s account.");
		return;
	}
}
