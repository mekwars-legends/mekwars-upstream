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


import java.io.File;
import java.util.StringTokenizer;

import common.Unit;
import common.util.StringUtils;
import common.util.UnitUtils;

import server.campaign.BuildTable;
import server.campaign.SUnit;
import server.campaign.SPlayer;
import server.campaign.SHouse;
import server.campaign.CampaignMain;
import server.campaign.pilot.SPilot;

public class RequestDonatedCommand implements Command {
	
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
		
		/*
		 * A command which checks qualifications for a request for a DONATED mech (XP/Cbills/IP, etc)
		 * and generates a string which describes the outcome of the request. Note that donated units
		 * cost HALF as much as new units in terms of Cbills and influence.
		 *
		 * USAGE: /c requestdonated#WEIGHTCLASS#TYPEID
		 */
		
		//load player, faction and defaults
		SPlayer p = CampaignMain.cm.getPlayer(Username);
        SHouse house = p.getMyHouse();
		int weightclass = Unit.LIGHT;
		int type_id = Unit.MEK;
		String result = "";

        boolean useBays = CampaignMain.cm.isUsingAdvanceRepair(); 
        
		//get the weightclass
		String weightstring = command.nextToken().toUpperCase();
		try {
			weightclass = Integer.parseInt(weightstring);
		} catch (Exception ex) {
			weightclass = Unit.getWeightIDForName(weightstring);
		}
		
		//get the unit type
		String typestring = command.nextToken();
		try {
			type_id = Integer.parseInt(typestring);
		} catch (Exception ex){
			type_id = Unit.getTypeIDForName(typestring);
		}
		
		//tell newbs they cant buy hangar units, but give them a link to request new units.
		if (p.getMyHouse().isNewbieHouse()) {
			result = "Players in the training faction may not purchase used/donated units; however, they may reset their units.";
			result += "<br><a href=\"MEKWARS/c request#resetunits\">Click here to request a reset of your units.</a>";
			CampaignMain.cm.toUser(result,Username,true);
			return;
		}
		
        if (p.mayAcquireWelfareUnits()) {
            SUnit unit = this.buildWelfareMek(house.getName());
            
            SPilot pilot = house.getNewPilot(unit.getType());
            unit.setPilot(pilot);
            p.addUnit(unit, true);
            CampaignMain.cm.toUser("High Command has given you a Mek from its welfare rolls to help you get back on your feet!",Username,true);
            
            return;
        }
        
        if (!p.mayUse(weightclass)) {
			result = "You are not experienced enough to use " + Unit.getWeightClassDesc(weightclass) + " units.";
			CampaignMain.cm.toUser(result,Username,true);
			return;
		}
		
		//get the unit from the faction vector. break out if there is no unit to be had.
		SUnit u = house.getEntity(weightclass,type_id);
		if (u == null) {//if getEntity returned null, there is none to give the player
			result = "There is no unit of the requested weight class/type avaliable";
			CampaignMain.cm.toUser(result,Username,true);
			return;
		}
		
		//boot the player's request if he has unmaintained units
		if (p.hasUnmaintainedUnit()) {
			result = "Your faction refuses to assign additional units to you force while existing resources are not being properly maintained!";
			house.addUnit(u, false);//add the retrieved mech back to the pool
			CampaignMain.cm.toUser(result,Username,true);
			return;
		}//end if(has an unmaintained unit)
		
		//get money and inf costs.
		int unitCbills = Math.round(Float.parseFloat(house.getConfig("UsedPurchaseCostMulti")) * house.getPriceForUnit(u.getWeightclass(),u.getType()));
        int unitInfluence = Math.round(Float.parseFloat(house.getConfig("UsedPurchaseCostMulti")) * house.getInfluenceForUnit(u.getWeightclass(),u.getType()));
        if (unitCbills < 0)
        	unitCbills = 0;
        if (unitInfluence < 0)
        	unitInfluence = 0;
        
        if (Boolean.parseBoolean(house.getConfig("UseCalculatedCosts")))
            unitCbills = Math.round(Float.parseFloat(house.getConfig("UsedPurchaseCostMulti")) * house.getHighestUnitCost(u.getWeightclass(),u.getType()) * Float.parseFloat(house.getConfig("CostModifier")));
		
        if (CampaignMain.cm.isUsingAdvanceRepair()) {
            if ( !UnitUtils.canStartUp(u.getEntity()) )
                unitCbills = Math.round(unitCbills * Float.parseFloat(house.getConfig("CostModifierToBuyEnginedUnit")));
            else if ( UnitUtils.hasCriticalDamage(u.getEntity()) )
                unitCbills = Math.round(unitCbills * Float.parseFloat(house.getConfig("CostModifierToBuyCritDamagedUnit")));
            else if ( UnitUtils.hasArmorDamage(u.getEntity()) )
                unitCbills = Math.round(unitCbills * Float.parseFloat(house.getConfig("CostModifierToBuyArmorDamagedUnit")));
        }
        
        if (unitCbills > p.getMoney() || unitInfluence > p.getInfluence()) {
            house.addUnit(u, false);//add the retrieved mech back to the pool
            CampaignMain.cm.toUser("You cannot afford to purchase " + StringUtils.aOrAn(Unit.getWeightClassDesc(u.getWeightclass()),true) +
            		" " + Unit.getTypeClassDesc(u.getType()) + " from the faction bay (Requires " + CampaignMain.cm.moneyOrFluMessage(true,false,unitCbills) +
            		", " + CampaignMain.cm.moneyOrFluMessage(false,true,unitInfluence) + ").",Username,true);
            return;
        }

