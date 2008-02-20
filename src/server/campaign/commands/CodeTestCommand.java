/*
 * MekWars - Copyright (C) 2008
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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.StringTokenizer;

import megamek.common.Entity;

import server.MWServ;
import server.campaign.CampaignMain;
import server.campaign.SPlayer;
import server.campaign.SUnit;
import server.campaign.commands.Command;

public class CodeTestCommand implements Command {
	
	int accessLevel = 0;
	public int getExecutionLevel(){return 200;}
	public void setExecutionLevel(int i) {}
	String syntax = "";
	public String getSyntax() { return syntax;}
	
	public void process(StringTokenizer command,String Username) {
		
		if (accessLevel != 0) {
			int userLevel = CampaignMain.cm.getServer().getUserLevel(Username);
			if(userLevel < getExecutionLevel()) {
				CampaignMain.cm.toUser("AM:Insufficient access level for command. Level: " + userLevel + ". Required: " + accessLevel + ".",Username,true);
				return;
			}
		}
		
		int id = Integer.parseInt(command.nextToken());
		String cmd = command.nextToken();
        String file = "testFile.dat";
        SPlayer player = null;
		
		if ( cmd.equalsIgnoreCase("load") ) {
		    try {
	            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
	            player = (SPlayer) ois.readObject();
	            ois.close();
		    }catch(Exception ex) {
		        MWServ.mwlog.errLog(ex);
		    }
		    CampaignMain.cm.doSendModMail("NOTE", "Player loaded: "+player.getName());
		}else {
		    player = CampaignMain.cm.getPlayer(Username);		    
		    try {
		        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
		        oos.writeObject(player);
		        oos.close();
		    }catch(Exception ex) {
		        MWServ.mwlog.errLog(ex);
		    }
		}
	}
}