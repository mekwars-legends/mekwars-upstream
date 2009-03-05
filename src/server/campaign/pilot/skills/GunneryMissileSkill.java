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

import common.MegaMekPilotOption;
import common.Unit;
import common.campaign.pilot.Pilot;


/**
 * NOTE: This is a unofficial rule. Pilot gets a -1 to-hit bonus on all missile weapons (LRM, SRM, MRM, RL and ATM).
 * @@author Torren (Jason Tighe)
 */
public class GunneryMissileSkill extends SPilotSkill {

    public GunneryMissileSkill(int id) {
        super(id, "Gunnery/Missile", "GM");
        setDescription("NOTE: This is a unofficial rule. Pilot gets a -1 to-hit bonus on all ballistic weapons (MGs, all ACs, Gaussrifles).");
    }

    public GunneryMissileSkill() {
    	//TODO: replace with ReflectionProvider
    }

    @Override
	public int getChance(int unitType, Pilot pilot) {
    	if (pilot.getSkills().has(this)) {
            return 0;
        }

    	String chance = "chancefor"+getAbbreviation()+"for"+Unit.getTypeClassDesc(unitType);

		SHouse house = CampaignMain.cm.getHouseFromPartialString(pilot.getCurrentFaction());

		if ( house == null ) {
            return CampaignMain.cm.getIntegerConfig(chance);
        }

		return house.getIntegerConfig(chance);
    }

    @Override
	public void modifyPilot(Pilot pilot) {
        pilot.addMegamekOption(new MegaMekPilotOption("gunnery_missile",true));
//        pilot.setBvMod(pilot.getBVMod() +  0.02);
    }
    @Override
	public int getBVMod(Entity unit){
        double missileBV = 0;
        double gunneryMissileBVBaseMod = megamek.common.Pilot.getBVSkillMultiplier(unit.getCrew().getGunnery()-1, unit.getCrew().getPiloting());

        for (Mounted weapon : unit.getWeaponList()){
            if ( weapon.getType().hasFlag(WeaponType.F_MISSILE) ) {
                missileBV += weapon.getType().getBV(unit);
            }
        }
        return (int)(missileBV * gunneryMissileBVBaseMod);
    }
}