        //check to make sure the player has enough support for the unit requested. if not, send hire and buy links.
		int spaceTaken = SUnit.getHangarSpaceRequired(type_id, weightclass,0,"null");
		if (spaceTaken > p.getFreeBays()) {//if only needs more technicians
			int techCost = p.getTechHiringFee();
            if ( useBays )
                techCost = Integer.parseInt(house.getConfig("CostToBuyNewBay"));
            
			int numTechs = spaceTaken - p.getFreeBays();
			techCost = techCost * numTechs;
			int totalCost = unitCbills + techCost;
			
			if (totalCost > p.getMoney()) {
                if ( useBays )
                    result = "Command will not assign the requested unit to your force unless support is in place; however, you cannot afford to " +
                    "buy the unit *and* purchase bays. Total cost would be " + CampaignMain.cm.moneyOrFluMessage(true,false,totalCost)+" and you only have " + p.getMoney() + ".";
                else
                    result = "Command will not assign the requested unit to your force unless support is in place; however, you cannot afford to " +
                    "buy the unit *and* hire technicians. Total cost would be " + CampaignMain.cm.moneyOrFluMessage(true,false,totalCost)+" and you only have " + p.getMoney() + ".";
				house.addUnit(u, false);//couldnt afford, so add the retrieved mech back to the pool
				CampaignMain.cm.toUser(result,Username,true);
				return;
			}
			
            if (useBays) {
    			result = "Quartermaster command will not release the requested unit to your force unless support resources are in place. You will " +
    			"need to purchase " + numTechs + " more bays (total cost: " + CampaignMain.cm.moneyOrFluMessage(true,true,techCost)+"). Combined cost of the requested unit and necessary " +
    			"bays is " +CampaignMain.cm.moneyOrFluMessage(true,true,totalCost)+" and " + CampaignMain.cm.moneyOrFluMessage(false,true,unitInfluence)+".";
    			result += "<br><a href=\"MEKWARS/c hireandrequestused#" + numTechs + "#" +
    			Unit.getWeightClassDesc(weightclass) + "#" + type_id + "\">Click here to purchase the bays and purchase the unit.</a>";
            }
            else {
                result = "Quartermaster command will not release the requested unit to your force unless support resources are in place. You will " +
                "need to hire " + numTechs + " more technicians (total tech cost: " + CampaignMain.cm.moneyOrFluMessage(true,true,techCost)+"). Combined cost of the requested unit and necessary " +
                "technicians is " +CampaignMain.cm.moneyOrFluMessage(true,true,totalCost)+" and " + CampaignMain.cm.moneyOrFluMessage(false,true,unitInfluence)+".";
                result += "<br><a href=\"MEKWARS/c hireandrequestused#" + numTechs + "#" +
                Unit.getWeightClassDesc(weightclass) + "#" + type_id + "\">Click here to hire the technicians and purchase the unit.</a>";
            }
            
			house.addUnit(u, false);//didnt complete the buy, so add the retrieved mech back to the pool
			CampaignMain.cm.toUser(result,Username,true);
			return;//break out ...
		}//end if (needsMoreTechs)
		
		//We're going to be giving the player the unit. Include a pilot, if we're not using queues or its a non-mek
		boolean needsPilotAlways = !Boolean.parseBoolean(house.getConfig("AllowPersonalPilotQueues"));
		boolean needsPilotWithPPQ = (u.getType() != Unit.MEK) && (u.getType() != Unit.PROTOMEK);
		if (needsPilotAlways || needsPilotWithPPQ)
			u.setPilot(p.getMyHouse().getNewPilot(type_id));
		
		p.addUnit(u, true);//if both tests were passed, give the unit
		p.addMoney(-unitCbills);//then take away money
		p.addInfluence(-unitInfluence);//and take away influence
		
		result = "You've been granted a " + u.getModelName() + ". (-" + CampaignMain.cm.moneyOrFluMessage(true,false,unitCbills)+" / -" + CampaignMain.cm.moneyOrFluMessage(false,true,unitInfluence)+") ";
		CampaignMain.cm.toUser(result,Username,true);
		CampaignMain.cm.doSendHouseMail(house,"NOTE",p.getName() + " bought " + StringUtils.aOrAn(u.getVerboseModelName(),true) + " from the faction bay!");
		
		//entity removed from SHouse. Send update to effected players
		CampaignMain.cm.doSendToAllOnlinePlayers(house, "HS|" + house.getHSUnitRemovalString(u), false);
		
	}//end process()
	
	/**
	 * Private method which builds a welfare unit. Duplicated in RequestCommand.
	 * Kept private in these classes in order to ensure that ONLY requests generate
	 * welfare units (had previously been a public call in CampaignMain).
	 */
	private SUnit buildWelfareMek(String producer){
		String Filename = "./data/buildtables/standard/"+producer+"_Welfare.txt";
		
		//Check for Faction Specific welfare tables first
		if ( !(new File(Filename).exists()) )
			Filename = "./data/buildtables/standard/Welfare.txt";

		String unitFileName = BuildTable.getUnitFilename(Filename);
        SUnit cm = new SUnit(producer,unitFileName,Unit.LIGHT);

        return cm;
    }
	
}//end RequestDonatedCommand