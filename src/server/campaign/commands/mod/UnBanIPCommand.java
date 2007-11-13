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

package server.campaign.commands.mod;

import java.net.InetAddress;
import java.util.Enumeration;
import java.util.StringTokenizer;


import server.campaign.CampaignMain;
import server.campaign.commands.Command;
import server.MWChatServer.auth.IAuthenticator;


/**
 * Moving the UnBanIP command from MWServ into the normal command structure.
 *
 * Syntax  /c UnBanIP#Number
 */
public class UnBanIPCommand implements Command {
	
	int accessLevel = IAuthenticator.MODERATOR;
	String syntax = "Number";
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
        
        try{
            int id  = Integer.parseInt(command.nextToken());
        
            Enumeration e = CampaignMain.cm.getServer().getBanIps().keys();
            InetAddress inetAddress = InetAddress.getLocalHost();
            while (id > 0) {
                inetAddress = (InetAddress) e.nextElement();
                id--;
            }
            CampaignMain.cm.getServer().getBanIps().remove(inetAddress);
            CampaignMain.cm.getServer().bansUpdate();
            CampaignMain.cm.toUser("You unbanned: " + inetAddress, Username);
            CampaignMain.cm.doSendModMail("NOTE",Username + " unbanned " + inetAddress);
        } catch(Exception ex) {
            CampaignMain.cm.toUser("Syntax: unbanip (number)<br>Where number corresponds to the number in the ipban list.", Username);
        }
	}
}