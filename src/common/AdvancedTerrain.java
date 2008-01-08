/*
 * MekWars - Copyright (C) 2004 
 * 
 * Derived from MegaMekNET (http://www.sourceforge.net/projects/megameknet)
 * Original author Helge Richter (McWizard)
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

import java.io.IOException;
import java.util.StringTokenizer;

import common.util.BinReader;
import common.util.BinWriter;

/**
 * Advanced Environment for planets.
 * 
 * @@author Torren (Jason Tighe)
 * 
 * allows So's to set up each individual terrain on a planet.
 */

final public class AdvancedTerrain{
    

    private String displayName = "";
    private String staticMapName ="surprise";

    private int xSize = -1;
    private int ySize = -1;
    
    private boolean staticMap = false;
    private int xBoardSize = -1;
    private int yBoardSize = -1;
    private int lowTemp = 25;
    private int highTemp = 25;
    private double gravity = 1.0;
    private boolean vacuum = false;
    private int nightChance = 0;
    private int nightTempMod = 0;
    private int minVisibility = 100;
    private int maxVisibility = 100;

	public String toString(){
        String result = "";
        result += "$"+ displayName
    	+"$"+ xSize
    	+"$"+ ySize
    	+"$"+ staticMap
    	+"$"+ xBoardSize
    	+"$"+ yBoardSize
    	+"$"+ lowTemp
    	+"$"+ highTemp
    	+"$"+ gravity
    	+"$"+ vacuum
    	+"$"+ nightChance
    	+"$"+ nightTempMod
    	+"$"+ staticMapName
        +"$"+ minVisibility
        +"$"+ maxVisibility;

        return result;
    }
    
    public void binIn(BinReader in) throws IOException {
        displayName= in.readLine("displayName");
        staticMap= in.readBoolean("staticMap");
        xSize= in.readInt("xSize");
        ySize= in.readInt("ySize");
        xBoardSize= in.readInt("xBoardSize");
        yBoardSize= in.readInt("yBoardSize");
        lowTemp= in.readInt("lowTemp");
        highTemp= in.readInt("highTemp");
        gravity= in.readDouble("gravity");
        vacuum = in.readBoolean("vacuum");
        nightChance = in.readInt("nightChance");
        nightTempMod = in.readInt("nightTempMod");
        staticMapName = in.readLine("staticMapName");
        minVisibility = in.readInt("minvisibility");
        maxVisibility = in.readInt("maxvisibility");
    }

    public void binOut(BinWriter out) throws IOException {
        
        out.println(displayName,"displayName");
        out.println(staticMap,"staticMap");
        out.println(xSize,"xSize");
        out.println(ySize,"ySize");
        out.println(xBoardSize,"xBoardSize");
        out.println(yBoardSize,"yBoardSize");
        out.println(lowTemp,"lowTemp");
        out.println(highTemp,"highTemp");
        out.println(gravity,"gravity");
        out.println(vacuum,"vacuum");
        out.println(nightChance,"nightChance");
        out.println(nightTempMod,"nightTempMod");
        out.println(staticMapName,"staticMapName");
        out.println(minVisibility,"minvisibility");
        out.println(maxVisibility,"maxvisibility");
    }
    
    public AdvancedTerrain(String s){
        StringTokenizer command = new StringTokenizer(s,"$");

    	setDisplayName(command.nextToken());
    	setXSize(Integer.parseInt(command.nextToken()));
    	setYSize(Integer.parseInt(command.nextToken()));
    	setStaticMap(Boolean.parseBoolean(command.nextToken()));
    	setXBoardSize(Integer.parseInt(command.nextToken()));
    	setYBoardSize(Integer.parseInt(command.nextToken()));
    	setLowTemp(Integer.parseInt(command.nextToken()));
    	setHighTemp(Integer.parseInt(command.nextToken()));
    	setGravity(Double.parseDouble(command.nextToken()));
    	setVacuum(Boolean.parseBoolean(command.nextToken()));
    	setNightChance(Integer.parseInt(command.nextToken()));
    	setNightTempMod(Integer.parseInt(command.nextToken()));
    	setStaticMapName(command.nextToken());
        if ( command.hasMoreTokens() )
            this.setMinVisibility(Integer.parseInt(command.nextToken()));
        if ( command.hasMoreTokens() )
            this.setMaxVisibility(Integer.parseInt(command.nextToken()));

    }
    
    public AdvancedTerrain(){
    }
    
	public String getDisplayName(){
	    return displayName;
	}
	
	public void setDisplayName(String name){
	    displayName = name;
	}
	
	public boolean isStaticMap(){
	    return staticMap;
	}
	
	public void setStaticMap(boolean map){
	    staticMap = map;
	}
	
	public boolean isVacuum(){
	    return vacuum;
	}
	
	public void setVacuum(boolean vacuum){
	    this.vacuum = vacuum;
	}
	
	public int getXBoardSize(){
	    return xBoardSize;
	}
	
	public void setXBoardSize(int size){
	    xBoardSize = size;
	}
	
	public int getYBoardSize(){
	    return yBoardSize;
	}
	
	public void setYBoardSize(int size){
	    yBoardSize = size;
	}
	
	public int getLowTemp(){
	    return lowTemp;
	}
	
	public void setLowTemp(int temp){
	    lowTemp = temp;
	}
	
	public int getHighTemp(){
	    return highTemp;
	}
	
	public void setHighTemp(int temp){
	    highTemp = temp;
	}
	
	public double getGravity(){
	    return gravity;
	}
	
	public void setGravity(double grav){
	    gravity = grav;
	}
	
	public int getNightChance(){
	    return nightChance;
	}
	
	public void setNightChance(int chance){
	    nightChance = chance;
	}
	
	public int getNightTempMod(){
	    return nightTempMod;
	}
	
	public void setNightTempMod(int mod){
	    nightTempMod= mod;
	}

	public String getStaticMapName(){
	    return staticMapName;
	}
	
	public void setStaticMapName(String name){
	    staticMapName = name;
	}

    public int getXSize() {
        return xSize;
    }
    
    public int getYSize() {
        return ySize;
    }
    
    public void setXSize(int xSize) {
        this.xSize = xSize;
    }

    public void setYSize(int ySize) {
        this.ySize = ySize;
    }

    public int getMinVisibility() {
        return minVisibility;
    }
    
    public int getMaxVisibility() {
        return maxVisibility;
    }
    
    public void setMinVisibility(int minVisibility) {
        this.minVisibility = minVisibility;
    }

    public void setMaxVisibility(int maxVisibility) {
        this.maxVisibility = maxVisibility;
    }
 }

