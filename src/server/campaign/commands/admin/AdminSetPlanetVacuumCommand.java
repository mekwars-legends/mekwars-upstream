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

import common.CampaignData;
import server.campaign.commands.Command;
import server.campaign.SPlanet;
import server.campaign.CampaignMain;
import server.MWChatServer.auth.IAuthenticator;

// AdminSetPlanetVacuum#Planet#true/false
public class AdminSetPlanetVacuumCommand implements Command {
	
	int accessLevel = IAuthenticator.ADMIN;
	String syntax = "Planet Name#[true/false]";
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
		
		try{
			SPlanet p = (SPlanet) CampaignMain.cm.getData().getPlanetByName(command.nextToken());
			if ( p == null ){
				CampaignMain.cm.toUser("Unknown planet!",Username,true);
				return;   
			}
			
			boolean lock; 
			if ( command.hasMoreElements() )
				lock = new Boolean(command.nextToken()).booleanValue();
			else {
				if ( p.isVacuum() )
					lock = false;
				else
					lock = true;
			}
			
			p.setVacuum(lock);
			if ( lock ) {
				CampaignMain.cm.toUser("You've removed the atmosphere from planet "+p.getName(),Username,true);
				//server.CampaignData.mwlog.modLog(Username + " removed the atmosphere from planet "+p.getName());
				CampaignMain.cm.doSendModMail("NOTE",Username + " has removed the atmosphere from planet "+p.getName());
			}
			else {	
				CampaignMain.cm.toUser("You've created an atmosphere on planet "+p.getName(),Username,true);
				//server.CampaignData.mwlog.modLog(Username + " has created an atmosphere on planet "+p.getName());
				CampaignMain.cm.doSendModMail("NOTE",Username + " has created an atmosphere on planet "+p.getName());
			}
			p.updated();

			
		}
		catch (Exception ex){
			CampaignData.mwlog.errLog(ex);
		}
		
	}
}