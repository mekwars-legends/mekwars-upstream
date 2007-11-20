/*
 * MekWars - Copyright (C) 2007
 * 
 * Original author - jtighe (torren@users.sourceforge.net)
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

package server.campaign.commands;

import java.util.StringTokenizer;

import common.SubFaction;

import server.MWChatServer.auth.IAuthenticator;
import server.campaign.SPlayer;
import server.campaign.CampaignMain;

public class PromotePlayerCommand implements Command {

	//Starting out at mod level this can be lowered as needed
	int accessLevel = IAuthenticator.MODERATOR;
	public int getExecutionLevel(){return accessLevel;}
	public void setExecutionLevel(int i) {accessLevel = i;}
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

		//person doing the premoting
		SPlayer leader = CampaignMain.cm.getPlayer(Username);
		//Person being promoted
		SPlayer grunt = null;
		String subFactionName;
		SubFaction subFaction = null;
		
		try{
			grunt = CampaignMain.cm.getPlayer(command.nextToken());
			subFactionName = command.nextToken();
		}catch(Exception ex){
			CampaignMain.cm.toUser("AM:Invalid Syntax: /promoteplayer Player#NewSubFactionName", Username);
			return;
		}
		
		if ( grunt == null ){
			CampaignMain.cm.toUser("AM:Unknown Player", Username);
			return;
		}
		
		if ( !grunt.getMyHouse().getName().equalsIgnoreCase(leader.getMyHouse().getName()) && !CampaignMain.cm.getServer().isModerator(Username) ){
			CampaignMain.cm.toUser("AM:You can only promote players that within your same faction!", Username);
			return;
		}
		subFaction = grunt.getMyHouse().getSubFactionList().get(subFactionName);
		
		if( subFaction == null ){
			CampaignMain.cm.toUser("AM:That SubFaction does not exist for faction "+grunt.getMyHouse().getName()+".", Username);
			return;
		}
		
		int minELO = Integer.parseInt(subFaction.getConfig("MinELO"));
		int minEXP = Integer.parseInt(subFaction.getConfig("MinExp"));

		if ( grunt.getSubFactionAccess() > Integer.parseInt(subFaction.getConfig("AccessLevel")) ){
			CampaignMain.cm.toUser("AM:You cannot promote "+grunt.getName()+" to a subfaction with a lower access level, try demoting.", Username);
			return;
		}
		
		if ( grunt.getExperience() < minEXP || grunt.getRating() < minELO ){
			CampaignMain.cm.toUser("AM:Sorry but "+grunt.getName()+" is not skilled enough to join that SubFaction.", Username);
			return;
		}
		
		grunt.setSubFaction(subFactionName);
		CampaignMain.cm.toUser("PS|"+grunt.toString(true), Username,false);
		CampaignMain.cm.toUser("AM:Congratulations you have been promoted to SubFaction "+subFactionName+".", grunt.getName());
		CampaignMain.cm.doSendHouseMail(grunt.getMyHouse(), "NOTE", grunt.getName()+" has been promoted to subfaction "+subFactionName+" by "+leader.getName()+"!");
	
	}
}//end RequestSubFactionPromotionCommand class