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

package server.campaign.commands;

import java.util.StringTokenizer;

import server.MWClientInfo;
import server.campaign.CampaignMain;
import server.campaign.SPlayer;
import server.campaign.commands.Command;


/**
 * Moving the Me command from MWServ into the normal command structure.
 *
 * Syntax  /c me blah
 */
public class MeCommand implements Command {
	
	int accessLevel = 0;
	String syntax = "";
	public int getExecutionLevel(){return accessLevel;}
	public void setExecutionLevel(int i) {accessLevel = i;}
	public String getSyntax() { return syntax;}
	
	public void process(StringTokenizer command,String Username) {
		
		if (accessLevel != 0) {
			int userLevel = CampaignMain.cm.getServer().getUserLevel(Username);
			if(userLevel < getExecutionLevel()) {
				CampaignMain.cm.toUser("Insufficient access level for command. Level: " + userLevel + ". Required: " + accessLevel + ".",Username,true);
				return;
			}
		}
        
        StringBuilder buffer = new StringBuilder();
        
        //grab the first command
        if ( command.hasMoreTokens() )
        	buffer.append(command.nextToken());
        
        while (command.hasMoreTokens()){
        	buffer.append("#");
            buffer.append(command.nextToken());
        }

        
        StringTokenizer channels = new StringTokenizer(buffer.toString(),"|");

        String toSend = "";
        String channel = "";

        if ( channels.hasMoreTokens() )
        	toSend = channels.nextToken();
        else
        	toSend = buffer.toString();
        
    	if (toSend.trim().length() == 0)
			return;
        if (channels.hasMoreTokens())
        	channel = channels.nextToken();

        //if client is somehow null, just send the message
        MWClientInfo client = CampaignMain.cm.getServer().getUser(Username);
        if (client == null) {
        	CampaignMain.cm.doSendToAllOnlinePlayers(Username + "|#me " + toSend,true);
        	return;
        }
        
        //check to see if the player is muted
        boolean generalMute = CampaignMain.cm.getServer().getIgnoreList().indexOf(client.getName()) > -1;
        boolean factionMute = CampaignMain.cm.getServer().getFactionLeaderIgnoreList().indexOf(client.getName()) > -1;
       
        if (generalMute || factionMute)
            CampaignMain.cm.toUser("You've been set to ignore mode and cannot participate in chat.", Username,true);
        else
		if ( channel.equalsIgnoreCase("hm") ){
			SPlayer player = CampaignMain.cm.getPlayer(Username);
			CampaignMain.cm.doSendHouseMail(player.getHouseFightingFor(),Username,"#me " + toSend);
		}
		else if ( channel.equalsIgnoreCase("mm") ){
			CampaignMain.cm.doSendModMail(Username,"#me " + toSend);
		}
		else if ( channel.equalsIgnoreCase("ic") ){
			CampaignMain.cm.doSendToAllOnlinePlayers("(In Character)"+Username + ":#me " + toSend,true);
		}
		else if ( channel.equalsIgnoreCase("mail") ){
			String reciever = channels.nextToken();
			CampaignMain.cm.getServer().doStoreMail(reciever+",#me " + toSend, Username);
		}
		else
	        CampaignMain.cm.doSendToAllOnlinePlayers(Username+"|#me " + toSend,true);
	}
}