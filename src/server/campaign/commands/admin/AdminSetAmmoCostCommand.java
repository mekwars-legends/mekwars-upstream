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

import server.campaign.CampaignMain;
import server.campaign.commands.Command;
import server.MWChatServer.auth.IAuthenticator;

public class AdminSetAmmoCostCommand implements Command {
	
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
		
		long ammo = -1;
        int cost = -1;
        
		try{
			ammo = Long.parseLong(command.nextToken());
            cost = Integer.parseInt(command.nextToken());
		}
		catch (Exception ex){
			CampaignMain.cm.toUser("Invalid syntax. Try: adminsetammocost#munitionnumber#cost",Username,true);
		}

        String ammoName = CampaignMain.cm.getData().getMunitionsByNumber().get(ammo);
        
        CampaignMain.cm.getAmmoCost().put(ammo,cost);
        CampaignMain.cm.toUser("Cost for " + ammoName + " set to " + CampaignMain.cm.moneyOrFluMessage(true,false,cost) + " per ton.",Username,true);
        CampaignMain.cm.doSendModMail("NOTE:",Username + " set the cost of " + ammoName + " to " + CampaignMain.cm.moneyOrFluMessage(true,false,cost) + " per ton.");
        CampaignMain.cm.saveAmmoCosts();
	}
}