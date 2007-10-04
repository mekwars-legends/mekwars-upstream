/*
 * MekWars - Copyright (C) 2007 
 * 
 * Original author - Torren (torren@users.sourceforge.net)
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

package common;

import megamek.common.EquipmentType;
import megamek.common.TechConstants;

/**
 *	Unit Equipment Container
 */
public class BMEquipment {

	private String equipmentInternalName = "";
	private String equipmentName = "";
	private double cost = 0;
	private int amount = 0;
	private boolean costUp = false;
	private String equipmentType = "";
	private String tech = "";
	
	static public String PART_AMMO = "Ammo";
	static public String PART_WEAPON = "Weapons";
	static public String PART_MISC = "Misc";
	static public String PART_ARMOR = "Armor";
	
	public void setEquipmentInternalName(String name) {
		this.equipmentInternalName = name;
	}
	
	public String getEquipmentInternalName() {
		return this.equipmentInternalName;
	}
	
	public void setEquipmentName(String name) {
		this.equipmentName = name;
	}
	
	public String getEquipmentName() {
		return this.equipmentName;
	}
	
	public void setEquipmentType(String type) {
		this.equipmentType = type;
	}
	
	public String getEquipmentType() {
		return this.equipmentType;
	}
	
	public void setCost(double cost) {
		
		this.cost = cost;
	}
	
	public double getCost() {
		
		return cost;
	}
	
	public void setAmount(int amount) {
		
		this.amount = amount;
	}
	
	public int getAmount() {
		
		return amount;
	}
	
	public boolean isCostUp() {
		return this.costUp;
	}
	
	public void setCostUp(boolean update) {
		this.costUp = update;
	}
	
	public String getTech() {
		
		if ( tech.trim().length() > 0 )
			return tech;
		
		EquipmentType eq = EquipmentType.get(getEquipmentInternalName());

		if ( eq == null ) {
			if ( this.getEquipmentInternalName().indexOf("Engine") > 0 &&
					this.getEquipmentInternalName().startsWith("Clan"))
				tech = "Clan";
				
			else if ( this.getEquipmentInternalName().indexOf("Engine") > 0 &&
					this.getEquipmentInternalName().startsWith("IS"))
				tech = "IS";
			else
				tech = "All";
		}else{
			if ( eq.getTechLevel() == TechConstants.T_CLAN_LEVEL_2 ||
					eq.getTechLevel() == TechConstants.T_CLAN_LEVEL_3)
				tech = "Clan";
			else if ( eq.getTechLevel() == TechConstants.T_ALL ||
					eq.getTechLevel() < TechConstants.T_IS_LEVEL_1 )
				tech = "All" ;
			else
				tech =  "IS";

		}
		
		return tech;
	}
	
	public BMEquipment clone() {
		BMEquipment clone = new BMEquipment();
		
		clone.setAmount(this.getAmount());
		clone.setCost(this.getCost());
		clone.setCostUp(this.isCostUp());
		clone.setEquipmentInternalName(this.getEquipmentInternalName());
		clone.setEquipmentName(this.getEquipmentName());
		clone.setEquipmentType(this.getEquipmentType());
		clone.getTech();
		
		return clone;
	}
	
}
