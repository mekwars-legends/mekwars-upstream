/*
 * MekWars - Copyright (C) 2004 
 * 
 * Derived from MegaMekNET (http://www.sourceforge.net/projects/megameknet)
 * Original Author - Jason Tighe (Torren)
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

import megamek.common.AmmoType;
import megamek.common.CriticalSlot;
import megamek.common.Entity;
import megamek.common.Mech;
import megamek.common.Mounted;

import common.Unit;
import common.util.StringUtils;
import common.util.UnitUtils;

import server.campaign.SPlayer;
import server.campaign.SUnit;
import server.campaign.SHouse;
import server.campaign.SUnitFactory;
import server.campaign.CampaignMain;
import server.campaign.pilot.SPilot;
import server.campaign.BuildTable;

import server.MWServ;

/**
 * 
 * @author Torren Aug 28, 2004
 * allows users to redeem award points.
 * they can redeem for techs, influence, or units
 * syntax for techs and influence: /c userewardpoints#typeofreward#amountofrewardpointstouse
 * syntax for units /c userewardpoints#typeofreward#unittype#unitweight#[faction]/[rare]
 * items in brackets are optional. purchasing a rare unit will cost more rewardpoints
 * 
 */
public class UseRewardPointsCommand implements Command {
	
	int accessLevel = 0;
	public int getExecutionLevel(){return accessLevel;}
	public void setExecutionLevel(int i) {accessLevel = i;}
	
