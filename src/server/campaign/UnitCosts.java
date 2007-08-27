/*
 * MekWars - Copyright (C) 2005
 * 
 * Original author - nmorris (urgru@users.sourceforge.net)  
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import common.Unit;
import server.MWServ;

@SuppressWarnings("unchecked")
public class UnitCosts {
	
	Vector<Vector> minCostUnitList = new Vector<Vector>();
	Vector<Vector> maxCostUnitList = new Vector<Vector>();
	
	public UnitCosts(){
		for (int weight = Unit.LIGHT; weight <= Unit.ASSAULT; weight++){
			Vector<Double> maxVector = new Vector<Double>();
			Vector<Double> minVector = new Vector<Double>();
			for (int type = Unit.MEK; type <= Unit.BATTLEARMOR; type++){
				maxVector.add(0.0);
				minVector.add(0.0);
			}
			maxCostUnitList.add(maxVector);
			minCostUnitList.add(minVector);
		}	
	}
	
	public void addMaxCost(int weight, int type, double amount){
		Vector<Double> maxCost = this.getMaxCost(weight);
		maxCost.removeElementAt(type);
		maxCost.add(type,amount);
		this.setMaxCost(weight,maxCost);
	}
	
	public void addMinCost(int weight, int type, double amount){
		Vector<Double> minCost = this.getMinCost(weight);
		minCost.removeElementAt(type);
		minCost.add(type,amount);
		this.setMinCost(weight,minCost);
	}
	
	public Vector getMaxCost(int weight){
		return maxCostUnitList.get(weight);
	}
	
	public Vector getMinCost(int weight){
		return minCostUnitList.get(weight);
	}
	
	public Double getMaxCostValue(int weight, int type){
		Vector<Double> maxCostVector = maxCostUnitList.get(weight);
		return maxCostVector.get(type);
	}
	
	public Double getMinCostValue(int weight, int type){
		Vector<Double> minCostVector = minCostUnitList.get(weight);
		return minCostVector.get(type);
	}
	
	public void setMaxCost(int weight, Vector cost){
		maxCostUnitList.removeElementAt(weight);
		maxCostUnitList.add(weight,cost);
	}
	
	public void setMinCost(int weight, Vector cost){
		minCostUnitList.removeElementAt(weight);
		minCostUnitList.add(weight,cost);
	}
	
	public void loadUnitCosts(){
		
		String entityName = "";
		try {
			FileInputStream in = new FileInputStream("./data/unitfiles/Meks.zip");
			ZipInputStream zipFile = new ZipInputStream(in);
			
			while ( zipFile.available() == 1){
				
				ZipEntry entry = zipFile.getNextEntry();
				entityName = entry.getName();
				if ( entityName.startsWith("Error"))
					continue;
				
				SUnit unit = new SUnit("null",entityName,Unit.LIGHT);
				double cost = unit.getEntity().getCost();
				
				double maxCost = this.getMaxCostValue(unit.getWeightclass(),unit.getType()); 
				if ( cost >  maxCost ){
					this.addMaxCost(unit.getWeightclass(),unit.getType(),cost);
					continue;
				}
				
				double minCost = this.getMinCostValue(unit.getWeightclass(),unit.getType());
				if ( minCost == 0 || cost < minCost )
					this.addMinCost(unit.getWeightclass(),unit.getType(),cost);
			}
			
			zipFile.close();
			in.close();
			
		} catch(FileNotFoundException fnf) {
			MWServ.mwlog.errLog("Unable to load Meks.zip for UnitCosts.loadUnitCosts");
		} catch(Exception ex) {
			MWServ.mwlog.errLog("Error with Meks.zip file "+ entityName);
			MWServ.mwlog.errLog(ex);
		}
		
		try {
			FileInputStream in = new FileInputStream("./data/unitfiles/Vehicles.zip");
			ZipInputStream zipFile = new ZipInputStream(in);
			
			while (zipFile.available() == 1) {
				ZipEntry entry = zipFile.getNextEntry();
				entityName = entry.getName();
				SUnit unit = new SUnit("null",entityName,Unit.LIGHT);
				double cost = unit.getEntity().getCost();
				
				double maxCost = this.getMaxCostValue(unit.getWeightclass(),unit.getType()); 
				if ( cost >  maxCost ){
					this.addMaxCost(unit.getWeightclass(),unit.getType(),cost);
					continue;
				}
				
				double minCost = this.getMinCostValue(unit.getWeightclass(),unit.getType());
				if ( minCost == 0 || cost < minCost )
					this.addMinCost(unit.getWeightclass(),unit.getType(),cost);
				
			}
			
			zipFile.close();
			in.close();
			
		} catch(FileNotFoundException fnf){
			MWServ.mwlog.errLog("Unable to load Vehicles.zip for UnitCosts.loadUnitCosts");
		} catch(Exception ex){
			MWServ.mwlog.errLog("Error with Vehicles.zip file "+ entityName);
			MWServ.mwlog.errLog(ex);
		}
		
		try {
			FileInputStream in = new FileInputStream("./data/unitfiles/Infantry.zip");
			ZipInputStream zipFile = new ZipInputStream(in);
			
			while ( zipFile.available() == 1){
				ZipEntry entry = zipFile.getNextEntry();
				entityName = entry.getName();
				SUnit unit = new SUnit("null",entityName,Unit.LIGHT);
				double cost = unit.getEntity().getCost();
				
				double maxCost = this.getMaxCostValue(unit.getWeightclass(),unit.getType()); 
				if ( cost >  maxCost ){
					this.addMaxCost(unit.getWeightclass(),unit.getType(),cost);
					continue;
				}
				double minCost = this.getMinCostValue(unit.getWeightclass(),unit.getType());
				if ( minCost == 0 || cost < minCost )
					this.addMinCost(unit.getWeightclass(),unit.getType(),cost);
				
			}
			
			zipFile.close();
			in.close();
			
		} catch(FileNotFoundException fnf){
			MWServ.mwlog.errLog("Unable to load Infantry.zip for UnitCosts.loadUnitCosts");
		} catch(Exception ex){
			MWServ.mwlog.errLog("Error with Infantry.zip file "+ entityName);
			MWServ.mwlog.errLog(ex);
		}
	}
	
	public String displayUnitCostsLists(){
		StringBuilder result = new StringBuilder("<b>Max Costs</b><br>");
		for (int weight = 0; weight <= Unit.ASSAULT; weight++)
			for (int type = 0; type <= Unit.BATTLEARMOR; type++)
				result.append(Unit.getWeightClassDesc(weight)+" " + Unit.getTypeClassDesc(type)+" MaxCost: " + this.getMaxCostValue(weight,type)+" MinCost: " + this.getMinCostValue(weight,type) + ".<br>");
		return result.toString();
	}
}