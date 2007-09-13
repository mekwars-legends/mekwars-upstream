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

package server.campaign;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.Vector;

import common.MegaMekPilotOption;
import common.Unit;
import common.campaign.pilot.skills.PilotSkill;
import common.util.UnitUtils;

import server.MWServ;
import server.campaign.pilot.SPilot;
import server.campaign.pilot.skills.PainResistanceSkill;
import server.campaign.pilot.skills.SPilotSkill;
import server.campaign.pilot.skills.WeaponSpecialistSkill;

import megamek.common.AmmoType;
import megamek.common.CriticalSlot;
import megamek.common.Entity;
import megamek.common.Mech;
import megamek.common.MechFileParser;
import megamek.common.MiscType;
import megamek.common.Mounted;
import megamek.common.Pilot;
import megamek.common.WeaponType;

/**
 * A class representing an MM.Net Entity
 * @author Helge Richter (McWizard)
 * 
 * Jun 10/04 - Dave Poole
 * added an overloaded constructor to allow creation of a new SUnit
 * with the same UnitID as an existing Mech to facilitate repodding
 */
@SuppressWarnings({"unchecked","serial"})
public final class SUnit extends Unit implements Serializable {
	
	//VARIABLES
	private Integer BV = 0;
	private Integer scrappableFor = new Integer(-1);
	
	private long passesMaintainanceUntil = 0;
	private boolean pilotIsRepairing = false;
	
	private Entity unitEntity = null;
    private int lastCombatPilot = -1;
	
    private int dbId = 0;
    
	//CONSTRUCTOR
	/**
	 * For Serialization.
	 */
	public SUnit(){
		super();
	}
	
	/**
	 * Construct a new unit.
	 * 
	 * @param p flavour string (es: Built by Kurita on An-Ting)
	 * @param filename to read this entity from
	 */
	public SUnit(String p, String Filename, int weightclass) {
		super();
        int gunnery = 4;
        int piloting = 5;
        
        SHouse house = CampaignMain.cm.getHouseFromPartialString(p,null);
        
		setUnitFilename(Filename);
		Entity ent = loadMech(getUnitFilename());
		this.setEntity(ent);

        this.setWeightclass(weightclass); //default weight class.
        if (ent != null) {
            unitEntity = ent;
            init();
        }

        if ( house != null )
            setPilot(house.getNewPilot(this.getType()));
        else
            setPilot(new SPilot(SPilot.getRandomPilotName(CampaignMain.cm.getR()),gunnery,piloting));
        
        setProducer(p);
		setId(CampaignMain.cm.getAndUpdateCurrentUnitID());
	}
	
	/**
	 * Constructs a new Unit with the id for an existing unit (repod)
	 * 
	 * @param p - flavour string (es: Built by Kurita on An-Ting)
	 * @param Filename - filename to read this entity from
	 * @param weightclass - int defining weightclass
	 * @param replaceId - unitID to assign a new SUnit
	 */
	public SUnit(int replaceId, String p, String Filename) {
		super();
		setUnitFilename(Filename);
		Entity ent = loadMech(getUnitFilename());
		this.setEntity(ent);
		new SPilot("Vacant",99,99);//only used for repods. A real pilot is transferred in later.
        setId(replaceId);
        setProducer(p);
		unitEntity = ent;
		init();
	}
	
	//STATIC METHODS
    /**
	 * Method which checks a unit for illegal ammo and
	 * replaces it with default ammo loads. useful for
	 * removing faction banned ammo from salvage.
	 * 
	 * Note that this is primarily designed to strip L2
	 * ammo from L2 units (eg - precision AC) and replace
	 * it with normal ammo. L3 ammos may lead to some
	 * oddities and should be banned or allowed server
	 * wide rather than on a house-by-house basis.
	 * 
	 * @param u - unit to check
	 * @param h - SHouse unit is joining
	 */
	public static void checkAmmoForUnit(SUnit u, SHouse h) {
		
		Entity en = u.getEntity();
		
		boolean wasChanged = false;
		
		for ( Mounted mAmmo : en.getAmmo() ){
		    
		    AmmoType at = (AmmoType)mAmmo.getType();
		    String munition = Long.toString(at.getMunitionType());
		    
		    if (at.getAmmoType() == AmmoType.T_ATM)
    			continue;
    			
    		if (at.getAmmoType() == AmmoType.T_AC_LBX)
    			continue;
		    
            if (at.getAmmoType() == AmmoType.T_SRM_STREAK)
                continue;
            
            if (at.getAmmoType() == AmmoType.T_LRM_STREAK)
                continue;
            
            if (at.getAmmoType() == AmmoType.M_STANDARD)
                continue;
            
            if (CampaignMain.cm.getData().getServerBannedAmmo().containsKey(munition) || h.getBannedAmmo().containsKey(munition) ) {
		    	
		    	Vector types = AmmoType.getMunitionsFor(at.getAmmoType());
		    	Enumeration allTypes = types.elements();
		    	
		    	boolean defaultFound = false;
		    	while (allTypes.hasMoreElements() && !defaultFound) {
		    		AmmoType currType = (AmmoType)allTypes.nextElement();
		    		
		    		if (currType.getTechLevel() <= en.getTechLevel() && currType.getMunitionType() == AmmoType.M_STANDARD && currType.getRackSize() == at.getRackSize()) {		
		    			mAmmo.changeAmmoType(currType);
		    			mAmmo.setShotsLeft(at.getShots());
		    			defaultFound = true;
		    			wasChanged = true;
		    		}
		    	}//end while	
		    }//end if(is banned)
		
		}
		if (wasChanged)
			u.setEntity(en);
	}
	
	/**
	 * Method which determines whether or not a given unit may be
	 * sold on the black market. Any "false" return prevents house
	 * listings as well as player sales.
	 */
	public static boolean mayBeSoldOnMarket(SUnit u) {
		
		if (u.getType() == Unit.BATTLEARMOR && !CampaignMain.cm.getBooleanConfig("BAMayBeSoldOnBM"))
			return false;
		
		else if (u.getType() == Unit.PROTOMEK && !CampaignMain.cm.getBooleanConfig("ProtosMayBeSoldOnBM"))
			return false;
		
		else if (u.getType() == Unit.INFANTRY && !CampaignMain.cm.getBooleanConfig("InfantryMayBeSoldOnBM"))
			return false;
		
		else if (u.getType() == Unit.VEHICLE && !CampaignMain.cm.getBooleanConfig("VehsMayBeSoldOnBM"))
			return false;
		
		else if ((u.getType() == Unit.MEK || u.getType() == Unit.QUAD) && !CampaignMain.cm.getBooleanConfig("MeksMayBeSoldOnBM"))
			return false;
		
		return true;
	}
	
