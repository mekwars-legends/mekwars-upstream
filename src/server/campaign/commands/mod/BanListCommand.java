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

import java.util.Date;
import java.util.Iterator;
import java.util.StringTokenizer;

import server.campaign.CampaignMain;
import server.campaign.commands.Command;
import server.MWChatServer.auth.IAuthenticator;


/**
 * Moving the BanList command from MMServ into the normal command structure.
 *
 * Syntax  /c BanList
 */
public class BanListCommand implements Command {
	
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
		

	        String result = "Banned accounts:<br>";
	        int i = 1;

        synchronized (CampaignMain.cm.getServer().getBanAccounts()) {
	        Iterator<String> banNames = CampaignMain.cm.getServer().getBanAccounts().keySet().iterator();
	        Iterator<String> banTimes = CampaignMain.cm.getServer().getBanAccounts().values().iterator();
	        while (banNames.hasNext()) {
	            String banName = banNames.next();
	            String banTime = banTimes.next();
	            
	            //Banned Accounts Username Check
	            Long until = Long.valueOf(banTime);
	            
	            //If they are no longer banned remove them from the list and don't display them
	            if (until.longValue() < System.currentTimeMillis() || until.longValue() == 0) {
	                CampaignMain.cm.getServer().getBanAccounts().remove(banName);
	                CampaignMain.cm.getServer().bansUpdate();
	                continue;
	            } 
	            
	            Long l = Long.valueOf(banTime);
	            result += Integer.toString(i++);
	            result +=  ") ";
	            result +=  banName;
	            result +=  " [unban at ";
	            result +=  new Date(l).toString();
	            result +=  "]<br>";
	        }
		}
		
		i = 1;
        result += "Banned IPs:<br>";
		synchronized (CampaignMain.cm.getServer().getBanIps().keySet()) {

	        for (InetAddress currAddress : CampaignMain.cm.getServer().getBanIps().keySet()) {
	            Long l = CampaignMain.cm.getServer().getBanIps().get(currAddress);
	            
	            //If they are no longer banned remove them from the list and don't display them
	            if (l.longValue() < System.currentTimeMillis() || l.longValue() == 0) {
	                CampaignMain.cm.getServer().getBanIps().remove(currAddress);
	                CampaignMain.cm.getServer().bansUpdate();
	                continue;
	            } 
	            
	            result += i++
	            + ") "
	            + currAddress.toString()
	            + " [unban at "
	            + new Date(l.longValue()).toString()
	            + "]<br>";
	        }
		}
		
        CampaignMain.cm.toUser(result, Username);
        CampaignMain.cm.doSendModMail("NOTE",Username + " checked the ban list.");
        //MMServ.mmlog.modLog(Username + " checked the ban list.");
	}
}