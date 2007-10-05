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

import server.MWServ;
import server.campaign.SUnitFactory;
import server.campaign.SPlanet;
import server.campaign.CampaignMain;
import server.campaign.commands.Command;
import server.MWChatServer.auth.IAuthenticator;

public class AdminDestroyFactoryCommand implements Command {
	
	int accessLevel = IAuthenticator.ADMIN, factoryID;
	public int getExecutionLevel(){return accessLevel;}
	public void setExecutionLevel(int i) {accessLevel = i;}
	String syntax = "Planet Name#Factory Name";
	public String getSyntax() { return syntax;}

	
	public void process(StringTokenizer command,String Username) {
		
		//access level check
		int userLevel = CampaignMain.cm.getServer().getUserLevel(Username);
		if(userLevel < getExecutionLevel()) {
			CampaignMain.cm.toUser("Insufficient access level for command. Level: " + userLevel + ". Required: " + accessLevel + ".",Username,true);
			return;
		}
		
		try {
			SPlanet p = CampaignMain.cm.getPlanetFromPartialString(command.nextToken(),Username);
			String factoryname = command.nextToken();
			if ( p == null ) {
				CampaignMain.cm.toUser("Planet not found:",Username,true);
				return;
			}
			
			SUnitFactory UF = null;
			Iterator it = p.getUnitFactories().iterator();
			if ( !it.hasNext() ) {
				CampaignMain.cm.toUser("This planet does not have any factories!",Username,true);
				return;
			}
			
			int count = 0;
			while (it.hasNext()){
				UF = (SUnitFactory)it.next();
				if ( UF.getName().equalsIgnoreCase(factoryname)) {
					// Remove it from the database
					if(CampaignMain.cm.isUsingMySQL())
						CampaignMain.cm.MySQL.deleteFactory(UF.getID());
					p.getUnitFactories().removeElementAt(count);
					p.getUnitFactories().trimToSize();
					break;
				}
				count++;
			}
			
			if ( UF == null ){
				CampaignMain.cm.toUser("Factory " + factoryname + " not found",Username,true);
				return;
			}
            p.updated();
			//server.MWServ.mwlog.modLog(Username + "  removed " + factoryname + " from " + p.getName() + ".");
			CampaignMain.cm.toUser(factoryname + " removed from " + p.getName() + ".",Username,true);
			CampaignMain.cm.doSendModMail("NOTE",Username + "  removed " + factoryname + " from " + p.getName() + ".");
		} catch (Exception ex){
			MWServ.mwlog.errLog(ex);
		}//end catch
		
	}
}