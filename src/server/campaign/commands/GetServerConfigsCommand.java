/*
 * MekWars - Copyright (C) 2006 
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

import server.campaign.CampaignMain;

public class GetServerConfigsCommand implements Command {
	
	int accessLevel = 0;
	String syntax = "";
	public int getExecutionLevel(){return 0;}
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
		
		try {
    		String config = command.nextToken();
    		
    		if ( config.equalsIgnoreCase("all") ) {
    		    
    		   StringBuffer result = new StringBuffer();
    		   for ( Object key : CampaignMain.cm.getConfig().keySet() ) {
    		       result.append(key.toString());
    		       result.append("|");
    		       result.append(CampaignMain.cm.getConfig(key.toString()));
    		       result.append("|");
    		   }
    		   CampaignMain.cm.toUser("SSC|"+result.toString(), Username,false);
    		}else {
    		    CampaignMain.cm.toUser("SSC|"+config+"|"+CampaignMain.cm.getConfig(config), Username,false);
    		}
    		
		}catch(Exception ex) {
		    CampaignData.mwlog.errLog(ex);
		    CampaignMain.cm.toUser("SSC|DONE|DONE", Username, false);
		}
	}
}
