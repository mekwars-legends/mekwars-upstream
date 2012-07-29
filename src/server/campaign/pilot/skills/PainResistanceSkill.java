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

package server.campaign.pilot.skills;

import java.util.Iterator;

import megamek.common.AmmoType;
import megamek.common.Entity;
import megamek.common.Mounted;
import server.campaign.CampaignMain;
import server.campaign.SHouse;
import server.campaign.pilot.SPilot;

import common.MegaMekPilotOption;
import common.Unit;
import common.campaign.pilot.Pilot;

/**
 * @author Helge Richter
 */
public class PainResistanceSkill extends SPilotSkill {

    public PainResistanceSkill() {
        // TODO: replace with ReflectionProvider
    }

    public PainResistanceSkill(int id) {
        super(id, "Pain Resistance", "PR");
        setDescription("When making consciousness rolls, 1 is added to all rolls. Also, damage received from ammo explosions is reduced to 1. Note: This ability is only used for BattleMechs.");
    }

    @Override
    public void modifyPilot(Pilot p) {
        // super.addToPilot(p);
        p.addMegamekOption(new MegaMekPilotOption("pain_resistance", true));
        p.setBvMod(p.getBVMod() + 0.01);
    }

    @Override
    public int getChance(int unitType, Pilot p) {
        if (p.getSkills().has(this)) {
            return 0;
        }

        if (unitType != Unit.MEK) {
            return 0;
        }

        String chance = "chancefor" + getAbbreviation() + "for" + Unit.getTypeClassDesc(unitType);

        SHouse house = CampaignMain.cm.getHouseFromPartialString(p.getCurrentFaction());

        if (house == null) {
            return CampaignMain.cm.getIntegerConfig(chance);
        }

        return house.getIntegerConfig(chance);
    }

    @Override
    public int getBVMod(Entity unit) {
        int amountOfAmmo = 0;
        int PainResistanceBVBaseMod = CampaignMain.cm.getIntegerConfig("PainResistanceBaseBVMod");

        Iterator<Mounted> ammoList = unit.getAmmo().iterator();
        while (ammoList.hasNext()) {
            Mounted ammoType = ammoList.next();

            if (ammoType.getUsableShotsLeft() <= 0) {
                continue;
            }

            AmmoType ammo = (AmmoType) ammoType.getType();
            if ((ammo.getAmmoType() == AmmoType.T_GAUSS) || (ammo.getAmmoType() == AmmoType.T_GAUSS_HEAVY) || (ammo.getAmmoType() == AmmoType.T_GAUSS_LIGHT)) {
                continue;
            }

            amountOfAmmo++;
        }

        Iterator<Mounted> weaponsList = unit.getWeapons();
        while (weaponsList.hasNext()) {
            Mounted weapon = weaponsList.next();
            if (weapon.getName().indexOf("Gauss Rifle") != -1) {
                amountOfAmmo++;
            }
        }
        return amountOfAmmo * PainResistanceBVBaseMod;

    }

    @Override
    public int getBVMod(Entity unit, SPilot p) {
        int amountOfAmmo = 0;
        SHouse house = CampaignMain.cm.getHouseFromPartialString(p.getCurrentFaction());
        int PainResistanceBVBaseMod = house.getIntegerConfig("PainResistanceBaseBVMod");

        Iterator<Mounted> ammoList = unit.getAmmo().iterator();
        while (ammoList.hasNext()) {
            Mounted ammoType = ammoList.next();

            if (ammoType.getUsableShotsLeft() <= 0) {
                continue;
            }

            AmmoType ammo = (AmmoType) ammoType.getType();
            if ((ammo.getAmmoType() == AmmoType.T_GAUSS) || (ammo.getAmmoType() == AmmoType.T_GAUSS_HEAVY) || (ammo.getAmmoType() == AmmoType.T_GAUSS_LIGHT)) {
                continue;
            }

            amountOfAmmo++;
        }

        Iterator<Mounted> weaponsList = unit.getWeapons();
        while (weaponsList.hasNext()) {
            Mounted weapon = weaponsList.next();
            if (weapon.getName().indexOf("Gauss Rifle") != -1) {
                amountOfAmmo++;
            }
        }
        return amountOfAmmo * PainResistanceBVBaseMod;

    }

}