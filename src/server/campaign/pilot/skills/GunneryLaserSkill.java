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

import megamek.common.Entity;
import megamek.common.Mounted;
import megamek.common.WeaponType;
import server.campaign.CampaignMain;
import server.campaign.SHouse;
import server.campaign.pilot.SPilot;

import common.CampaignData;
import common.MegaMekPilotOption;
import common.Unit;
import common.campaign.pilot.Pilot;

/**
 * NOTE: This is a unofficial rule. Pilot gets a -1 to-hit bonus on all
 * energy-based weapons (Laser, PPC, and Flamer).
 * 
 * @@author Torren (Jason Tighe)
 */
public class GunneryLaserSkill extends SPilotSkill {

    public GunneryLaserSkill(int id) {
        super(id, "Gunnery/Laser", "GL");
        setDescription("NOTE: This is a unofficial rule. Pilot gets a -1 to-hit bonus on all energy-based weapons (Laser, PPC, and Flamer).");
    }

    public GunneryLaserSkill() {
        // TODO: replace with ReflectionProvider
    }

    @Override
    public int getChance(int unitType, Pilot pilot) {
        if (pilot.getSkills().has(this)) {
            return 0;
        }

        String chance = "chancefor" + getAbbreviation() + "for" + Unit.getTypeClassDesc(unitType);

        SHouse house = CampaignMain.cm.getHouseFromPartialString(pilot.getCurrentFaction());

        if (house == null) {
            return CampaignMain.cm.getIntegerConfig(chance);
        }

        return house.getIntegerConfig(chance);
    }

    @Override
    public void modifyPilot(Pilot pilot) {
        pilot.addMegamekOption(new MegaMekPilotOption("gunnery_laser", true));
        // pilot.setBvMod(pilot.getBVMod() + 0.02);
    }

    @Override
    public int getBVMod(Entity unit) {
    	if (CampaignMain.cm.getBooleanConfig("USEFLATGUNNERYLASERMODIFIER")) {
    		CampaignData.mwlog.debugLog("Using Flat GL Mod");
    		return getBVModFlat(unit);
    	}
        double laserBV = 0;
        double gunneryLaserBVBaseMod = megamek.common.Pilot.getBVSkillMultiplier(unit.getCrew().getGunnery() - 1, unit.getCrew().getPiloting());
        double originalLaserBV = 0;
        for (Mounted weapon : unit.getWeaponList()) {
            if (weapon.getType().hasFlag(WeaponType.F_ENERGY)) {
                laserBV += weapon.getType().getBV(unit);
                originalLaserBV += weapon.getType().getBV(unit);
            }
        }
        // This is adding the base BV of the weapon twice - once originally, and once here.
        // Need to back out the original cost so that it only gets added once.
        CampaignData.mwlog.debugLog("Laser BV: " + laserBV);
        CampaignData.mwlog.debugLog("Original Laser BV: " + originalLaserBV);
        CampaignData.mwlog.debugLog("Mod: " + (int) ((laserBV * gunneryLaserBVBaseMod) - originalLaserBV));

        return (int) ((laserBV * gunneryLaserBVBaseMod) - originalLaserBV);
    }

    @Override
    public int getBVMod(Entity unit, SPilot p) {
        return getBVMod(unit);
    }
    
	public int getBVModFlat(Entity unit){
        int numberOfLasers = 0;
        int gunneryLaserBVBaseMod = CampaignMain.cm.getIntegerConfig("GunneryLaserBaseBVMod");
        
        for(Mounted weapon : unit.getWeaponList() ){
            if ( weapon.getType().hasFlag(WeaponType.F_ENERGY) )
                numberOfLasers++;
        }
        return numberOfLasers * gunneryLaserBVBaseMod;
    }
}
