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

import common.Army;
import server.campaign.CampaignMain;
import server.campaign.SPlayer;
import server.campaign.SArmy;

public class ArmyUpperLimiterCommand implements Command {
	
	int accessLevel = 0;
	public int getExecutionLevel(){return accessLevel;}
	public void setExecutionLevel(int i) {accessLevel = i;}
	
	public void process(StringTokenizer command,String Username) {
		
		//access check
		if (accessLevel != 0) {
			int userLevel = CampaignMain.cm.getServer().getUserLevel(Username);
			if(userLevel < getExecutionLevel()) {
				CampaignMain.cm.toUser("Insufficient access level for command. Level: " + userLevel + ". Required: " + accessLevel + ".",Username,true);
				return;
			}
		}
		
		if (command.hasMoreElements()) {
			
			//first, make sure limiters are allowed ...
			boolean limitsAllowed = new Boolean(CampaignMain.cm.getConfig("AllowLimiters")).booleanValue();
			if (!limitsAllowed) {
				CampaignMain.cm.toUser("Limits are disabled.",Username,true);
				return;
			}
			
			
			int armyid = Integer.parseInt((String)command.nextElement());
			if (command.hasMoreElements()) {
				
				int limit = Integer.parseInt(command.nextToken());
				SPlayer p = CampaignMain.cm.getPlayer(Username);
				if (p != null) {
					if (p.getDutyStatus() == SPlayer.STATUS_ACTIVE) {
						CampaignMain.cm.toUser("You cannot change limits while active.",Username,true);
						return;
					}
					SArmy army = p.getArmy(armyid);
					if (army != null) {
						
						if (limit < Army.NO_LIMIT) {//-1 is NO_LIMIT
							CampaignMain.cm.toUser("You may not set negative limits.",Username,true);
							return;
						}
						
						//check to make sure that the proposed limit doesnt hit the buffer
						int bufferAmt = CampaignMain.cm.getIntegerConfig("UpperLimitBuffer");
						if (limit < bufferAmt && limit != Army.NO_LIMIT) {
							CampaignMain.cm.toUser("You must set an upper limit of " + bufferAmt + "or more.",Username,true);
							return;
						}
						
						army.setUpperLimiter(limit);
						
						if (limit == -1)
							CampaignMain.cm.toUser("Army #" + armyid + "'s upper limit disabled.",Username,true);
						else	
							CampaignMain.cm.toUser("Army #" + armyid + "'s upper limit set to " + limit + ".",Username,true);
						
						CampaignMain.cm.toUser("PL|SAB|"+army.getID()+"#"+army.getLowerLimiter()+"#"+ army.getUpperLimiter(),Username,false);
					}
				}
			}
		}
	}
}