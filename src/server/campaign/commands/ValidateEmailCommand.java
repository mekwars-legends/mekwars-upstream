/*
 * MekWars - Copyright (C) 2004 
 * 
 * Derived from MegaMekNET (http://www.sourceforge.net/projects/megameknet)
 * Original Author - Nathan Morris (urgru)
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
import server.campaign.CampaignMain;
import server.campaign.SPlayer;

public class ValidateEmailCommand implements Command {
	
	int accessLevel = 0;
	String syntax = "";
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
		
		// Check if this is even necessary
		if(!CampaignMain.cm.isSynchingBB()) {
			CampaignMain.cm.toUser("AM:This server does not require email validation.", Username, true);
			return;
		}
		
		// Is there a validation code?

		if(!command.hasMoreTokens()) {
			CampaignMain.cm.toUser("AM:Missing validation code, please try again.  /validateemail [validation code]", Username, true);
			return;
		}
		String codeEntered = command.nextToken().trim();
		if(codeEntered.length()<1) {
			CampaignMain.cm.toUser("AM:Missing validation code, please try again.  /validateemail [validation code]", Username, true);
			return;
		}
		
		SPlayer player = CampaignMain.cm.getPlayer(Username);
		String validationCode = CampaignMain.cm.MySQL.getActivationKey(player.getForumID());
		
		if(player.isValidated()) {
			CampaignMain.cm.toUser("AM: Your account has already been validated.", Username, true);
			return;
		}
		
		if(!validationCode.equals(codeEntered)) {
			CampaignMain.cm.toUser("AM: Invalid validation code, unable to validate your account.", Username, true);
			return;
		}
		
		player.setUserValidated(true);
		CampaignMain.cm.MySQL.validateUser(player.getForumID());
		player.setSave();
		CampaignMain.cm.toUser("Your account has been validated.  Thank you.", Username, true);
		return;
	}
}