	/**
	 * Return the number of techs/bays required for a unit of given size/type.
	 */
	public static int getHangarSpaceRequired(int typeid, int weightclass, int baymod, String model) {
		
		if (typeid == Unit.PROTOMEK)
		    return 0;
		
		if (typeid == Unit.INFANTRY && CampaignMain.cm.getBooleanConfig("FootInfTakeNoBays")) {
			
			//check types
			boolean isFoot = model.startsWith("Foot");
			boolean isAMFoot = model.startsWith("Anti-Mech Foot");
			
			if (isFoot || isAMFoot)
				return 0;
		}
		
		int result = 1;
		String techAmount = "TechsFor"+Unit.getWeightClassDesc(weightclass)+Unit.getTypeClassDesc(typeid);
		result = CampaignMain.cm.getIntegerConfig(techAmount);
        
        if (!CampaignMain.cm.isUsingAdvanceRepair())//Apply Pilot Mods (Astech skill)
    		result += baymod;
		 
		//no negative techs
		if (result < 0)
			result = 0;
		
		return result;
	}
	
	/**
	 * Pass-through method that gets the number of bays/techs required for a given unit
	 * by drawing its characteristics and feeding them to getHangarSpaceRequired(int,int,int,String).
	 */
	public static int getHangarSpaceRequired(SUnit u) {
		return SUnit.getHangarSpaceRequired(u.getType(),u.getWeightclass(),u.getPilot().getBayModifier(),u.getModelName());
	}
	
	/**
	 * Simple static method that access configs and returns a
	 * unit's influence on map size. Called by ShortOperation
	 * when changing status from Waiting -> In_Progress.
	 *
	 * @return - configured map weighting
	 */
	public static int getMapSizeModification(SUnit u) {
		if (u.getType() == Unit.VEHICLE)
			return CampaignMain.cm.getIntegerConfig("VehicleMapSizeFactor");
		if (u.getType() == Unit.INFANTRY)
			return CampaignMain.cm.getIntegerConfig("InfantryMapSizeFactor");
		if (u.getType() == Unit.MEK)
			return CampaignMain.cm.getIntegerConfig("MekMapSizeFactor");
		if (u.getType() == Unit.BATTLEARMOR ) 
			return CampaignMain.cm.getIntegerConfig("BattleArmorMapSizeFactor");
		if (u.getType() == Unit.PROTOMEK )
			return CampaignMain.cm.getIntegerConfig("ProtoMekMapSizeFactor");
		return 0;//no known type? return 0.
	}
		
	/*
	 * AR-related statics.
	 */
	public static double getArmorCost(Entity unit){
        double cost = 0.0;
		
        if ( CampaignMain.cm.getBooleanConfig("UsePartsRepair") )
			return 0;

        String armorCost = "CostPoint"+UnitUtils.getArmorShortName(unit);
        cost = CampaignMain.cm.getDoubleConfig(armorCost);
		
        
        return cost;
    }
    
    public static double getStructureCost(Entity unit){
        double cost = 0.0;
        
        if ( CampaignMain.cm.getBooleanConfig("UsePartsRepair") )
			return 0;

        String armorCost = "CostPoint"+UnitUtils.getInternalShortName(unit)+"IS";
        cost = CampaignMain.cm.getDoubleConfig(armorCost);

        return cost;
    }
    
    public static double getCritCost(Entity unit,CriticalSlot crit){
        
    	double cost = 0.0;
        if ( CampaignMain.cm.getBooleanConfig("UsePartsRepair") )
			return 0;

        if (crit.isBreached() && !crit.isDamaged())
            return 0;
        
        //else
        if ( UnitUtils.isEngineCrit(crit) )
        	cost = CampaignMain.cm.getDoubleConfig("EngineCritRepairCost");
        else if ( crit.getType() == CriticalSlot.TYPE_SYSTEM)
        	if ( crit.isMissing() )
        		cost = CampaignMain.cm.getDoubleConfig("SystemCritReplaceCost");
        	else
        		cost = CampaignMain.cm.getDoubleConfig("SystemCritRepairCost");
        else{
        	Mounted mounted = unit.getEquipment(crit.getIndex());

        	if ( mounted.getType() instanceof WeaponType ){
        		WeaponType weapon = (WeaponType)mounted.getType();
        		if ( weapon.hasFlag(WeaponType.F_ENERGY) )
        			if ( crit.isMissing() )
        				cost = CampaignMain.cm.getDoubleConfig("EnergyWeaponCritReplaceCost");
        			else
        				cost = CampaignMain.cm.getDoubleConfig("EnergyWeaponCritRepairCost");
        		else if ( weapon.hasFlag(WeaponType.F_BALLISTIC) )
        			if ( crit.isMissing() )
        				cost = CampaignMain.cm.getDoubleConfig("BallisticCritReplaceCost");
        			else
        				cost = CampaignMain.cm.getDoubleConfig("BallisticCritRepairCost");
        		else if ( weapon.hasFlag(WeaponType.F_MISSILE) )
        			if ( crit.isMissing() )
        				cost = CampaignMain.cm.getDoubleConfig("MissileCritReplaceCost");
        			else
        				cost = CampaignMain.cm.getDoubleConfig("MissileCritRepairCost");
        		else//use the misc eq costs.
        			if ( crit.isMissing() )
        				cost = CampaignMain.cm.getDoubleConfig("EquipmentCritReplaceCost");
        			else
        				cost = CampaignMain.cm.getDoubleConfig("EquipmentCritRepairCost");
        	}
        	else//use the misc eq costs.
        		if ( crit.isMissing() )
        			cost = CampaignMain.cm.getDoubleConfig("EquipmentCritReplaceCost");
        		else
        			cost = CampaignMain.cm.getDoubleConfig("EquipmentCritRepairCost");
        }

        cost = Math.max(cost,1);
        return cost;
    }
	
