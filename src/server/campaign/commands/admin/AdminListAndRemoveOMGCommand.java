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
import java.util.Iterator;
import java.util.Vector;

import common.Unit;

import server.campaign.commands.Command;
import server.campaign.CampaignMain;
import server.campaign.SHouse;
import server.campaign.SUnit;
import server.MWChatServer.auth.IAuthenticator;

public class AdminListAndRemoveOMGCommand implements Command {
	
	int accessLevel = IAuthenticator.ADMIN;
	String syntax = "";
	public int getExecutionLevel(){return accessLevel;}
	public void setExecutionLevel(int i) {accessLevel = i;}
	public String getSyntax() { return syntax;}
	
	public void process(StringTokenizer command,String Username) {
		
		//access level check
		int userLevel = CampaignMain.cm.getServer().getUserLevel(Username);
		if(userLevel < getExecutionLevel()) {
			CampaignMain.cm.toUser("Insufficient access level for command. Level: " + userLevel + ". Required: " + accessLevel + ".",Username,true);
			return;
		}
		
		/*
		 * We know that OMG's are always light meks, so we can loop
		 * through every faction's light mek queue to remove them.
		 */
		Iterator i = CampaignMain.cm.getData().getAllHouses().iterator();
		while (i.hasNext()) {
			SHouse faction  = (SHouse)i.next();
			Iterator it = ((Vector)faction.getHangar(Unit.MEK).elementAt(Unit.LIGHT)).iterator();
			while (it.hasNext()) {
				SUnit currU = (SUnit)it.next();
				if (currU.getModelName().equals("OMG-UR-FD")){
					CampaignMain.cm.doSendModMail("NOTE",Username + " removed an OMG from the " + faction.getName() + "bays. Should have been a " + currU.getUnitFilename()+ ".");
					CampaignMain.cm.toUser("Removed an OMG from the " + faction.getName() + " bays. Should have been a " + currU.getUnitFilename()+ ".",Username,true);
					it.remove();
				}
			}//end while(units remain in light mek hangar)
		}//end while(factions remain)
	
	}//end process()
}//end AdminListAndRemoveOMGCommand