/*
 * MekWars - Copyright (C) 2007 
 * 
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

/**
 * @author jtighe
 * This Command is used by server admins to update owners for a planet.
 * 
 */

package server.campaign.commands.admin;

import java.util.StringTokenizer;

import common.House;

import server.campaign.commands.Command;
import server.campaign.CampaignMain;
import server.campaign.SPlanet;
import server.MWChatServer.auth.IAuthenticator;

public class AdminUpdatePlanetOwnershipCommand implements Command {
	
	int accessLevel = IAuthenticator.ADMIN;
	public int getExecutionLevel(){return accessLevel;}
	public void setExecutionLevel(int i) {accessLevel = i;}
	
	public void process(StringTokenizer command,String Username) {
		
		//access level check
		int userLevel = CampaignMain.cm.getServer().getUserLevel(Username);
		if(userLevel < getExecutionLevel()) {
			CampaignMain.cm.toUser("Insufficient access level for command. Level: " + userLevel + ". Required: " + accessLevel + ".",Username,true);
			return;
		}
		
		//vars
		SPlanet planet = null;
		House house = null;
		int ownerShip = 0;
		
		try {
			planet = CampaignMain.cm.getPlanetFromPartialString(command.nextToken(),Username);
			house = CampaignMain.cm.getHouseFromPartialString(command.nextToken(),Username);
			ownerShip = Integer.parseInt(command.nextToken());
			
		} catch (Exception e) {
			CampaignMain.cm.toUser("Improper command. Try: /c adminupdateplanetownership#planet#faction#amount", Username, true);
			return;
		}
		
		if (planet == null) {
			CampaignMain.cm.toUser("Could not find a matching planet.",Username,true);
			return;
		}
		
		if ( house == null ){
			CampaignMain.cm.toUser("Could not find a matching faction to remove.",Username,true);
			return;
		}
		
		if ( ownerShip <= 0 ){
			CampaignMain.cm.toUser("Ownership cannot be less then or equal to 0",Username,true);
			return;
		}
		
		if ( ownerShip == planet.getInfluence().getInfluence(house.getId()) )
			return;
		
		planet.getInfluence().updateHouse(house.getId(), ownerShip);
		planet.updated();
		
        if(CampaignMain.cm.isUsingMySQL())
        	CampaignMain.cm.MySQL.savePlanet(planet);
		
		CampaignMain.cm.doSendModMail("NOTE",Username + " updated "+house.getName()+" ownership of "+ planet.getName()+" to "+ownerShip+".");
	}
}