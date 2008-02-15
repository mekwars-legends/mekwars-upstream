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
		
	
	public OperationReporter () {
		
	}
}
