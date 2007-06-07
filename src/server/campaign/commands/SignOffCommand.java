/*
 * MekWars - Copyright (C) 2006
 * 
 * Original author - Jason Tighe (torren@users.sourceforge.net)
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 */

package server.campaign.commands;

import java.util.StringTokenizer;

import server.MMServ;
import server.campaign.CampaignMain;
import server.campaign.commands.Command;



/**
 * Moving the SignOff command from MMServ into the normal command structure.
 *
 * Syntax  /c SignOff
 */
public class SignOffCommand implements Command {
	
	public int getExecutionLevel(){return 0;}
	public void setExecutionLevel(int i) {}
	
	public void process(StringTokenizer command,String Username) {
		
        MMServ.mmlog.errLog(Username+" has sent signoff command");
        CampaignMain.cm.getServer().clientLogout(Username);
	}
}