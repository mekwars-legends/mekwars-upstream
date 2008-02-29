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

import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import server.campaign.CampaignMain;
import server.campaign.SArmy;
import server.campaign.SPlayer;

public class OperationReporter {

	private OperationReportEntry opData = new OperationReportEntry();
	private Set<String> attackerSet;
	private Set<String> defenderSet;
	
	public void setAttackers(TreeMap<String, Integer> attackers) {
		attackerSet = attackers.keySet();
		Iterator<String> it = attackerSet.iterator();
		int count = 0;
		StringBuilder aNames = new StringBuilder();
		
		while(it.hasNext()) {
			if(count > 0)
				aNames.append(", ");
			aNames.append(it.next());
			count ++;
		}
		opData.setAttackerName(aNames.toString());
	}
	
	public void setDefenders(TreeMap<String, Integer> defenders) {
		defenderSet = defenders.keySet();
		Iterator<String> it = defenderSet.iterator();
		int count = 0;
		StringBuilder dNames = new StringBuilder();
		
		while(it.hasNext()) {
			if(count > 0)
				dNames.append(", ");
			dNames.append(it.next());
			count ++;
		}
		opData.setDefenderName(dNames.toString());
	}
		
	public void setPlanetInfo(String pName, String tName, String thName) {
		opData.setPlanetInfo(pName, tName, thName);
	}
	
	public void setAttackerStartBV(int BV) {
		opData.setBV(true, true, BV);
	}
	
	public void setDefenderStartBV(int BV) {
		opData.setBV(false, true, BV);
	}
	
	public void setAttackerEndBV(int BV) {
		opData.setBV(true, false, BV);
	}
	
	public void setDefenderEndBV(int BV) {
		opData.setBV(false, false, BV);
	}
	
	public void commit() {
// Not yet ready for prime time
//		if(CampaignMain.cm.isUsingMySQL()) {
//			CampaignMain.cm.MySQL.commitBattleReport(opData);
//		} else {
//		}
	}
	
	public void setUpOperation(String operationName, TreeMap<String, Integer> attackers, TreeMap<String, Integer> defenders, String planetName, String terrainName, String themeName) {
		 setAttackers(attackers);
         setDefenders(defenders);
         setPlanetInfo(planetName, terrainName, themeName);
         calculateStartingBVs(attackers, defenders);
         opData.setOpType(operationName);
	}
	
	public void calculateStartingBVs(TreeMap<String, Integer> attackers, TreeMap<String, Integer> defenders) {
        int bv = 0;
        for (String attacker : attackers.keySet()) {
        	SPlayer player = CampaignMain.cm.getPlayer(attacker);
        	if(player != null) {
        		SArmy army = player.getArmy(attackers.get(attacker));
        		if (army != null)
        			bv += army.getBV();
        	}
        }
        setAttackerStartBV(bv);
        
        bv = 0;
        for (String defender : defenders.keySet()) {
        	SPlayer player = CampaignMain.cm.getPlayer(defender);
        	if(player != null) {
        		SArmy army = player.getArmy(defenders.get(defender));
        		if (army != null)
        			bv += army.getBV();
        	}
        }
        setDefenderStartBV(bv);     
	}
	
	public OperationReporter () {
		
	}
}