	//METHODS
	/**
	 * @return the Serialized Version of this entity
	 */
	public String toString(boolean toPlayer) {
		
		//Recalculate the unit's bv. There is a reason we are sending new data to the player
		if (toPlayer) {
			setBV(0);
			getBV();
		}

		StringBuilder result = new StringBuilder();
		result.append("CM$");
		if ( toPlayer )
			result.append(getEntity().getChassis() + " " +  getEntity().getModel());
		else
			result.append(getUnitFilename());
		result.append("$");
		result.append(getPosId());
		result.append("$");
		result.append(getStatus());
		result.append("$");
		result.append(getProducer());
		result.append("$");
		result.append(((SPilot) getPilot()).toFileFormat("#", toPlayer));
		result.append("$"); 
		if (toPlayer)
		{
			LinkedList mmoptions = getPilot().getMegamekOptions();
			result.append(mmoptions.size());
			result.append("$");
			Iterator i = mmoptions.iterator();
			while (i.hasNext()) {
				MegaMekPilotOption mmo = (MegaMekPilotOption) i.next();
				result.append(mmo.getMmname());
				result.append("$");
				result.append(mmo.isValue());
				result.append("$");
			}
			result.append(this.getType());
			result.append("$");
			result.append(this.getBV());
			result.append("$");
		}
		result.append(getWeightclass());
		result.append("$");
		result.append(getId());
		result.append("$");
		
		//error units don't need the rest of this data sent.
		if ( this.getModelName().equals("OMG-UR-FD"))
			return result.toString();
		
		if ( this.getEntity() instanceof Mech)
		{
			unitEntity = this.getEntity();
			result.append(((Mech)unitEntity).isAutoEject());
			result.append("$");
		}
		ArrayList<Mounted> en_Ammo = unitEntity.getAmmo();
		result.append(en_Ammo.size());
		result.append("$");
		for (Mounted mAmmo : en_Ammo ){

            boolean hotloaded = mAmmo.isHotLoaded();
            if ( !CampaignMain.cm.getMegaMekClient().game.getOptions().booleanOption("maxtech_hotload") )
                hotloaded = false;

			AmmoType at = (AmmoType)mAmmo.getType();
			result.append(at.getAmmoType());
			result.append("$");
			result.append(at.getInternalName());
			result.append("$");
			result.append(mAmmo.getShotsLeft());
			result.append("$");
            result.append(hotloaded);
            result.append("$");
		}
		
        ArrayList<Mounted> en_Weapon = unitEntity.getWeaponList();
		result.append(CampaignMain.cm.getMachineGunCount(en_Weapon));
		result.append("$");
		int location = 0;
		for (Mounted mWeapon : en_Weapon ){
			WeaponType weapon = (WeaponType)mWeapon.getType();
			if (weapon.hasFlag(WeaponType.F_MG) ){
				result.append(location);
				result.append("$");
				result.append(mWeapon.isRapidfire());
				result.append("$");
			}
			location++;
		}
		result.append(unitEntity.hasSpotlight());
		result.append("$");
		result.append(unitEntity.isUsingSpotlight());
		result.append("$");
		if (toPlayer){
			result.append(this.getPilotSkillBV());
			result.append("$");
		}
        if ( CampaignMain.cm.getData().getBannedTargetingSystems().containsKey(this.getEntity().getTargSysType()) ){
            result.append(MiscType.T_TARGSYS_STANDARD);
            this.getEntity().setTargSysType(MiscType.T_TARGSYS_STANDARD);
        }
        else
            result.append(this.getEntity().getTargSysType());
		result.append("$");
		result.append(this.getScrappableFor());
		result.append("$");
		if ( CampaignMain.cm.isUsingAdvanceRepair() ){
			//do not need to save ammo twice so set sendAmmo to False
			result.append(UnitUtils.unitBattleDamage(this.getEntity(),false));
		}
		else
			result.append("%%-%%-%%-");
		result.append("$");
		
		if (toPlayer){
			result.append(getPilotIsReparing());
			result.append("$");
		}
        if ( !toPlayer ){
    		result.append(this.getLastCombatPilot());
            result.append("$");
        }
        
        result.append(this.getCurrentRepairCost());
        result.append("$");
        result.append(this.getLifeTimeRepairCost());
        result.append("$");
        if(CampaignMain.cm.isUsingMySQL() && ! toPlayer) {
        	result.append(this.getDBId());
        	result.append("$");
        }
		return result.toString();
	}
	
