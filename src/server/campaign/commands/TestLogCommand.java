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

import java.util.StringTokenizer;

import common.CampaignData;

import server.MWServ;
import server.campaign.CampaignMain;

public class TestLogCommand implements Command {
	
	//conforming methods
	public int getExecutionLevel(){return 0;}
	public void setExecutionLevel(int i) {}
	String syntax = "";
	public String getSyntax() { return syntax;}

	public void process(StringTokenizer command,String Username) {
		StringBuilder sb = new StringBuilder();
		while(command.hasMoreTokens()) {
			sb.append(command.nextToken());
		}
		CampaignData.mwlog.testLog(Username + " has requested logging: ");
		CampaignData.mwlog.testLog(sb.toString());
		CampaignData.mwlog.testLog("End requested log from " + Username);
		CampaignMain.cm.doSendModMail("NOTE", Username + " has sent a testLog entry.");
	}
	
}