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

import server.MWChatServer.auth.IAuthenticator;
import server.campaign.CampaignMain;
import server.campaign.SPlayer;
import server.util.MWPasswd;

import common.CampaignData;


/**
 * Moving the Register command from MWServ into the normal command structure.
 *
 * Syntax  /c Register#Name,Password
 */
public class RegisterCommand implements Command {
	
	int accessLevel = 0;
	String syntax = "";
	public int getExecutionLevel(){return accessLevel;}
	public void setExecutionLevel(int i) {accessLevel = i;}
	public String getSyntax() { return syntax;}
	
	public void process(StringTokenizer command,String Username) {
		
		/*
		 * Never check access level for register, but DO check
		 * to ensure that a player is enrolled in the campaign.
		 */
		if (CampaignMain.cm.getPlayer(Username) == null) {
			CampaignMain.cm.toUser("<font color=\"navy\"><br>---<br>You must have a campaign account in order to register a nickname. [<a href=\"MEKWARS/c enroll\">Click to get started</a>]<br>---<br></font>", Username, true);
			return;
		}
		
        try {
            StringTokenizer str = new StringTokenizer(command.nextToken(), ",");
        	String regname = "";
            String pw = "";  
            String email = "";
            SPlayer player = null;
            
            try{
                regname = str.nextToken().trim().toLowerCase();
                if(CampaignMain.cm.requireEmailForRegistration())
                	email = str.nextToken();
                pw = str.nextToken();
            }catch (Exception ex){
                CampaignData.mwlog.errLog("Failure to register: "+regname);
                return;
            }
            
            
            //Check to see if the Username is already registered
            boolean regged = false;
            try {
                //MWPasswd.getRecord(regname, null);
            	 player = CampaignMain.cm.getPlayer(regname);
            	if ( player.getPassword() != null && player.getPassword().access >= 2)
            		regged = true;
            } catch (Exception ex) {
                //Username already registered, ignore error.
                //CampaignData.mwlog.errLog(ex);
                regged = true;
            }
             
            if (regged && !CampaignMain.cm.getServer().isAdmin(Username)) {
            	CampaignMain.cm.toUser("AM:Nickname \"" + regname + "\" is already registered!", Username);
                //CampaignData.mwlog.modLog(Username + " tried to register the nickname \"" + regname + "\", which was already registered.");
                CampaignMain.cm.doSendModMail("NOTE",Username + " tried to register the nickname \"" + regname + "\", which was already registered.");
                return;
            }
            	
            //check passwd length
            if (pw.length() < 3 && pw.length() > 11) {
            	CampaignMain.cm.toUser("AM:Passwords must be between 4 and 10 characters!", Username);
            	return;
            }
            
            // Check for phpBB integration - there are a lot of possible outcomes here.
            if(CampaignMain.cm.isSynchingBB()) 
            	if(!CampaignMain.cm.MySQL.addUserToForum(Username, pw, email)) {
            		CampaignMain.cm.doSendModMail("NOTE","Server was unable to send registration email to " + Username + ".  Staff will need to give him his activation key.");
            		CampaignMain.cm.toUser("AM: The server was unable to send your registration email.  Please ask staff for assistance.", Username, true);
            	}
                	
            //change userlevel
            int level = -1;
            if (CampaignMain.cm.getServer().isAdmin(Username)){
            	MWPasswd.writeRecord(regname, IAuthenticator.ADMIN, pw);	
            	level = IAuthenticator.ADMIN;
            } else {
            	MWPasswd.writeRecord(regname, IAuthenticator.REGISTERED, pw);
            	level = IAuthenticator.REGISTERED;
            }
            
            //send the userlevel change to all players
            CampaignMain.cm.getServer().getClient(regname).setAccessLevel(level);
            CampaignMain.cm.getServer().getUser(regname).setLevel(level);
            CampaignMain.cm.getServer().sendRemoveUserToAll(regname,false);
            CampaignMain.cm.getServer().sendNewUserToAll(regname,false);
            
            if (player != null){
            	CampaignMain.cm.doSendToAllOnlinePlayers("PI|DA|" + CampaignMain.cm.getPlayerUpdateString(player),false);
            }
            if(CampaignMain.cm.isUsingMySQL() && player != null) {
            	CampaignMain.cm.MySQL.setPlayerPassword(CampaignMain.cm.MySQL.getPlayerIDByName(Username), pw);
            	CampaignMain.cm.MySQL.setPlayerAccess(CampaignMain.cm.MySQL.getPlayerIDByName(Username), level);
            	if(CampaignMain.cm.isSynchingBB()) {
           			//CampaignMain.cm.MySQL.addUserToForum(Username, pw, email);
           			player.setForumID(CampaignMain.cm.MySQL.getUserForumID(Username, email));
           			CampaignMain.cm.MySQL.addUserToHouseForum(player.getForumID(), player.getMyHouse().getForumID());
            	}
            }
            //acknowledge registration
            CampaignMain.cm.toUser("AM:\"" + regname + "\" successfully registered.", Username);
            CampaignData.mwlog.modLog("New nickname registered: " + regname);
            CampaignMain.cm.doSendModMail("NOTE","New nickname registered: " + regname + " by: " + Username);
    	
        } catch (Exception e) {
            CampaignData.mwlog.errLog(e);
            CampaignData.mwlog.errLog("^ Not supposed to happen! ^");
            CampaignData.mwlog.errLog(e);
            CampaignData.mwlog.errLog("Not supposed to happen");
        }
    }
}