	public void toDB() {
		PreparedStatement ps;
		StringBuffer sql = new StringBuffer();
		Entity ent = getEntity();
		
		try {
				if(getDBId()==0) {
				// Unit's not in there - insert it
				sql.setLength(0);
				sql.append("INSERT into units set MWID=?, uFileName=?, uPosID=?, uStatus=?, uProducer=?, uWeightClass=?, uAutoEject=?, uHasSpotlight=?, uIsUsingSpotlight=?, uTargetSystem=?, uScrappableFor=?, uBattleDamage=?, uLastCombatPilot=?, uCurrentRepairCost=?, uLifetimeRepairCost=?, uType=?");
				ps=CampaignMain.cm.MySQL.getPreparedStatement(sql.toString(), PreparedStatement.RETURN_GENERATED_KEYS);
				ps.setInt(1, getId());
				ps.setString(2, getUnitFilename());
				ps.setInt(3, getPosId());
				ps.setInt(4, getStatus());
				ps.setString(5, getProducer());
				ps.setInt(6, getWeightclass());
				if(ent instanceof Mech)
				  ps.setString(7, Boolean.toString(((Mech)ent).isAutoEject()));
				else
					ps.setString(7, "false");
				ps.setString(8, Boolean.toString(ent.hasSpotlight()));
				ps.setString(9, Boolean.toString(ent.isUsingSpotlight()));
				if (CampaignMain.cm.getData().getBannedTargetingSystems().containsKey(getEntity().getTargSysType())) {
					ps.setInt(10, MiscType.T_TARGSYS_STANDARD);
					getEntity().setTargSysType(MiscType.T_TARGSYS_STANDARD);
				} else {
					ps.setInt(10, ent.getTargSysType());
				}
				ps.setInt(11, getScrappableFor());
				if(CampaignMain.cm.isUsingAdvanceRepair())
					ps.setString(12, UnitUtils.unitBattleDamage(getEntity(), false));
				else
					ps.setString(12, "%%-%%-%%-");
				ps.setInt(13, getLastCombatPilot());
				ps.setInt(14, getCurrentRepairCost());
				ps.setInt(15, getLifeTimeRepairCost());
				ps.setInt(16, getType());
				ps.executeUpdate();
				setDBId(getId());
			} else {
				// Unit's already there - update it
				sql.setLength(0);
				sql.append("UPDATE units set uFileName=?, uPosID=?, uStatus=?, uProducer=?, uWeightClass=?, uAutoEject=?, uHasSpotlight=?, uIsUsingSpotlight=?, uTargetSystem=?, uScrappableFor=?, uBattleDamage=?, uLastCombatPilot=?, uCurrentRepairCost=?, uLifetimeRepairCost=?, uType = ? where MWID=?");
				ps=CampaignMain.cm.MySQL.getPreparedStatement(sql.toString());
				ps.setString(1, getUnitFilename());
				ps.setInt(2, getPosId());
				ps.setInt(3, getStatus());
				ps.setString(4, getProducer());
				ps.setInt(5, getWeightclass());
				if(ent instanceof Mech)
				  ps.setString(6, Boolean.toString(((Mech)ent).isAutoEject()));
				else
					ps.setString(6, "false");
				ps.setString(7, Boolean.toString(ent.hasSpotlight()));
				ps.setString(8, Boolean.toString(ent.isUsingSpotlight()));
				if (CampaignMain.cm.getData().getBannedTargetingSystems().containsKey(getEntity().getTargSysType())) {
					ps.setInt(9, MiscType.T_TARGSYS_STANDARD);
					getEntity().setTargSysType(MiscType.T_TARGSYS_STANDARD);
				} else {
					ps.setInt(9, ent.getTargSysType());
				}
				ps.setInt(10, getScrappableFor());
				if(CampaignMain.cm.isUsingAdvanceRepair())
					ps.setString(11, UnitUtils.unitBattleDamage(getEntity(), false));
				else
					ps.setString(11, "%%-%%-%%-");
				ps.setInt(12, getLastCombatPilot());
				ps.setInt(13, getCurrentRepairCost());
				ps.setInt(14, getLifeTimeRepairCost());
				ps.setInt(15, getType());
				ps.setInt(16, getId());
				ps.executeUpdate();
			}
			// Now do Machine Guns
			ps.executeUpdate("DELETE from unit_mgs WHERE unitID = " + getId());
			ArrayList<Mounted> en_Weapon = ent.getWeaponList();
			int location = 0;
			for (Mounted mWeapon : en_Weapon) {
				WeaponType weapon = (WeaponType)mWeapon.getType();
				if (weapon.hasFlag(WeaponType.F_MG)) {
					sql.setLength(0);
					sql.append("INSERT into unit_mgs set unitID=?, mgLocation=?, mgRapidFire=?");
					ps = CampaignMain.cm.MySQL.getPreparedStatement(sql.toString());
					ps.setInt(1, getId());
					ps.setInt(2, location);
					ps.setString(3, Boolean.toString(mWeapon.isRapidfire()));
					ps.executeUpdate();
				}
				location++;
			}
			// Do Ammo
			ps.executeUpdate("DELETE from unit_ammo WHERE unitID = " + getId());
			
			ArrayList<Mounted> en_Ammo = ent.getAmmo();
			int AmmoLoc = 0;
			for (Mounted mAmmo : en_Ammo ) {
				boolean hotloaded = mAmmo.isHotLoaded();
				if (!CampaignMain.cm.getMegaMekClient().game.getOptions().booleanOption("maxtech_hotload"))
					hotloaded = false;
				AmmoType at = (AmmoType)mAmmo.getType();
				sql.setLength(0);
				sql.append("INSERT into unit_ammo set unitID = ?, ammoLocation = ?, ammoHotLoaded=?, ammoType=?, ammoInternalName=?, ammoShotsLeft=?");
				ps = CampaignMain.cm.MySQL.getPreparedStatement(sql.toString());
				ps.setInt(1, getId());
				ps.setInt(2, AmmoLoc);
				ps.setString(3, Boolean.toString(hotloaded));
				ps.setInt(4, at.getAmmoType());
				ps.setString(5, at.getInternalName());
				ps.setInt(6, mAmmo.getShotsLeft());
				ps.executeUpdate();
				AmmoLoc++;
			}
			ps.close();
//			 Save the pilot
			if (getPilot().getGunnery()!=99){
				((SPilot)getPilot()).toDB(getType(), getWeightclass());
			}
		} catch (SQLException e){
			MWServ.mwlog.dbLog("SQL Exception in SaveUnit.toDB: " + e.getMessage());
		}
	}
	
