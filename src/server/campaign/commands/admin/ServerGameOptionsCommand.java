/*
 * MekWars - Copyright (C) 2005
 * 
 * Original author - nmorris (urgru@users.sourceforge.net)
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 */

/**
 * @author Torren (Jason Tighe)
 * 08/18/2005
 * 
 * Receives the MegaMek games options from the client.
 * parses it and saves it.
 */

package server.campaign.commands.admin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.StringTokenizer;

import server.MWChatServer.auth.IAuthenticator;
import server.campaign.CampaignMain;
import server.campaign.commands.Command;

import common.CampaignData;

public class ServerGameOptionsCommand implements Command {
	
	int accessLevel = IAuthenticator.ADMIN;
	String syntax = "";
	public int getExecutionLevel(){return accessLevel;}
	public void setExecutionLevel(int i) {accessLevel = i;}
	public String getSyntax() { return syntax;}
	
	public void process(StringTokenizer command,String Username) {
		
		//access level check
		int userLevel = CampaignMain.cm.getServer().getUserLevel(Username);
		if(userLevel < getExecutionLevel()) {
			CampaignMain.cm.toUser("AM:Insufficient access level for command. Level: " + userLevel + ". Required: " + accessLevel + ".",Username,true);
			return;
		}
		
		File mmGameOptionsFolder = new File("./mmconf");
		
		if ( !mmGameOptionsFolder.exists() )
			mmGameOptionsFolder.mkdir();
		
		File mmGameOptions = new File("./mmconf/gameoptions.xml");
		try{
			FileOutputStream fops = new FileOutputStream(mmGameOptions);
			PrintStream out = new PrintStream(fops);
			while (command.hasMoreTokens()){
				out.println(command.nextToken());
			}
			out.close();
			fops.close();
		}
		catch (Exception ex){
			CampaignData.mwlog.errLog("Unable to save Mega Mek Game Options!");
			CampaignData.mwlog.errLog(ex);
		}
		
		CampaignMain.cm.getMegaMekClient().getGame().getOptions().loadOptions();
		
		CampaignMain.cm.toUser("You have set the MegaMek Game Options",Username,true);
		CampaignMain.cm.doSendModMail("NOTE",Username + " has set the MegaMek game options for the server.");
	}
}
