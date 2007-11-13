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

import megamek.common.MiscType;

import server.campaign.CampaignMain;
import server.campaign.commands.Command;
import server.MWChatServer.auth.IAuthenticator;

public class AdminSetBanTargetingCommand implements Command {
	
	int accessLevel = IAuthenticator.ADMIN;
	String syntax = "targetingType";
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
		
		int targetingType = -1;
		try{
            targetingType = Integer.parseInt(command.nextToken());
		}
		catch (Exception ex){
			CampaignMain.cm.toUser("Invalid syntax. Try: AdminSetBanTargeting#targetingType",Username,true);
		}
		
		if (CampaignMain.cm.getData().getBannedTargetingSystems().get(targetingType)!= null){
			CampaignMain.cm.getData().getBannedTargetingSystems().remove(targetingType);
            CampaignMain.cm.getData().setBannedTargetingSystems(CampaignMain.cm.getData().getBannedTargetingSystems());
			CampaignMain.cm.toUser("You lifted the ban on " + MiscType.getTargetSysName(targetingType)+".",Username,true);
			CampaignMain.cm.doSendModMail("NOTE:",Username + " lifted the ban on " + MiscType.getTargetSysName(targetingType)+".");
		} else {
            CampaignMain.cm.getData().getBannedTargetingSystems().put(targetingType,"banned");
            CampaignMain.cm.getData().setBannedTargetingSystems(CampaignMain.cm.getData().getBannedTargetingSystems());
			CampaignMain.cm.toUser("You banned " + MiscType.getTargetSysName(targetingType)+".",Username,true);
			CampaignMain.cm.doSendModMail("NOTE:",Username + " banned " + MiscType.getTargetSysName(targetingType)+".");
		}
		
        CampaignMain.cm.saveBannedTargetingSystems();
	}
}