	/**
	 * Reads a Entity from a String
	 * @param s A string to read from
	 * @return the remaining String
	 */
	public String fromString(String s) {
		
		try{
			s = s.substring(3);
			
			StringTokenizer ST = new StringTokenizer(s,"$");
			setUnitFilename((String)ST.nextElement());
			
			setPosId(Integer.parseInt(ST.nextToken()));
			int newstate = Integer.parseInt(ST.nextToken());//status read-in
			setProducer((String)ST.nextElement());
			SPilot p = new SPilot();
			p.fromFileFormat(ST.nextToken(), "#");
			if (ST.hasMoreElements())
				setWeightclass(Integer.parseInt(ST.nextToken()));
			else
				setWeightclass(Unit.LIGHT);
			
			if (ST.hasMoreElements()) {
				setId(Integer.parseInt(ST.nextToken()));
				if (CampaignMain.cm.getCurrentUnitID() <= getId())
					CampaignMain.cm.setCurrentUnitID(getId() + 1);
			}
			else {
				setId(CampaignMain.cm.getAndUpdateCurrentUnitID());
			}
			if (this.getId() == 0 ) {
				setId(CampaignMain.cm.getAndUpdateCurrentUnitID());
			}
			/*
			 * Handle unit status. FOR_SALE and AdvanceRepair both require special handling. If the
			 * unit is FOR_SALE, make sure a listing still exists. If not, the server probably crashed
			 * and the unit should be returned to normal.
			 */
			if (newstate == STATUS_FORSALE && CampaignMain.cm.getMarket().getListingForUnit(this.getId()) == null)
				setStatus(STATUS_OK);
			else if (CampaignMain.cm.isUsingAdvanceRepair())
				setStatus(STATUS_OK);
			else
				setStatus(newstate);
			
			unitEntity = loadMech(getUnitFilename());
			this.setEntity(unitEntity);
			this.setPilot(p);
			this.init();
			
			//if its an OMG unit it won't have Ammo
			if ( this.getModelName().equals("OMG-UR-FD") )
				return s;
			
			if ( ST.hasMoreElements() && unitEntity instanceof Mech)
				((Mech)unitEntity).setAutoEject(Boolean.parseBoolean(ST.nextToken()));
			String defaultField ="";
			if ( ST.hasMoreElements()){
				Entity en = getEntity();
				int maxCrits = Integer.parseInt(ST.nextToken());
				defaultField = ST.nextToken();
				ArrayList<Mounted> e = en.getAmmo();
				for ( int count = 0; count < maxCrits; count++ ){
					int weaponType = Integer.parseInt(defaultField);
					String ammoName = ST.nextToken();
					int shots = Integer.parseInt(ST.nextToken());
                    boolean hotloaded = false;
                    //needed to make backwards compatibility better.
                    try{
                    	defaultField = ST.nextToken();
                        hotloaded = Boolean.parseBoolean(defaultField);
                        defaultField = ST.nextToken();
                    }catch(Exception ex){
                    	hotloaded = false;
                    }
                        
                    if ( !CampaignMain.cm.getMegaMekClient().game.getOptions().booleanOption("maxtech_hotload") )
                        hotloaded = false;
                    
                    Mounted mWeapon = e.get(count);
					
					AmmoType at = this.getEntityAmmo(weaponType,ammoName);
					if ( at == null ) //saved ammo no longer found use Entity loaded --Torren.
						continue;
					String munition = Long.toString(at.getMunitionType());
					
					//check banned ammo
					if (CampaignMain.cm.getData().getServerBannedAmmo().get(munition) != null)
						continue;
					
					mWeapon.changeAmmoType(at);
					mWeapon.setShotsLeft(shots);
                    mWeapon.setHotLoad(hotloaded);
				}
				setEntity(en);
			}
			if ( ST.hasMoreElements()){
				int maxMachineGuns = Integer.parseInt(defaultField);
				int currentLocation = 0;
				Entity en = this.getEntity();
				ArrayList<Mounted> enWeapons = en.getWeaponList();
				for ( int count = 0; count < maxMachineGuns; count++ ){
					int location = Integer.parseInt(ST.nextToken());
					boolean selection = Boolean.parseBoolean(ST.nextToken());
					for ( Mounted mWeapon : enWeapons ){
						if ( currentLocation == location ){
							mWeapon.setRapidfire(selection);
							currentLocation++;
							break;
						}
						currentLocation++;
					}
				}
				setEntity(en);
			}
			if ( ST.hasMoreElements())
				unitEntity.setSpotlight(Boolean.parseBoolean(ST.nextToken()));
			if ( ST.hasMoreElements())
				unitEntity.setSpotlightState(Boolean.parseBoolean(ST.nextToken()));
			if ( ST.hasMoreElements()){//if allow level 3 targeting is enabled for all units then apply the saved one else use the entitie default.
				if ( CampaignMain.cm.getMegaMekClient().game.getOptions().booleanOption("allow_level_3_targsys") ){
                    int targetingType = Integer.parseInt(ST.nextToken());
                    //check if the targeting type has become banned. if so then set the system to standard.
                    if ( CampaignMain.cm.getData().getBannedTargetingSystems().containsKey(targetingType)
                    		|| this.getEntity().hasC3()
                    		|| this.getEntity().hasC3i()
                    		|| UnitUtils.hasTargettingComputer(this.getEntity()) )
                        unitEntity.setTargSysType(MiscType.T_TARGSYS_STANDARD);
                    else
                        unitEntity.setTargSysType(targetingType);

                }
				else
					ST.nextElement();
			}
			if (ST.hasMoreElements())
				this.setScrappableFor(new Integer(ST.nextToken()));
			if ( ST.hasMoreTokens() ){
				if ( CampaignMain.cm.isUsingAdvanceRepair() )
					UnitUtils.applyBattleDamage(unitEntity,ST.nextToken(),(CampaignMain.cm.getRTT() != null && CampaignMain.cm.getRTT().unitRepairTimes(this.getId()) != null) );
				else
					ST.nextToken();
			}
            if ( ST.hasMoreTokens() )
                this.setLastCombatPilot(Integer.parseInt(ST.nextToken()));
            
            if ( ST.hasMoreTokens() )
            	this.setRepairCosts(Integer.parseInt(ST.nextToken()),Integer.parseInt(ST.nextToken()));
            if ( CampaignMain.cm.isUsingMySQL() && ST.hasMoreTokens())
            	this.setDBId(Integer.parseInt(ST.nextToken()));
            
			return s;
		}
		catch(Exception ex){
			MWServ.mwlog.errLog(ex);
			MWServ.mwlog.errLog("Unable to Load SUnit: "+s);
			//the unit should still be good return what did get set
			return s;
		}
	}
	
