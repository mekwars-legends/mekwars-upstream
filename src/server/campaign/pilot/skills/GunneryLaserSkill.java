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

/*
 * Created on 18.04.2004
 *
 */
package server.campaign.pilot.skills;

import megamek.common.Compute;
import megamek.common.Entity;
import megamek.common.Mounted;
import megamek.common.WeaponType;
import server.campaign.CampaignMain;
import server.campaign.SHouse;
import common.MegaMekPilotOption;
import common.Unit;
import common.campaign.pilot.Pilot;


/**
 * NOTE: This is a unofficial rule. Pilot gets a -1 to-hit bonus on all energy-based weapons (Laser, PPC, and Flamer).
 * @@author Torren (Jason Tighe)
 */
public class GunneryLaserSkill extends SPilotSkill {

    public GunneryLaserSkill(int id) {
        super(id, "Gunnery/Laser", "GL");
        this.setDescription("NOTE: This is a unofficial rule. Pilot gets a -1 to-hit bonus on all energy-based weapons (Laser, PPC, and Flamer).");
    }
    
    public GunneryLaserSkill() {
    	//TODO: replace with ReflectionProvider
    }

    @Override
	public int getChance(int unitType, Pilot pilot) {
    	if (pilot.getSkills().has(this))
    		return 0;

    	String chance = "chancefor"+this.getAbbreviation()+"for"+Unit.getTypeClassDesc(unitType);
    	
		SHouse house = CampaignMain.cm.getHouseFromPartialString(pilot.getCurrentFaction());
		
		if ( house == null )
			return CampaignMain.cm.getIntegerConfig(chance);
		
		return Integer.parseInt(house.getConfig(chance));
    }

    @Override
	public void modifyPilot(Pilot pilot) {
        pilot.addMegamekOption(new MegaMekPilotOption("gunnery_laser",true));
        //pilot.setBvMod(pilot.getBVMod() +  0.02);
    }
    
    @Override
	public int getBVMod(Entity unit){
        double laserBV = 0;
        double gunneryLaserBVBaseMod = megamek.common.Pilot.getBVSkillMultiplier(unit.getCrew().getGunnery()-1, unit.getCrew().getPiloting());
        
        for(Mounted weapon : unit.getWeaponList() ){
            if ( weapon.getType().hasFlag(WeaponType.F_ENERGY) ) {
                laserBV += weapon.getType().getBV(unit);
            }
        }
        return (int)(laserBV * gunneryLaserBVBaseMod);
    }
}
