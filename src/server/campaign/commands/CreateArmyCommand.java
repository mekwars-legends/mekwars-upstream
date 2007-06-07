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
import server.campaign.CampaignMain;
import server.campaign.SPlayer;
import server.campaign.SArmy;


public class CreateArmyCommand implements Command {
	
	int accessLevel = 0;
	public int getExecutionLevel(){return accessLevel;}
	public void setExecutionLevel(int i) {accessLevel = i;}
	
	public void process(StringTokenizer command,String Username) {
		
		if (accessLevel != 0) {
			int userLevel = CampaignMain.cm.getServer().getUserLevel(Username);
			if(userLevel < getExecutionLevel()) {
				CampaignMain.cm.toUser("Insufficient access level for command. Level: " + userLevel + ". Required: " + accessLevel + ".",Username,true);
				return;
			}
		}
		
		SPlayer p = CampaignMain.cm.getPlayer(Username);
		int maxlances = CampaignMain.cm.getIntegerConfig("MaxLancesPerPlayer");
		
		if (p.getDutyStatus() == SPlayer.STATUS_ACTIVE) {
			CampaignMain.cm.toUser("You may not create new armies while on active duty.",Username,true);
			return;
		}
		
		if ( p.getArmies().size() >= maxlances ) {
			CampaignMain.cm.toUser("You have reached the max number of allowable armies ("+maxlances+").",Username,true);
			return;
		}
		
		/*
		 * Determine the lowest free army id by searching upwards until
		 * no army with a matching ID is found in the player's list. This
		 * involves hideous nested loops, but works well.
		 */
		p.getArmies().trimToSize();
		int i = 0;
		boolean free = false;
		while (!free) {
			free = true;
			for (int j = 0; j < p.getArmies().size(); j++) {
				if (p.getArmies().elementAt(j).getID() == i) {
					free = false;
					i++;
				}
			}
		}
		
		//make the new army, and set misc. data
		SArmy newArmy = new SArmy(i, p.getName());
		
		//check for all standard illegal name chars
		if (command.hasMoreElements()) {
			
			String name = (String)command.nextElement();
			boolean illegalName = false;
			
			if (name.length() > 50)
				name = name.substring(0,50);
			
			if (name.indexOf("%") != -1) {
				CampaignMain.cm.toUser("Illegal army name (% forbidden).",Username,true);
				illegalName = true;
			} else if (name.indexOf("~") != -1) {
				CampaignMain.cm.toUser("Illegal army name (~ forbidden).",Username,true);
				illegalName = true;
			} else if (name.indexOf("$") != -1) {
				CampaignMain.cm.toUser("Illegal army name ($ forbidden).",Username,true);
				illegalName = true;
			} else if (name.indexOf("|") != -1) {
				CampaignMain.cm.toUser("Illegal army name (| forbidden).",Username,true);
				illegalName = true;
			} else if (name.indexOf("!") != -1) {
				CampaignMain.cm.toUser("Illegal army name (! forbidden).",Username,true);
				illegalName = true;
			} else if (name.indexOf("*") != -1) {
				CampaignMain.cm.toUser("Illegal army name (* forbidden).",Username,true);
				illegalName = true;
			} else if (name.indexOf("#") != -1) {
				CampaignMain.cm.toUser("Illegal army name (# forbidden).",Username,true);
				illegalName = true;
			} else if (name.indexOf(">") != -1) {
				CampaignMain.cm.toUser("Illegal army name (> forbidden).",Username,true);
				illegalName = true;
			} else if (name.indexOf("<") != -1) {
				CampaignMain.cm.toUser("Illegal army name (< forbidden).",Username,true);
				illegalName = true;
			} else if (name.indexOf("@") != -1) {
				CampaignMain.cm.toUser("Illegal army name (@ forbidden).",Username,true);
				illegalName = true;
			} else if (name.indexOf("&") != -1) {
				CampaignMain.cm.toUser("Illegal army name (& forbidden).",Username,true);
				illegalName = true;
			} else if (name.indexOf("^") != -1) {
				CampaignMain.cm.toUser("Illegal army name (^ forbidden).",Username,true);
				illegalName = true;
			} else if (name.indexOf("+") != -1) {
				CampaignMain.cm.toUser("Illegal army name (+ forbidden).",Username,true);
				illegalName = true;
			} else if (name.indexOf("=") != -1) {
				CampaignMain.cm.toUser("Illegal army name (= forbidden).",Username,true);
				illegalName = true;
			}
			
			if (!illegalName)
				newArmy.setName(name);
		}
		
		//player is making a new army. set the default limiters.
		newArmy.setUpperLimiter(CampaignMain.cm.getIntegerConfig("DefaultUpperLimit"));
		newArmy.setLowerLimiter(CampaignMain.cm.getIntegerConfig("DefaultLowerLimit"));
		
		//add the army to the player's list
		p.getArmies().add(newArmy);
		
		//send relevant data to client
		CampaignMain.cm.toUser("PL|SAD|"+p.getArmy(i).toString(true,"%"),Username,false);
		CampaignMain.cm.toUser("Created a new Army (#" + p.getArmy(i).getID() + ")." ,Username,true);
	
	}//end process()
}//end CreateArmyCommand