	public void fromDB(int unitID) {
		try {
			ResultSet rs;
			Statement stmt = CampaignMain.cm.MySQL.getStatement();
			
			rs = stmt.executeQuery("SELECT * from units WHERE MWID = " + unitID);
			if(rs.next()) {
			setUnitFilename(rs.getString("uFileName"));
			setPosId(rs.getInt("uPosID"));
			int newstate = rs.getInt("uStatus");
			setProducer(rs.getString("uProducer"));
			setWeightclass(rs.getInt("uWeightClass"));
			setId(unitID);
			setDBId(unitID);
			if(CampaignMain.cm.getCurrentUnitID() <= getId())
				CampaignMain.cm.setCurrentUnitID(getId() + 1);
			if (getId()==0)
				setId(CampaignMain.cm.getAndUpdateCurrentUnitID());
			if (newstate == Unit.STATUS_FORSALE && CampaignMain.cm.getMarket().getListingForUnit(getId()) == null)
				setStatus(Unit.STATUS_OK);
			else if (CampaignMain.cm.isUsingAdvanceRepair())
				setStatus(Unit.STATUS_OK);
			else
				setStatus(newstate);
			setScrappableFor(rs.getInt("uScrappableFor"));
			setRepairCosts(rs.getInt("uCurrentRepairCost"), rs.getInt("uLifetimeRepairCost"));
			Entity unitEntity = loadMech(getUnitFilename());


			if(unitEntity instanceof Mech)
				((Mech)unitEntity).setAutoEject(Boolean.parseBoolean(rs.getString("uAutoEject")));
			unitEntity.setSpotlight(Boolean.parseBoolean(rs.getString("uHasSpotlight")));
			
			unitEntity.setSpotlightState(Boolean.parseBoolean(rs.getString("uIsUsingSpotlight")));
			
			if(CampaignMain.cm.isUsingAdvanceRepair())
				UnitUtils.applyBattleDamage(unitEntity, rs.getString("uBattleDamage"), (CampaignMain.cm.getRTT() != null & CampaignMain.cm.getRTT().unitRepairTimes(getId())!=null));
			
			if (CampaignMain.cm.getMegaMekClient().game.getOptions().booleanOption("allow_level_3_targsys")) {
				int targetingType = rs.getInt("uTargetSystem");
				if (CampaignMain.cm.getData().getBannedTargetingSystems().containsKey(targetingType) || unitEntity.hasC3() || unitEntity.hasC3i() || UnitUtils.hasTargettingComputer(unitEntity))
					unitEntity.setTargSysType(MiscType.T_TARGSYS_STANDARD);
				else
					unitEntity.setTargSysType(targetingType);
			}

			setEntity(unitEntity);
			SPilot p = new SPilot();
			p = CampaignMain.cm.MySQL.loadUnitPilot(unitID);
			setPilot(p);
			setLastCombatPilot(p.getPilotId());
			init();
			
			
			// Load ammo			
			rs = stmt.executeQuery("SELECT * from unit_ammo WHERE unitID = " + unitID + " ORDER BY ammoLocation");

			while(rs.next()) {
				int weaponType = rs.getInt("ammoType");
				String ammoName = rs.getString("ammoInternalName");
				int shots = rs.getInt("ammoShotsLeft");
				int AmmoLoc = rs.getInt("ammoLocation");
				boolean hotloaded = Boolean.parseBoolean(rs.getString("ammoHotLoaded"));
				if(!CampaignMain.cm.getMegaMekClient().game.getOptions().booleanOption("maxtech_hotload"))
					hotloaded = false;

				AmmoType at = getEntityAmmo(weaponType, ammoName);
				String munition = Long.toString(at.getMunitionType());
				
				if (CampaignMain.cm.getData().getServerBannedAmmo().get(munition) != null)
					continue;				
				try {
					unitEntity.getAmmo().get(AmmoLoc).changeAmmoType(at);
					unitEntity.getAmmo().get(AmmoLoc).setShotsLeft(shots);
					unitEntity.getAmmo().get(AmmoLoc).setHotLoad(hotloaded);
				} catch (Exception ex) {
					MWServ.mwlog.dbLog("Exception: " + ex.toString());
					MWServ.mwlog.dbLog(ex.getStackTrace().toString());
				}
			}

			setEntity(unitEntity);
					
			// Load MGs
			ArrayList<Mounted> enWeapons = unitEntity.getWeaponList();
			rs = stmt.executeQuery("SELECT * from unit_mgs WHERE unitID = " + unitID + " ORDER BY mgLocation");
			while(rs.next()) {
				int location = rs.getInt("mgLocation");
				int currentLocation = 0;
				for (Mounted mWeapon : enWeapons) {
					if (currentLocation == location ) {
						mWeapon.setRapidfire(Boolean.parseBoolean(rs.getString("mgRapidFire")));
						currentLocation++;
						break;
					}
					currentLocation++;
				}
			}
			
				setEntity(unitEntity);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			MWServ.mwlog.dbLog("SQL Error in SUnit.fromDB: " + e.getMessage());
		}
	}
	
	/**
	 * @return a description of the entity including pilot
	 */
	public String getDescription(boolean showLink) {
		String status = "";
		
		if (CampaignMain.cm.isUsingAdvanceRepair()) {
			if ( UnitUtils.hasCriticalDamage(this.getEntity()) )
				status = "Is Critically Damaged";
			else if ( UnitUtils.hasArmorDamage(this.getEntity()) )
				status = "Has Minor Armor Damage";
			else if ( UnitUtils.isRepairing(this.getEntity()) )
				status = "Is Currently Under Going Repairs";
			else
				status = "Is Fully Functional";
		} else {
			if (getStatus() == Unit.STATUS_UNMAINTAINED)
				status = "Unmaintained"+" (" + getMaintainanceLevel() + "%)";
			else 
				status = "Maintained"+ " (" + getMaintainanceLevel() + "%)";
		}
		
		String idToShow = "";
		if (showLink)
			idToShow = "<a href=\"MEKWARS/c sth#u#" + this.getId() + "\">#" + this.getId() + "</a>";
		else
			idToShow = "#" + this.getId();
		
		if (this.getType() == Unit.MEK || this.getType() == Unit.VEHICLE)
			return idToShow + " " + getModelName() + " (" + this.getPilot().getGunnery() + "/" + this.getPilot().getPiloting() + ") [" + getPilot().getExperience() + " EXP " + this.getPilot().getSkillString(false)+ "] Kills: " +this.getPilot().getKills()+" "+ this.getProducer() + ". BV: " + this.getBV() + " " + status ;
		
		//else
		return idToShow + " " + getModelName() + " (" + this.getPilot().getGunnery() + ") [" + getPilot().getExperience() + " EXP " + this.getPilot().getSkillString(false)+ "] Kills: " +this.getPilot().getKills()+" "+ this.getProducer() + ". BV: " + this.getBV() + " " + status ;
	}
	
	/**
	 *
	 * @return a smaller description
	 */
	public String getSmallDescription() {
		String result;
		if (this.getType() == Unit.MEK || this.getType() == Unit.VEHICLE)
			result = getModelName() + " [" + this.getPilot().getGunnery()+"/"+this.getPilot().getPiloting();
		else
			result = getModelName() + " [" + this.getPilot().getGunnery();
		
		if (!getPilot().getSkillString(true).equals(" "))
			result += getPilot().getSkillString(true);
		result += "]";
		return result;
	}
	
		
	/**
	 * Returns the Modelname for this Unit
	 * @return the Modelname
	 */
	public String getModelName() {
		if (checkModelName() == null){
			unitEntity = this.loadMech(getUnitFilename());
			this.init();
		}
		return checkModelName();
	}
	
	public String getVerboseModelName()
	{
		//Includes Pilot Stats in ModelName
		if ( this.getType() == Unit.MEK || this.getType() == Unit.VEHICLE )
			return getModelName() + " (" + this.getPilot().getGunnery() + "/" + this.getPilot().getPiloting() +")";
		return getModelName() + " (" + this.getPilot().getGunnery() +")";
	}
	
	/**
	 * @return the BV of this entity including all modifications
	 */
	public int calcBV(boolean C3Network) {
		
		//get a base BV from MegaMek
		int calcedBV = this.getEntity().calculateBattleValue(C3Network,false);
		
		//Boost BV of super-fast tanks if the "FastHoverBVMod" is a positive number.
		int FastHoverBVMod = CampaignMain.cm.getIntegerConfig("FastHoverBVMod");
		if (FastHoverBVMod > 0 && this.getType() == Unit.VEHICLE &&
			this.getEntity().getMovementMode() == megamek.common.IEntityMovementMode.HOVER) {
			if (this.getEntity().getWalkMP() >= 8)
				calcedBV += FastHoverBVMod;
		}
	
		//Increase elite BV's by 5% if the "ElitePilotsBVMod" is enabled.
		if (CampaignMain.cm.getBooleanConfig("ElitePilotsBVMod")){
			if (getPilot().getGunnery() < 3)
				calcedBV = (int)Math.round(calcedBV * 1.05);
			else if (getPilot().getPiloting() < 3)
				calcedBV = (int)Math.round(calcedBV * 1.05);
		}
		
		//Increase BV if the pilot has MaxTech/MechWarrior skills.
		calcedBV += getPilotSkillBV();
		
		return calcedBV;
	}
	
	@Override
	public boolean equals(Object o) {
		
		SUnit m = null;
		try {
			m = (SUnit)o;
		} catch (ClassCastException e) {
			return false;
		}
		
		if (m == null)
			return false;
		
		if (m.getId() == this.getId()
				&& m.getUnitFilename().equals(this.getUnitFilename())
				&& m.getPilot().getGunnery() == this.getPilot().getGunnery()
				&& m.getPilot().getPiloting() == this.getPilot().getPiloting()) {
			return true;
		}
		
		//else
		return false;
	}
	
	/**
	 * Sets the Pilot of this entity
	 * @param p A pilot
	 */
	public void setPilot(SPilot p) {
		
		//zero BV any time a new pilot is added
		setBV(0);
		
		if (p == null)
			return;
		
		//any time the pilot changes set the unit commander flag to false.
		Pilot mPilot = new Pilot(p.getName(), p.getGunnery(), p.getPiloting());
		Entity entity = this.getEntity();
		entity.setCrew(mPilot);
		this.setEntity(entity);
		
		if (p.getSkills().has(PilotSkill.WeaponSpecialistSkillID)){
			Iterator ski = p.getSkills().getSkillIterator();
			while ( ski.hasNext() ){
				SPilotSkill skill = (SPilotSkill)ski.next();
				if ( skill.getName().equals("Weapon Specialist")
						&& p.getWeapon().equals("Default")){
					//MWServ.mwlog.errLog("setPilot inside");
					p.getSkills().remove(skill);
					((WeaponSpecialistSkill)skill).assignWeapon(this.getEntity(),p);
					skill.addToPilot(p);
					skill.modifyPilot(p);
					break;
				}
			}
		}
		
		p.setUnitType(this.getType());
		super.setPilot(p);
		if(CampaignMain.cm.isUsingMySQL() && !p.getName().equalsIgnoreCase("Vacant"))
			CampaignMain.cm.MySQL.linkPilotToUnit(p.getDBId(), this.getDBId());
		}
	
	public void init() {
		
		setType(Unit.getEntityType(this.getEntity()));
		
		if (this.getType() == Unit.MEK || this.getType() == Unit.VEHICLE)
			setWeightclass(getEntityWeight(this.getEntity()));
		
		//Set Modelname
		if ( getType() == Unit.PROTOMEK || getType() == Unit.BATTLEARMOR 
				|| getType() == Unit.INFANTRY || getType() == Unit.VEHICLE
				|| this.getEntity().isOmni() )
			setModelname( unitEntity.getChassis() + " " +  unitEntity.getModel());
		else
			setModelname(unitEntity.getModel());
		this.getC3Type(unitEntity);
		
		if (this.getModelName().equals("OMG-UR-FD"))
			this.setProducer("Error loading unit. Tried to build from "+this.getUnitFilename());
		
		/*
		 //Set Weight
		  this.weight = m.getWeight();
		  */
	}
	
	/**
	 * Sets status to unmaintained. Factors out repetetive code checking maintainance
	 * status and decreasing as unit is moved to unmaintained. Called from both Player and 
	 * SetUnmaintainedCommand.
	 * 
	 * It would possible to bypass this code and set a unit as unmaintained without incurring
	 * any maintainance penalty w/ Unit.setStatus(STATUS_UNMAINTAINED).
	 * 
	 * @urgru 8/4/04
	 */
	public void setUnmaintainedStatus() {
		
		if ( CampaignMain.cm.isUsingAdvanceRepair() ){
			this.setStatus(STATUS_OK);
			return;
		}
		
		//load configurables
		int baseUnmaintained = CampaignMain.cm.getIntegerConfig("BaseUnmaintainedLevel");
		int unmaintPenalty = CampaignMain.cm.getIntegerConfig("UnmaintainedPenalty");
		
		//set the actual status
		this.setStatus(STATUS_UNMAINTAINED);
		
		/*
		 * now change the maintainance levels. if the unit is well maintained,
		 * drop it to the basevalue. otherwise, apply the standard penalty.
		 */
		if (getMaintainanceLevel() >= baseUnmaintained + unmaintPenalty)
			setMaintainanceLevel(baseUnmaintained);
		else
			addToMaintainanceLevel(-unmaintPenalty);
		
	}//end setUnmaintainedStatus()
	
	
	//GETTER AND SETTER
	public int getBV() {
		return this.getBV(false);
	}

	public int getBV(boolean C3Network) {
		
		int toReturn = 0;
		
		if (C3Network)
			toReturn = calcBV(C3Network);
		else if (BV <= 0) {
			toReturn = calcBV(false);
			BV = toReturn;
		}
		else
			toReturn = BV;
		
		//if the BV is negative, send a 0 instead.
		return (toReturn < 0) ? 0 : toReturn;
	}
	
	public void setBV(Integer i) {
		if (i < 0)
			BV = 0;
		BV = i;
	}
	
	/**
	 * @return the megamek.common.entity this Unit represents
	 */
	public Entity getEntity() {
		
		//alreayd loaded. return.
		if (unitEntity != null)
			return unitEntity;
		
		//need to load. do so.
		unitEntity = loadMech(getUnitFilename()); 
		return unitEntity;
	}
	
	public void setEntity(Entity unitEntity){
		this.unitEntity = unitEntity;
	}
	
	public  Entity loadMech(String Filename) {
		
		if ( Filename == null )
			return null;
		
		Entity ent = null;
		
		//look for a mek first
		try{
			ent = new MechFileParser (new File("./data/unitfiles/Meks.zip"),Filename).getEntity();
		} catch (Exception ex) {
			
			//not a mek, see if file is a vehicle...
			try {
				ent = new MechFileParser (new File("./data/unitfiles/Vehicles.zip"),Filename).getEntity();
			} catch (Exception exe){
				
				//neither mek nor veh. look for infantry.
				try {
					ent = new MechFileParser (new File("./data/unitfiles/Infantry.zip"),Filename).getEntity();
				} catch (Exception exei){
					
					/*
					 * Unit cannot be found in Meks.zip, Vehicles.zip or Infantry.zip. Probably
					 * a bad filename (table type) or a missing unit. Either way, need to set
					 * up and return a failsafe unit.
					 */
					MWServ.mwlog.errLog("Error loading: " + Filename);
					
					try {
						ent = new MechFileParser (new File("./data/unitfiles/Meks.zip"),"Error OMG-UR-FD.hmp").getEntity();
					} catch (Exception exep) {
						
						/*
						 * Can't even find the default unit file. Are all the
						 * .zip files missing? Misnamed? Read access is denied?
						 */
						MWServ.mwlog.errLog("Unable to find default unit file. Server Exiting");
						MWServ.mwlog.errLog(exep);
						System.exit(1);
					}
				}
			}
		}
		return ent;
	}//end loadMech
	
	public void setPassesMaintainanceUntil(long l) {
		passesMaintainanceUntil = l;
	}
	
	public long getPassesMaintainanceUntil() {
		return passesMaintainanceUntil;
	}

	public Integer getScrappableFor() {
		return scrappableFor;
	}
	
	public void setScrappableFor(Integer i) {
		scrappableFor = i;
	}
	
	/**
	 *
	 * @return the amount of EXP the pilot has
	 */
	public Integer getExperience() {
		return new Integer(getPilot().getExperience());
	}
	
	/**
	 *
	 * @param experience the experience to set the pilot to
	 */
	public void setExperience(Integer experience) {
		this.getPilot().setExperience(experience.intValue());
		//this.experience = experience;
	}
	
	public boolean isOmni(){
		
		boolean isOmni = false;
		String targetChassis = this.getEntity().getChassis();
		
		if (this.getType() == Unit.VEHICLE) {
			try{
				FileInputStream fis = new FileInputStream("./data/buildtables/omnivehiclelist.txt");
				BufferedReader dis = new BufferedReader(new InputStreamReader(fis));
				while (dis.ready())
				{
					String chassie = dis.readLine();
					//check to see if the chassies listed in the file match omni vehicle chassies.
					if ( targetChassis.equalsIgnoreCase(chassie) ){
						isOmni = true;
						break;//found one no reason to keep searching :)
					}
				}
				dis.close();
				fis.close();
			} catch(Exception ex){
				//Simply means no omniveh list present. Ignore.
			}
		}
		else
			isOmni = this.getEntity().isOmni();
		
		return isOmni;
	}
	
	public boolean hasTAG(){
		return this.getEntity().hasTAG();
	}
	
	public boolean hasHoming(){
		
		for ( Mounted ammo : this.getEntity().getAmmo() ){
			if ( ((AmmoType)ammo.getType()).getMunitionType() == AmmoType.M_HOMING )
				return true;
		}
		return false;
	}
	
	public boolean hasSemiGuided(){
		for ( Mounted ammo : this.getEntity().getAmmo() ){
			//MWServ.mwlog.errLog("ammo type: "+((AmmoType)ammo.getType()).getMunitionType());
			if ( ((AmmoType)ammo.getType()).getMunitionType() == AmmoType.M_SEMIGUIDED )
				return true;
		}
		return false;
		
	}
	
	public int getPilotSkillBV(){
		
		int skillBV = 0;
		Iterator pilotSkills = this.getPilot().getSkills().getSkillIterator();
		
		while (pilotSkills.hasNext()){
			SPilotSkill skill = (SPilotSkill)pilotSkills.next();
			if (skill instanceof WeaponSpecialistSkill || skill instanceof PainResistanceSkill)
				skillBV  += skill.getBVMod(this.getEntity(),(SPilot)this.getPilot());
			else
				skillBV += skill.getBVMod(this.getEntity());
		}
		
		return skillBV;
	}
	
	public void setPilotIsRepairing(boolean repair){
		pilotIsRepairing = repair;
	}
	
	public boolean getPilotIsReparing(){
		return pilotIsRepairing;
	}
    
    public int getLastCombatPilot(){
        return lastCombatPilot;
    }
    
    public void setLastCombatPilot(int pilot){
        lastCombatPilot = pilot;
    }
    
    public int getDBId() {
    	return dbId;
    }
    
    public void setDBId(int newId) {
    	dbId = newId;
    }
}
