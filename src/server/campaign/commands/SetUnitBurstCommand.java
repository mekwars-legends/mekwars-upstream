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


import java.util.Iterator;
import java.util.StringTokenizer;
import server.campaign.SPlayer;
import server.campaign.CampaignMain;
import server.campaign.SUnit;

import megamek.common.Entity;
import megamek.common.Mounted;

public class SetUnitBurstCommand implements Command {
	
	int accessLevel = 0;
	String syntax = "";
	public int getExecutionLevel(){return accessLevel;}
	public void setExecutionLevel(int i) {accessLevel = i;}
	public String getSyntax() { return syntax;}
	
	public void process(StringTokenizer command,String Username) {
		
		if (accessLevel != 0) {
			int userLevel = CampaignMain.cm.getServer().getUserLevel(Username);
			if(userLevel < getExecutionLevel()) {
				CampaignMain.cm.toUser("Insufficient access level for command. Level: " + userLevel + ". Required: " + accessLevel + ".",Username,true);
				return;
			}
		}
		
		SPlayer p = CampaignMain.cm.getPlayer(Username);
		
		int unitid= 0;//ID# of the mech which is to set ammo change
		int weaponLocation = 0; //starting position for weapon
		boolean selection = false; //burst on or off
		
		try {
			unitid= Integer.parseInt(command.nextToken());
		}//end try
		catch (NumberFormatException ex) {
			CampaignMain.cm.toUser("SetBurstAmmo command failed. Check your input. It should be something like this: /c setUnitAmmo#unitid#weaponlocation#true/false",Username,true);
			return;
		}//end catch
		
		try {
			weaponLocation = Integer.parseInt(command.nextToken());
		}//end try
		catch (Exception ex){
			CampaignMain.cm.toUser("SetBurstAmmo command failed. Check your input. It should be something like this: /c setUnitAmmo#unitid#weaponlocation#true/false",Username,true);
			return;
		}//end catch
		
		try {
			selection = new Boolean(command.nextToken()).booleanValue();
		}//end try
		catch (Exception ex){
			CampaignMain.cm.toUser("SetBurstAmmo command failed. Check your input. It should be something like this: /c setUnitAmmo#unitid#weaponlocation#true/false",Username,true);
			return;
		}//end catch
		
		
		SUnit unit = p.getUnit(unitid);
		Entity en = unit.getEntity();
		Iterator e = en.getWeapons();
		int location = 0;
		Mounted mWeapon = null;
		
		while (e.hasNext())
		{
			mWeapon = (Mounted)e.next();
			if ( location == weaponLocation)
				break;
			location++;
		}
		
		if ( mWeapon.isRapidfire() == selection )
		    return;
		
		mWeapon.setRapidfire(selection);
		unit.setEntity(en);
		CampaignMain.cm.toUser("PL|UU|"+unit.getId()+"|"+unit.toString(true),Username,false);
		
		CampaignMain.cm.toUser("Rapid fire set for " + unit.getModelName() + " (#" +unit.getId()+").",Username,true);
		
	}//end process() 
}//end SetMaintainedCommand class
