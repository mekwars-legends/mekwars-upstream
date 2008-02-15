	/*
	 * MekWars - Copyright (C) 2008 
	 * 
	 * Original author - Bob Eldred (billypinhead@users.sourceforge.net)
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

package server.campaign.operations;

import server.campaign.CampaignMain;

public class OperationReportEntry {

	private String attackerName = "";
	private String defenderName = "";
	private String planetName = "";
	private String terrainName = "";
	private String themeName = "";
	
	private int attackerStartBV = 0;
	private int attackerEndBV = 0;
	private int defenderStartBV = 0;
	private int defenderEndBV = 0;
	
	private boolean attackerWon = false;
	
	private long gameLength = 0;
	
	public void setAttackerName(String name) {
		attackerName = name;
	}
	
	public void setDefenderName(String name) {
		defenderName = name;
	}
	
	public void setBV (boolean attacker, boolean start, int BV) {
		if (attacker) {
			if(start)
				attackerStartBV = BV;
			else
				attackerEndBV = BV;	
		} else {
			if(start)
				defenderStartBV = BV;
			else
				defenderEndBV = BV;
		}
	}
	
	public void setPlanetInfo (String pName, String tName, String thName) {
		planetName = pName;
		terrainName = tName;
		themeName = thName;
	}
	
	public void setAttackerWon(boolean aWon) {
		attackerWon = aWon;
	}
	
	public void commit() {
		// Write this to the database
		if(!CampaignMain.cm.isUsingMySQL())
			return;
		
	}
	
	public OperationReportEntry() {
		
	}
}
