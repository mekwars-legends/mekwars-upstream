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


import java.util.Vector;

import megamek.common.Entity;

import server.campaign.CampaignMain;
import server.campaign.SHouse;

import common.Unit;
import common.campaign.pilot.Pilot;


/**
 * Pilot traits for use with moding the gaining of other traits
 * @@author Torren (Jason Tighe)
 */
public class TraitSkill extends SPilotSkill {

    public TraitSkill(int id) {
        super(id, "Trait", "TN");
        this.setDescription("Pilot traits for use with moding the gaining of other skills");
    }
    
    public TraitSkill() {
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
    }
    
    @Override
	public int getBVMod(Entity unit){
        return 0;
    }
        
    public void assignTrait(Pilot p){
        int size = 0;
        int choice = 0;
        String Trait = "none";
        String faction = p.getCurrentFaction();

        //MWServ.mwlog.errLog("Trait Skill Faction: "+faction);
        Vector traitNames = CampaignMain.cm.getFactionTraits(faction);

        size = traitNames.size();
        
        //MWServ.mwlog.errLog("Trait Skill size: "+size);
        
        if ( size < 1 )
            return;
        
        if ( size == 1)
            Trait = (String)traitNames.elementAt(0);
        else{
	        choice = CampaignMain.cm.getRandomNumber(size);
	
	        //MWServ.mwlog.errLog("Trait Skill choice: "+choice);
	        
	        for (int i = 0; i < choice; i++)
	            Trait = (String)traitNames.elementAt(i);
        }
        if ( Trait.indexOf("*") > -1)
            p.setTraitName(Trait.substring(0,Trait.indexOf("*")));
        else
            p.setTraitName(Trait);
    }
}
