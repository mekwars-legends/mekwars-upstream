/*
 * MekWars - Copyright (C) 2006
 * 
 * Original author - Jason Tighe (torren@users.sourceforge.net)
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 */

package server.campaign.commands.admin;

import java.util.StringTokenizer;


import server.MMServ;
import server.campaign.CampaignMain;
import server.campaign.commands.Command;
import server.MWChatServer.auth.IAuthenticator;
import server.util.MMNetPasswd;


/**
 * Moving the Shutdown command from MMServ into the normal command structure.
 *
 * Syntax  /c Shutdown
 */
public class ShutdownCommand implements Command {
	
	int accessLevel = IAuthenticator.ADMIN;
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
        CampaignMain.cm.getMarket().removeAllListings();
        CampaignMain.cm.forceSavePlayers(Username);
        if ( CampaignMain.cm.isUsingMySQL() )
        	CampaignMain.cm.MySQL.closeMySQL();
        CampaignMain.cm.toUser("You halted the server. Have a nice day.", Username,true);
        MMServ.mmlog.infoLog(Username + " halted the server. Have a nice day!");
        CampaignMain.cm.addToNewsFeed("Server halted!");
        try {
            MMNetPasswd.save();
        } catch(Exception ex) {
            MMServ.mmlog.errLog("Unable to save passwords before shutdown!");
            MMServ.mmlog.errLog(ex);
        }
        
        System.exit(0);
	}
}