	public void process(StringTokenizer command,String Username) {
		
		if (accessLevel != 0) {
			int userLevel = CampaignMain.cm.getServer().getUserLevel(Username);
			if(userLevel < getExecutionLevel()) {
				CampaignMain.cm.toUser("Insufficient access level for command. Level: " + userLevel + ". Required: " + accessLevel + ".",Username,true);
				return;
			}
		}
		
		/*
		 * rewardSelections:
		 * 0 Techs
		 * 1 Influence
		 * 2 Units
		 */
		
		int rewardSelection = Integer.parseInt(command.nextToken());
		int rewardPoints = 0;
        String techs = "";
        String rewards = "";

		SPlayer player = CampaignMain.cm.getPlayer(Username);
		SHouse house = player.getMyHouse();
		if (rewardSelection < 0 || rewardSelection > 3 ){
			CampaignMain.cm.toUser("Invalid reward selection. 0 for techs, 1 for influence, 2 for units, 3 for repair.",Username,true);
			return;
		}
		switch(rewardSelection){
		case 0:  //buying techs.
			rewardPoints = Integer.parseInt(command.nextToken());
			
			if (rewardPoints < 0) {
				CampaignMain.cm.toUser("Invalid input - negative reward points.",Username,true);
				return;
			}
			
			if ( !(new Boolean(house.getConfig("AllowTechsForRewards")).booleanValue()) ){
				CampaignMain.cm.toUser("Sorry but you are not allowed to buy techs with reward points.",Username,true);
				return;
			}
			
			if ( rewardPoints > player.getReward() )
			{
				if ( player.getReward() == 1)
					CampaignMain.cm.toUser("You only have 1 reward point. Try again later.",Username,true);
				else
					CampaignMain.cm.toUser("You only have " + player.getReward() + " reward points. Try again later.",Username,true);
				return; 
			}
            if ( CampaignMain.cm.isUsingAdvanceRepair() ){
                int typeOfTechToBuy = rewardPoints;
                int techCost = Integer.parseInt(house.getConfig("RewardPointsFor"+UnitUtils.techDescription(typeOfTechToBuy)));
                
                if ( player.getReward() < techCost ){
                    CampaignMain.cm.toUser("You do not have enough reward points to buy this tech. You need "+techCost,Username,true);
                    return;
                }
                
                player.addReward(-techCost);
                player.addTotalTechs(typeOfTechToBuy,1);
                player.addAvailableTechs(typeOfTechToBuy,1);
                if (techCost > 1)
                    rewards = "s";
                
                CampaignMain.cm.toUser("You hired "+ StringUtils.aOrAn(UnitUtils.techDescription(typeOfTechToBuy),true)+ " tech for "+techCost+"RP"+rewards+".",Username,true);
                
            } else {
    			int numOfTechBought = (Integer.parseInt(house.getConfig("TechsForARewardPoint")));
    			numOfTechBought *= rewardPoints;
    			if ( numOfTechBought > 1 )
    				techs ="s";
    			if (rewardPoints > 1)
    				rewards = "s";
    			CampaignMain.cm.toUser("You hired " + numOfTechBought + " tech" + techs + " for " + rewardPoints + " reward point"+ rewards +".",Username,true);
    			player.addReward(-rewardPoints);
    			player.addTechnicians(numOfTechBought);
            }
			break;
			
		case 1: //buying influence
			rewardPoints = Integer.parseInt(command.nextToken());
			
			if (rewardPoints < 0) {
				CampaignMain.cm.toUser("Invalid input - negative reward points.",Username,true);
				return;
			}
			
			if ( !(new Boolean(house.getConfig("AllowInfluenceForRewards")).booleanValue())){
				CampaignMain.cm.toUser("Sorry but you are not allowed to buy influence with reward points.",Username,true);
				return;
			}
			
			if (rewardPoints > player.getReward()) {
				
				if (player.getReward() == 0)
					CampaignMain.cm.toUser("You don't have any reward points. Purchase fails.",Username,true);
				else {
					String toSend = "You only have " + player.getReward() + "reward point" + StringUtils.addAnS(player.getReward()) + ". Try again.";
					CampaignMain.cm.toUser(toSend,Username,true);
				}
				
				return; 
			}
			
			int amountOfInfluenceBought = (Integer.parseInt(house.getConfig("InfluenceForARewardPoint")));
			amountOfInfluenceBought *= rewardPoints;
			CampaignMain.cm.toUser("You've bought " + CampaignMain.cm.moneyOrFluMessage(false,true,amountOfInfluenceBought)+" for " + rewardPoints + " reward point" + StringUtils.addAnS(rewardPoints) + ".",Username,true);
			
			player.addReward(-rewardPoints);
			player.addInfluence(amountOfInfluenceBought);
			break;
		
		case 2: //buying units
			if ( !(new Boolean(house.getConfig("AllowUnitsForRewards")).booleanValue())){
				CampaignMain.cm.toUser("Sorry but you are not allowed to buy units with reward points.",Username,true);
				return;
			}
			int rewardPointsAvailable = player.getReward();
			int unitTotalRewardPointCost = 0;
			String typestring = command.nextToken();
			String weightstring = command.nextToken();
			int unitType = Unit.MEK;
			int unitWeight = Unit.LIGHT;
			int typeCost = (Integer.parseInt(house.getConfig("RewardPointsForAMek")));
			int weightCost = (Integer.parseInt(house.getConfig("RewardPointsForALight")));
			SHouse faction = player.getHouseFightingFor();
			double rareCost = 1;
			boolean buyRareUnit = false;
			SUnit newUnit = null;
			SPilot newPilot = null;
			String factionstring = "common";
			
			if ( new Boolean(house.getConfig("AllowRareUnitsForRewards")).booleanValue())
				rareCost = (Double.parseDouble(house.getConfig("RewardPointMultiplierForRare")));
			
			try {
				unitType = Integer.parseInt(typestring);
			} catch (Exception ex) {
				unitType = Unit.getTypeIDForName(typestring);
			}
			
			try {
				unitWeight = Integer.parseInt(weightstring);
			} catch (Exception ex) {
				unitWeight = Unit.getWeightIDForName(weightstring.toUpperCase());
			}
			
			if ( command.hasMoreElements()) {
				factionstring = command.nextToken();   	        
				if ( factionstring.equalsIgnoreCase("rare") ) {
					
					if ( !(new Boolean(house.getConfig("AllowRareUnitsForRewards")).booleanValue()) ) {
						CampaignMain.cm.toUser("Sorry. You are not allowed to buy rare units with your reward points.",Username,true);
						return;
					}
					
					//else
					buyRareUnit = true;
					factionstring = "Rare";
					
				}
				else if ( !factionstring.equalsIgnoreCase("common") )
					faction = CampaignMain.cm.getHouseFromPartialString(factionstring,Username);
				
				if ( faction == null ) {
					faction = player.getHouseFightingFor();
					if ( faction == null )
						factionstring = "Common";
				}
			}
			
			switch (unitType) {
				case Unit.MEK:
					typeCost = (Integer.parseInt(house.getConfig("RewardPointsForAMek")));
					break;
				case Unit.VEHICLE:
					typeCost = (Integer.parseInt(house.getConfig("RewardPointsForAVeh")));
					if (new Boolean(house.getConfig("UseOnlyOneVehicleSize")).booleanValue())
						unitWeight = Unit.LIGHT;
					break;
				case Unit.INFANTRY:
					typeCost = (Integer.parseInt(house.getConfig("RewardPointsForInf")));
					if (new Boolean(house.getConfig("UseOnlyLightInfantry")).booleanValue() )
						unitWeight = Unit.LIGHT;
					break;
				case Unit.PROTOMEK:
					typeCost = (Integer.parseInt(house.getConfig("RewardPointsForProto")));
					if (new Boolean(house.getConfig("UseOnlyLightInfantry")).booleanValue())
						unitWeight = Unit.LIGHT;
					break;
				case Unit.BATTLEARMOR:
					typeCost = (Integer.parseInt(house.getConfig("RewardPointsForBA")));
					if (new Boolean(house.getConfig("UseOnlyLightInfantry")).booleanValue() )
						unitWeight = Unit.LIGHT;
					break;
			}
			
			switch (unitWeight) {
				case Unit.LIGHT:
					weightCost = (Integer.parseInt(house.getConfig("RewardPointsForALight")));
					break;
				case Unit.MEDIUM:
					weightCost = (Integer.parseInt(house.getConfig("RewardPointsForAMed")));
					break;
				case Unit.HEAVY:
					weightCost = (Integer.parseInt(house.getConfig("RewardPointsForAHeavy")));
					break;
				case Unit.ASSAULT:
					weightCost = (Integer.parseInt(house.getConfig("RewardPointsForAnAssault")));
					break;
			}
			
			unitTotalRewardPointCost = weightCost + typeCost;
			
			if ( !player.getHouseFightingFor().equals(faction) ) {
				double nonHouseUnitMod = Double.parseDouble(house.getConfig("RewardPointNonHouseMultiplier"));
				if ( nonHouseUnitMod > 0 )
					unitTotalRewardPointCost *= nonHouseUnitMod;
			}
			
			if (buyRareUnit)
				unitTotalRewardPointCost *= rareCost;
			
			if ( unitTotalRewardPointCost > rewardPointsAvailable ){
				CampaignMain.cm.toUser("Sorry. You need more reward points to buy that kind of unit.",Username,true);
				return;
			}    	    
			
			try {
				//Lets get us a pilot and a unit
				if ( new Boolean(house.getConfig("AllowPersonalPilotQueues")).booleanValue() && ( unitType == Unit.MEK || unitType == Unit.PROTOMEK) )
					newPilot = new SPilot("Vacant",99,99);
				else
					newPilot = player.getMyHouse().getNewPilot(unitType);

				newUnit = getUnitProduced(unitType,unitWeight,newPilot,factionstring,player.getMyHouse());
				player.addUnit(newUnit, true);
				CampaignMain.cm.toUser("You've bought a " + newUnit.getModelName() + " for " +unitTotalRewardPointCost + " reward points.",Username,true);
				player.addReward(-unitTotalRewardPointCost);
			} catch (Exception ex){
				CampaignMain.cm.toUser("An error has occured while trying to create your requested unit. Please contact an admin. Faction: "+factionstring +" Type: "+unitType+" Class: "+unitWeight,Username,true);
				MWServ.mwlog.errLog(ex);
				MWServ.mwlog.errLog("Error creating unit in "+this.getClass().getName());
			}
            break;
            
        case 3://repairs
            rewardPoints = Integer.parseInt(house.getConfig("RewardPointsForRepair"));
            
            if ( rewardPoints > player.getReward() ){
                CampaignMain.cm.toUser("You need more reward points to repair this unit (requires " + rewardPoints + " RP)", Username, true);
                return;
            }
            
            int unitID = Integer.parseInt(command.nextToken());
            SUnit unit = player.getUnit(unitID);
            
            //break out if the player doesn't have a unit with that id
            if (unit == null) {
                CampaignMain.cm.toUser("You don't have a unit with ID# " + unitID + ".", Username, true);
                return;
            }
            
            Entity entity = unit.getEntity();
            for (int x = 0; x < entity.locations(); x++) {
                entity.setArmor(entity.getOArmor(x),x);
                if ( entity.hasRearArmor(x) )
                    entity.setArmor(entity.getOArmor(x,true),x,true);
                entity.setInternal(entity.getOInternal(x),x);
                for (int y = 0; y < entity.getNumberOfCriticals(x); y++) {
                    CriticalSlot cs = entity.getCritical(x,y);

                    if ( cs == null )
                        continue;
                    
                    if ( cs.getType() == CriticalSlot.TYPE_EQUIPMENT ){
                        Mounted mounted = entity.getEquipment(cs.getIndex());
                        UnitUtils.repairEquipment(mounted,entity,x);
                    }// end CS type if
                    else{
                        if ( UnitUtils.isEngineCrit(cs) ){
                            UnitUtils.repairDamagedEngine(entity);
                        }
                        else{
                            if (entity instanceof Mech) {
                                //Fix both breached and damaged crits.
                                UnitUtils.fixCriticalSlot(cs,entity,true);
                                UnitUtils.fixCriticalSlot(cs,entity,false);
                            }
                            entity.setCritical(x,y,cs);
                        }
                    }//end CS type else

                }
            }
            
            //Fill up ammo.
            for (Mounted weap : entity.getAmmo())
                weap.setShotsLeft(((AmmoType)weap.getType()).getShots());

            CampaignMain.cm.toUser("Unit #" + unitID + " "+unit.getModelName()+" is now fully repaired.", Username, true);
            CampaignMain.cm.toUser("PL|UU|"+unit.getId()+"|"+unit.toString(true),Username,false);
            player.addReward(-rewardPoints);
            player.checkAndUpdateArmies(unit);
            player.setSave(true);
            break;
		}  
		
	}
	
	/**
	 * Build a unit. Derived from SUnitFactory.java's getUnitProduced()
	 * 
	 * @return the Mek Produced
	 */
	private SUnit getUnitProduced(int type_id, int weightClass, SPilot pilot, String faction, SHouse house) {
		
		SUnitFactory factory = new SUnitFactory();
		String unitSize = Unit.getWeightClassDesc(weightClass);
		factory.setFounder(faction);
		
		String Filename = "";
		
		//Use special RP-build fluff text for the unit
		String producer = "Reward Unit";
		
		if (new Boolean(house.getConfig("UseOnlyOneVehicleSize")).booleanValue() && type_id == Unit.VEHICLE)
			unitSize = Unit.getWeightClassDesc(CampaignMain.cm.getR().nextInt(4));
		
		Filename = BuildTable.getUnitFilename(faction,unitSize,type_id,BuildTable.REWARD);//build from rewards dir.
		SUnit cm = new SUnit(producer,Filename,weightClass);
				
		cm.setPilot(pilot);
		factory = null;  // clear this out of memory
		return cm;
	}
}