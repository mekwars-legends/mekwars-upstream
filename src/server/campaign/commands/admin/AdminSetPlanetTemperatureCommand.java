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

package server.campaign.commands.admin;

import java.util.StringTokenizer;
import java.awt.Dimension;
import server.campaign.commands.Command;
import server.campaign.CampaignMain;
import server.campaign.SPlanet;
import server.MWChatServer.auth.IAuthenticator;


public class AdminSetPlanetTemperatureCommand implements Command {
	
	int accessLevel = IAuthenticator.ADMIN;
	String syntax = "Planet Name#Low Temp#[Hi Temp]";
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
		
		SPlanet planet =  (SPlanet) CampaignMain.cm.getData().getPlanetByName(command.nextToken());
		if ( planet == null ) {
			CampaignMain.cm.toUser("Unknown Planet",Username,true);
			return;
		}
		int low = Integer.parseInt(command.nextToken());
		
		int hi;
		if ( command.hasMoreTokens() )
			hi = Integer.parseInt(command.nextToken());
		else
			hi = low;
		
		planet.setTemp(new Dimension(low,hi));
		planet.updated();
		
        if(CampaignMain.cm.isUsingMySQL())
        	planet.toDB();
		
		CampaignMain.cm.toUser("Temperature range set for "+planet.getName(),Username,true);
		//server.CampaignData.mwlog.modLog(Username + " set the Temperature range for "+planet.getName());
		CampaignMain.cm.doSendModMail("NOTE",Username + " has set the Temperature range for "+planet.getName());
	}
}