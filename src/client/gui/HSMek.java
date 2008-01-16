/*
 * MekWars - Copyright (C) 2004 
 * 
 * Derived from MegaMekNET (http://www.sourceforge.net/projects/megameknet)
 * Original author Helge Richter (McWizard)
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

package client.gui;

import java.util.StringTokenizer;

import common.campaign.pilot.Pilot;

import megamek.common.BattleArmor;
import megamek.common.Entity;
import megamek.common.Infantry;
import megamek.common.Mech;
import megamek.common.Protomech;
import megamek.common.QuadMech;

import client.MWClient;
import client.campaign.CUnit;

public class HSMek {
	
	String MekFile;
	int unitID;
	
	String name;
	String type;
	String battleDamage = "";
	
	CUnit embeddedUnit;//bury a CUnit in HSMek, a la BMUnit
	
	public HSMek(MWClient mwclient, StringTokenizer tokenizer) {
		
		this.MekFile = tokenizer.nextToken();
		unitID = Integer.valueOf(tokenizer.nextToken());
		
        if (tokenizer.hasMoreTokens())
            battleDamage = tokenizer.nextToken();
        		
		//bury a CUnit
		embeddedUnit = new CUnit();
		embeddedUnit.setUnitFilename(MekFile);
		embeddedUnit.createEntity();

        /*
		 * CUnit.createEntity sets type. Now that we've bootstrapped the
		 * type in, we know if we need to set piloting and gunnery (meks,
		 * vehicles) or just gunnery (misc. infantry types).
		 */
		int factionGunnery = mwclient.getCampaign().getPlayer().getMyHouse().getBaseGunner();
		int factionPiloting = mwclient.getCampaign().getPlayer().getMyHouse().getBasePilot();
		if (embeddedUnit.getType() != CUnit.PROTOMEK )
		    
		    if ( embeddedUnit.getType() == CUnit.INFANTRY  ){
		        if ( ((Infantry)embeddedUnit.getEntity()).isAntiMek() )
    		        embeddedUnit.setPilot(new Pilot("BM Unit",factionGunnery,factionPiloting));
    		    else
    		        embeddedUnit.setPilot(new Pilot("BM Unit",factionGunnery,5));
		    }
		    else
		        embeddedUnit.setPilot(new Pilot("BM Unit",factionGunnery,factionPiloting));
		else 
			embeddedUnit.setPilot(new Pilot("BM Unit",factionGunnery,5));
		
		/*
		 * HSMek.getBV() uses MegaMek's calculateBV() function instead of pulling the
		 * stringed CUnit BV. The server sends over units without pilot data, so we set
		 * a faction-default crew. See CHSPanel.java for usage. 
		 */
		embeddedUnit.getEntity().setCrew(new megamek.common.Pilot("Generic Pilot", factionGunnery, factionPiloting));
	
		//set type
		Entity e = embeddedUnit.getEntity();
		if (e instanceof Mech || e instanceof QuadMech)
            type = "Mek";
        else if (e instanceof Protomech)
            type = "Protomek";
        else if (e instanceof BattleArmor)
            type = "BattleArmor";
        else if (e instanceof Infantry)
            type = "Infantry";
        else 
            type = "Vehicle";
		
		//vehicles and inf prepend chassis
		if (type.equalsIgnoreCase("Mek"))
		    if (e.isOmni())
		        name = e.getChassis() + " " +  e.getModel();
		    else
		        name = e.getModel();
        else
            name = e.getShortNameRaw();
	}
	
	public Entity getEntity() {		
		return embeddedUnit.getEntity();
	}
	
	public String getMekFile() {
		return MekFile;
	}
	
	public String getName() {
		return name.toString();
	}
	
	public String getType() {
		return type;
	}
	
	public int getUnitID() {
		return unitID;
	}
    
    public String getBattleDamage() {
        return battleDamage;
    }
    
    public int getBV() {
    	return embeddedUnit.getEntity().calculateBattleValue(false,false);
    }
 
}
