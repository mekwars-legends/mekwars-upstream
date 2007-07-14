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

import server.MMServ;
import server.campaign.CampaignMain;
import server.campaign.SPlayer;
import server.campaign.commands.Command;
import server.MWChatServer.auth.IAuthenticator;
import server.util.MMNetPasswd;


/**
 * Moving the Register command from MMServ into the normal command structure.
 *
 * Syntax  /c Register#Name,Password
 */
public class RegisterCommand implements Command {
	
	int accessLevel = 0;
	public int getExecutionLevel(){return accessLevel;}
	public void setExecutionLevel(int i) {accessLevel = i;}
	
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
            SPlayer player = null;
            
            try{
                regname = str.nextToken().trim().toLowerCase();
                pw = str.nextToken();
            }catch (Exception ex){
                MMServ.mmlog.errLog("Failure to register: "+regname);
                return;
            }
            
            //Check to see if the Username is already registered
            boolean regged = false;
            try {
                //MMNetPasswd.getRecord(regname, null);
            	 player = CampaignMain.cm.getPlayer(regname);
            	if ( player.getPassword() != null )
            		regged = true;
            } catch (Exception ex) {
                //Username already registered, ignore error.
                MMServ.mmlog.errLog(ex);
                regged = true;
            }
             
            if (regged && !CampaignMain.cm.getServer().isAdmin(Username)) {
            	CampaignMain.cm.toUser("Nickname \"" + regname + "\" is already registered!", Username);
                //MMServ.mmlog.modLog(Username + " tried to register the nickname \"" + regname + "\", which was already registered.");
                CampaignMain.cm.doSendModMail("NOTE",Username + " tried to register the nickname \"" + regname + "\", which was already registered.");
                return;
            }
            	
            //check passwd length
            if (pw.length() < 3 && pw.length() > 11) {
            	CampaignMain.cm.toUser("Passwords must be between 4 and 10 characters!", Username);
            	return;
            }
                	
            //change userlevel
            int level = -1;
            if (CampaignMain.cm.getServer().isAdmin(Username)){
            	MMNetPasswd.writeRecord(regname, IAuthenticator.ADMIN, pw);	
            	level = IAuthenticator.ADMIN;
            } else {
            	MMNetPasswd.writeRecord(regname, IAuthenticator.REGISTERED, pw);
            	level = IAuthenticator.REGISTERED;
            }
            
            //send the userlevel change to all players
            CampaignMain.cm.getServer().getClient(Username).setAccessLevel(level);
            CampaignMain.cm.getServer().getUser(Username).setLevel(level);
            CampaignMain.cm.getServer().sendRemoveUserToAll(Username,false);
            CampaignMain.cm.getServer().sendNewUserToAll(Username,false);
            
            if (player != null){
            	CampaignMain.cm.doSendToAllOnlinePlayers("PI|DA|" + CampaignMain.cm.getPlayerUpdateString(player),false);
            	player.getPassword().setAccess(level);
                player.setSave(true);
                
            }
            if(CampaignMain.cm.isUsingMySQL()) {
            	CampaignMain.cm.MySQL.setPlayerPassword(CampaignMain.cm.MySQL.getPlayerIDByName(Username), pw);
            	CampaignMain.cm.MySQL.setPlayerAccess(CampaignMain.cm.MySQL.getPlayerIDByName(Username), level);
            }
            //acknowledge registration
            CampaignMain.cm.toUser("\"" + regname + "\" successfully registered.", Username);
            MMServ.mmlog.modLog("New nickname registered: " + regname);
            CampaignMain.cm.doSendModMail("NOTE","New nickname registered: " + regname + " by: " + Username);
    	
        } catch (Exception e) {
            MMServ.mmlog.errLog(e);
            MMServ.mmlog.errLog("^ Not supposed to happen! ^");
            MMServ.mmlog.errLog(e);
            MMServ.mmlog.errLog("Not supposed to happen");
        }
    }
}