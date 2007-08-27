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
import server.campaign.SPlayer;
import server.campaign.CampaignMain;
import server.campaign.commands.Command;
import server.MWChatServer.auth.IAuthenticator;

public class FluffCommand implements Command {
	
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
		
		SPlayer p = null;
		String fluff = "";
		
		try {
			p = CampaignMain.cm.getPlayer(command.nextToken());
		} catch (Exception e) {
			CampaignMain.cm.toUser("Improper command. Try: /c fluff#PlayerName#text to set text or /c fluff#PlayerName to clear", Username, true);
			return;
		}
		
		try {
			fluff = command.nextToken();
		} catch (Exception e) {
			fluff = null;
		}
		
		if (p == null) {
			CampaignMain.cm.toUser("Couldn't find a player with that name.", Username, true);
			return;
		}
		
		//set the text
		if (fluff != null) {
            if ( fluff.indexOf("~") > 0 ||
                    fluff.indexOf("$") >0 ||
                    fluff.indexOf("#") >0 ||
                    fluff.indexOf("|") > 0){
                CampaignMain.cm.toUser("Illegal characters in the fluff text try again without '|','$','#', or '~' characters",Username,true);
                return;
            }
            
            p.setFluffText(fluff);
			CampaignMain.cm.toUser("New fluff text for " + p.getName() + ": " + fluff,Username,true);
			CampaignMain.cm.toUser(Username + " set your fluff to: " + fluff,p.getName(),true);
			//server.MWServ.mwlog.modLog(Username + " set " + p.getName() + "'s fluff to '" + fluff + "'.");
			CampaignMain.cm.doSendModMail("NOTE",Username + " set " + p.getName() + "'s fluff to '" + fluff + "'.");
		}
		
		//no text, so remove fluff
		else {
			p.setFluffText("");
			CampaignMain.cm.toUser("Removed fluff from " + p.getName() + ".",Username,true);
			CampaignMain.cm.toUser(Username + " removed your fluff text.",p.getName(),true);
			//server.MWServ.mwlog.modLog(Username + " removed " + p.getName() + "'s fluff.");
			CampaignMain.cm.doSendModMail("NOTE",Username + " removed " + p.getName() + "'s fluff.");
		}
		
        CampaignMain.cm.doSendToAllOnlinePlayers("PI|FT|"+p.getName()+"|"+p.getFluffText(),false);
	}
}