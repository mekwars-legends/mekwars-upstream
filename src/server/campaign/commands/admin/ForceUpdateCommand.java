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

import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import server.MMServ;
import server.MWChatServer.MWChatServer;
import server.campaign.CampaignMain;
import server.campaign.DefaultServerOptions;
import server.campaign.commands.Command;
import server.MWChatServer.auth.IAuthenticator;


/**
 * Allows SO's to force clients to update without a major version change
 *
 * Syntax  /c forceupdate#Key#[Player/Dedicated/All]
 * <code>Player/Dedicated/All</code> are optional and will kick those entities
 * off so that they have to update right away.
 */
public class ForceUpdateCommand implements Command {
	
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
        
        String updateKey = "";
        String whoToKick = "";
        
        try{
            updateKey = command.nextToken();
        }catch(Exception ex){
            CampaignMain.cm.toUser("You must supply a Key<br>" +
                    "Syntax  /c forceupdate#Key#[Player/Dedicated/All]<br>"+
                     "Player/Dedicated/All are optional and will kick those entities<br>"+
                     "off so that they have to update right away.",Username);
            return;
        }

        CampaignMain.cm.getConfig().setProperty("ForceUpdateKey",updateKey);
        DefaultServerOptions dso = new DefaultServerOptions();
        dso.createConfig();

        CampaignMain.cm.doSendModMail("NOTE",Username + " set the Force Update Key");
        CampaignMain.cm.toUser("Make sure to add UPDATEKEY="+updateKey+"<br>To the serverdata.dat",Username);
        if ( command.hasMoreTokens() ){
            whoToKick = command.nextToken();
            CampaignMain.cm.doSendModMail("NOTE",Username + " is kicking "+whoToKick);

            if ( whoToKick.equalsIgnoreCase("all")) {
                Set<String> users = CampaignMain.cm.getServer().getUsers().keySet();
                TreeSet<String> toKickList = new TreeSet<String>();
                synchronized (users) {
                    for ( String toKick : users)
                        toKickList.add(toKick);
                }
                for ( String toKick : toKickList ){
                    if ( CampaignMain.cm.getServer().isAdmin(toKick) )
                        continue;

                    if ( toKick.toLowerCase().startsWith("[dedicated]") ) {
                        try{
                    		CampaignMain.cm.getServer().doStoreMail(toKick+",update", Username);
                        	Thread.sleep(120);
                        }catch (Exception ex){
                            MMServ.mmlog.errLog(ex);
                        }
                        continue;
                    }

                    CampaignMain.cm.toUser("PL|GBB|Bye Bye", toKick,false);
                    //Use this to kick ghost players from the clients.
                    CampaignMain.cm.getServer().sendRemoveUserToAll(toKick,false);
                    try{
                        Thread.sleep(120);
                    }catch (Exception ex){
                        MMServ.mmlog.errLog(ex);
                    }
                    try{
                        CampaignMain.cm.doLogoutPlayer(toKick);
                        CampaignMain.cm.getOpsManager().doDisconnectCheckOnPlayer(toKick);
                        if (CampaignMain.cm.getServer().getClient(MWChatServer.clientKey(toKick)) != null) {
                            CampaignMain.cm.getServer().killClient(toKick,Username);
                        }
                        
                    }catch (Exception ex){
                        MMServ.mmlog.errLog(ex);
                    }
                }//end For
            }//end if all
            else if ( whoToKick.equalsIgnoreCase("player")) {
                Set<String> users = CampaignMain.cm.getServer().getUsers().keySet();
                TreeSet<String> toKickList = new TreeSet<String>();
                synchronized (users) {
                    for ( String toKick : users)
                        toKickList.add(toKick);
                }
                for ( String toKick : users ){
                    if ( toKick.toLowerCase().startsWith("[dedicated]") )
                        continue;
                    if ( CampaignMain.cm.getServer().isAdmin(toKick) )
                        continue;
                    CampaignMain.cm.toUser("You have been kicked by " + Username, toKick,true);
                    CampaignMain.cm.toUser("PL|GBB|Bye Bye", toKick,false);
                    //Use this to kick ghost players from the clients.
                    CampaignMain.cm.getServer().sendRemoveUserToAll(toKick,false);
                    try{
                        Thread.sleep(120);
                    }catch (Exception ex){
                        MMServ.mmlog.errLog(ex);
                    }
                    try{
                        CampaignMain.cm.doLogoutPlayer(toKick);
                        CampaignMain.cm.getOpsManager().doDisconnectCheckOnPlayer(toKick);
                        if (CampaignMain.cm.getServer().getClient(MWChatServer.clientKey(toKick)) != null) {
                            CampaignMain.cm.getServer().killClient(toKick,Username);
                        }
                        
                    }catch (Exception ex){
                        MMServ.mmlog.errLog(ex);
                    }
                }//end For
            }//end if player
            else if ( whoToKick.equalsIgnoreCase("dedicated")) {
                Set<String> users = CampaignMain.cm.getServer().getUsers().keySet();
                TreeSet<String> toKickList = new TreeSet<String>();
                synchronized (users) {
                    for ( String toKick : users)
                        toKickList.add(toKick);
                }
                for ( String toKick : toKickList ){
                    if ( !toKick.toLowerCase().startsWith("[dedicated]") )
                        continue;
                    if ( CampaignMain.cm.getServer().isAdmin(toKick) )
                        continue;
                    try{
                		CampaignMain.cm.getServer().doStoreMail(toKick+",update", Username);
                    	Thread.sleep(120);
                    }catch (Exception ex){
                        MMServ.mmlog.errLog(ex);
                    }
                }//end For
            }//end if ded
        }//end hasMore Commands
   	}
}