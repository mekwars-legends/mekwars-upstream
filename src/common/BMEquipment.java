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

/**
 *	Unit Equipment Container
 */
public class BMEquipment {

	private String equipmentInternalName = "";
	private String equipmentName = "";
	private double cost = 0;
	private int amount = 0;
	private boolean costUp = false;
	private String tech = "IS";
	private String equipmentType = "";
	
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
	
	public void setTech(String tech) {
		this.tech = tech;
	}
	
	public String getTech() {
		return this.tech;
	}
	
	public BMEquipment clone() {
		BMEquipment clone = new BMEquipment();
		
		clone.setAmount(this.getAmount());
		clone.setCost(this.getCost());
		clone.setCostUp(this.isCostUp());
		clone.setEquipmentInternalName(this.getEquipmentInternalName());
		clone.setEquipmentName(this.getEquipmentName());
		clone.setEquipmentType(this.getEquipmentType());
		clone.setTech(this.getTech());
		
		return clone;
	}
	
}
