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

import server.campaign.SPlanet;
import server.campaign.SPlayer;
import server.campaign.SUnitFactory;
import server.campaign.CampaignMain;
import server.campaign.commands.Command;
import server.MWChatServer.auth.IAuthenticator;

//refreshfactory#planet#factory
public class RefreshFactoryCommand implements Command {
	
	int accessLevel = IAuthenticator.GUEST;
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
		
		if (!CampaignMain.cm.getBooleanConfig("AllowFactoryRefreshForRewards")) {
			CampaignMain.cm.toUser("You may not use RP to refresh a factory on this server.",Username,true);
			return;   
		}
		
		int rpCost = CampaignMain.cm.getIntegerConfig("RewardPointToRefreshFactory");
		
		SPlayer player = CampaignMain.cm.getPlayer(Username);
		String planetName;
		String factoryName;
		try {
			planetName = command.nextToken();
			factoryName = command.nextToken();
		} catch (Exception e) {
			CampaignMain.cm.toUser("Improper format. Try: /c refreshfactory#planetname#factoryname",Username,true);
			return;   
		}
		
		SPlanet p = (SPlanet)CampaignMain.cm.getData().getPlanetByName(planetName);
		if (p == null){
			CampaignMain.cm.toUser("Could not find planet: " + planetName + ".",Username,true);
			return;   
		}
		
		SUnitFactory uf = (SUnitFactory)CampaignMain.cm.getData().getFactoryByName(p,factoryName);
		if (uf == null){
			CampaignMain.cm.toUser("Could not find factory: " + factoryName + ".",Username,true);
			return;               
		}
		
		int playerRP = player.getReward();
		if (playerRP < rpCost){
			CampaignMain.cm.toUser(rpCost + " reward points required to refresh "+ uf.getName()+ ". You only have " + playerRP + ".",Username,true);
			return;
		}
		
		player.addReward(-rpCost);
		int ticksToRemove = uf.getTicksUntilRefresh();
		String refresh = uf.addRefresh(-ticksToRemove, true);//use get and add instead of set b/c add sends HS update
		
		CampaignMain.cm.doSendToAllOnlinePlayers(player.getMyHouse(), "HS|" + refresh, false);

		CampaignMain.cm.toUser("You refreshed "+ uf.getName()+" on planet "+p.getName()+" (-"+rpCost+" RP).",Username,true);
		CampaignMain.cm.doSendHouseMail(player.getMyHouse(), "NOTE",player.getName()+" refreshed "+ uf.getName()+" on planet "+p.getName());
	}
}