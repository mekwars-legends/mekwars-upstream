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

import server.campaign.SPlayer;
import server.campaign.CampaignMain;
import server.campaign.SUnit;

import megamek.common.Entity;
import megamek.common.AmmoType;
import megamek.common.Mounted;

public class ReloadAllAmmoCommand implements Command {
	
	int accessLevel = 0;
	String syntax = "";
	public int getExecutionLevel(){return accessLevel;}
	public void setExecutionLevel(int i) {accessLevel = i;}
	public String getSyntax() { return syntax;}
	
	public void process(StringTokenizer command,String Username) {
		
		if (accessLevel != 0) {
			int userLevel = CampaignMain.cm.getServer().getUserLevel(Username);
			if(userLevel < getExecutionLevel()) {
				CampaignMain.cm.toUser("AM:Insufficient access level for command. Level: " + userLevel + ". Required: " + accessLevel + ".",Username,true);
				return;
			}
		}
		
		SPlayer p = CampaignMain.cm.getPlayer(Username);
		
		int unitid= 0;//ID# of the mech which is to set ammo change
        
		try {
			unitid= Integer.parseInt(command.nextToken());
		} catch (NumberFormatException ex) {
			CampaignMain.cm.toUser("AM:ReloadAllAmmo command failed. Check your input. It should be something like this: /c reloadAllAmmo#unitid",Username,true);
			return;
		}
		
		SUnit unit = p.getUnit(unitid);
		Entity en = unit.getEntity();
		int cost = 0;
		int refillShots = 0;
		double ammoCharge = 0;

		if ( !CampaignMain.cm.getBooleanConfig("UsePartsRepair") ) {
			
			for ( Mounted ammo : en.getAmmo()) {
				
				AmmoType baseAmmo = (AmmoType)ammo.getType();
				
	            refillShots = baseAmmo.getShots();
	    		ammoCharge = CampaignMain.cm.getAmmoCost(baseAmmo.getInternalName());
	    		if ( ammoCharge < 0 ){
	    		    continue;
	    		}
	            //Single shot weapons should only cost 1 shot
	            if ( ammo.getLocation() == Entity.LOC_NONE ){
		                refillShots = 1;
	            }//Parital Reloads
	            else if ( refillShots != ammo.getShotsLeft() ){
	            	 refillShots -= ammo.getShotsLeft();

	            } else 
	            	continue;
	            ammoCharge *= (double)refillShots; 
	            cost += ammoCharge;
			}
	
			if ( cost > p.getMoney() ) {
				CampaignMain.cm.toUser("AM:You do not have enough to fully reload Unit #" +unit.getId()+". It would cost "+CampaignMain.cm.moneyOrFluMessage(true,false,cost), Username);
				return;
			}
			
			for ( Mounted ammo : en.getAmmo()) {
				
				AmmoType baseAmmo = (AmmoType)ammo.getType();

				//Do not refill banned ammo
				ammoCharge = CampaignMain.cm.getAmmoCost(baseAmmo.getInternalName());
                if ( ammoCharge < 0 ){
                    continue;
                }

	            refillShots = baseAmmo.getShots();

	            //Single shot weapons should only cost 1 short i.e. total shots = 10 then price is 1/10th minium 1.
	            if ( ammo.getLocation() == Entity.LOC_NONE ){
	                refillShots = 1 ;
	            }else if (baseAmmo.getAmmoType() == AmmoType.T_ROCKET_LAUNCHER){
	                refillShots = 1 ;
	            }
	            ammo.setShotsLeft(refillShots);
			}
		}else {
			for (Mounted ammo : en.getAmmo()) {
				AmmoType baseAmmo = (AmmoType)ammo.getType();
	           	//ammo.changeAmmoType(baseAmmo);
	            refillShots = baseAmmo.getShots();
	           	int ammoAmount = 0;

	           	if ( (ammo.getLocation() == Entity.LOC_NONE ||
	            		baseAmmo.getAmmoType() == AmmoType.T_ROCKET_LAUNCHER)
	            		&& ammo.getShotsLeft() != 1) {
	            	ammoAmount = 1;
	            	refillShots = 1;
	           	}
	            else if ( refillShots != ammo.getShotsLeft() ){
	            	ammoAmount = (refillShots - ammo.getShotsLeft());
	            	if ( p.getPartsAmount(baseAmmo.getInternalName()) < ammoAmount ) {
	            		
						if ( p.getAutoReorder() ){
							String newCommand = baseAmmo.getInternalName()+"#"+ammoAmount;
							CampaignMain.cm.getServerCommands().get("BUYPARTS").process(new StringTokenizer(newCommand,"#"), Username);
						}
						ammoAmount = p.getPartsAmount(baseAmmo.getInternalName());
	            	}
	            	refillShots = ammoAmount+ammo.getShotsLeft();
	            } else 
	            	continue;
        		p.updatePartsCache(baseAmmo.getInternalName(), -ammoAmount);
    			ammo.setShotsLeft(refillShots);
			}
			
			
		}
		//unit.toString() sent's BV to zero and recalculates, so we don't need to do it in this Command class.
		CampaignMain.cm.toUser("PL|UU|"+unit.getId()+"|"+unit.toString(true),Username,false);
		
		p.addMoney(-cost);
		CampaignMain.cm.toUser("AM:Ammo set for " + unit.getModelName() + " (#" +unit.getId()+") at a cost of "+CampaignMain.cm.moneyOrFluMessage(true,false,cost),Username,true);
		
	}//end process() 
}//end SetMaintainedCommand class
