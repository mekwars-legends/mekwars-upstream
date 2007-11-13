/*
 * MekWars - Copyright (C) 2004 
 * 
 * Derived from MegaMekNET (http://www.sourceforge.net/projects/megameknet)
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

import java.io.File;
import java.net.InetAddress;
import java.util.StringTokenizer;

import server.MWServ;
import server.campaign.CampaignMain;
import server.campaign.SPlayer;
import server.campaign.SHouse;


public class UnenrollCommand implements Command {
	
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
		
		if (Username.startsWith("Nobody")) {
			CampaignMain.cm.toUser("Nobodies can't enroll, hence they can't unenroll. Nice try though.", Username, true);
			return;
		}
		
		//load the player
		SPlayer p = CampaignMain.cm.getPlayer(Username);
		if (p == null) {
			CampaignMain.cm.toUser("Couldn't find your player to unenroll. Contact an admin immediately.", Username, true);
			return;
		}
		
		//check for confirmation
		if (!command.hasMoreTokens()) {
			CampaignMain.cm.toUser("You didn't confirm the Unenroll command. Enter /c unenroll#confirm if you're absolutely sure you want to quit." , Username, true);
			return;
		}
		
		String confirmString = command.nextToken();
		if (!confirmString.equalsIgnoreCase("confirm")) {
			CampaignMain.cm.toUser("You didn't confirm the Unenroll Command. Enter /c unenroll#confirm if you're absolutely sure you want to quit.", Username, true);
			return;
		}
		
		if (CampaignMain.cm.getOpsManager().getShortOpForPlayer(p) != null
				|| p.getDutyStatus() == SPlayer.STATUS_FIGHTING) {
			CampaignMain.cm.toUser("You cannot unenroll while in a game.", Username, true);
			return;
		}
		
		if (p.getExperience() == 0) {
			CampaignMain.cm.toUser("You cannot unenroll with 0 XP. Ask an admin or mod to remove your account.", Username, true);
			return;
		}
		
		if (CampaignMain.cm.getMarket().hasActiveListings(p)) {
			CampaignMain.cm.toUser("You cannot unenroll while you have units on the Market. Recall them and try again.", Username, true);
			return;
		}
		
		if (p.hasRepairingUnits(false)) {
			CampaignMain.cm.toUser("You cannot unenroll while repairing units. Cancel the repairs and try again.", Username, true);
			return;
		}
		
		SHouse hisfaction = CampaignMain.cm.getHouseForPlayer(Username);
		if (hisfaction == null) {
			CampaignMain.cm.toUser("Couldn't find faction to unenroll. Contact an admin immediately.", Username, true);
			return;
		}
		
		//checks passed. do the actual removal.
		hisfaction.removePlayer(p, CampaignMain.cm.getBooleanConfig("DonateUnitsUponUnenrollment"));

		//tell the user
		CampaignMain.cm.toUser("You've been unenrolled.", Username, true);
		
		//delete the player's saved info, if a pfile exists
		File fp = new File("./campaign/players/" + p.getName().toLowerCase() + ".dat");
		if (fp.exists())
			fp.delete();
		if(CampaignMain.cm.isUsingMySQL()) {
			CampaignMain.cm.MySQL.deletePlayer(p);
		}
		//tell the mods and add to iplog.0
		InetAddress ip = CampaignMain.cm.getServer().getIP(Username);
		//MWServ.mwlog.modLog(Username + " unenrolled from the campaign (IP: " + ip + ").");
		MWServ.mwlog.ipLog("UNENROLL: " + Username + " IP: " + ip);
		CampaignMain.cm.doSendModMail("NOTE",Username + " unenrolled from the campaign (IP: " + ip + ").");
		
		
	}//end process
	
}//end UnenrollCommand