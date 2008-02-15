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

final public class AdvancedTerrain {

    private String displayName = "";
    private String staticMapName = "surprise";

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
    private int blizzardChance = 0;
    private int blowingSandChance = 0;
    private int heavySnowfallChance = 0;
    private int lightRainfallChance = 0;
    private int heavyRainfallChance = 0;
    private int moderateWindsChance = 0;
    private int highWindsChance = 0;

    public String toString() {
        String result = "";
        result += "$" + displayName + "$" + xSize + "$" + ySize + "$" + staticMap + "$" + xBoardSize 
        + "$" + yBoardSize + "$" + lowTemp + "$" + highTemp + "$" + gravity + "$" + vacuum + "$" + nightChance 
        + "$" + nightTempMod + "$" + staticMapName + "$" + minVisibility + "$" + maxVisibility + "$" 
        + blizzardChance + "$" + blowingSandChance + "$" + heavySnowfallChance + "$" + lightRainfallChance + "$" 
        + heavyRainfallChance + "$" + moderateWindsChance + "$" + highWindsChance;

        return result;
    }

    public void binIn(BinReader in) throws IOException {
        displayName = in.readLine("displayName");
        staticMap = in.readBoolean("staticMap");
        xSize = in.readInt("xSize");
        ySize = in.readInt("ySize");
        xBoardSize = in.readInt("xBoardSize");
        yBoardSize = in.readInt("yBoardSize");
        lowTemp = in.readInt("lowTemp");
        highTemp = in.readInt("highTemp");
        gravity = in.readDouble("gravity");
        vacuum = in.readBoolean("vacuum");
        nightChance = in.readInt("nightChance");
        nightTempMod = in.readInt("nightTempMod");
        staticMapName = in.readLine("staticMapName");
        minVisibility = in.readInt("minvisibility");
        maxVisibility = in.readInt("maxvisibility");
        blizzardChance = in.readInt("blizzardChance");
        blowingSandChance = in.readInt("blowingSandChance");
        heavySnowfallChance = in.readInt("heavySnowfallChance");
        lightRainfallChance = in.readInt("lightRainfallChance");
        heavyRainfallChance = in.readInt("heavyRainfallChance");
        moderateWindsChance = in.readInt("moderateWindsChance");
        highWindsChance = in.readInt("highWindsChance");
    }

    public void binOut(BinWriter out) throws IOException {

        out.println(displayName, "displayName");
        out.println(staticMap, "staticMap");
        out.println(xSize, "xSize");
        out.println(ySize, "ySize");
        out.println(xBoardSize, "xBoardSize");
        out.println(yBoardSize, "yBoardSize");
        out.println(lowTemp, "lowTemp");
        out.println(highTemp, "highTemp");
        out.println(gravity, "gravity");
        out.println(vacuum, "vacuum");
        out.println(nightChance, "nightChance");
        out.println(nightTempMod, "nightTempMod");
        out.println(staticMapName, "staticMapName");
        out.println(minVisibility, "minvisibility");
        out.println(maxVisibility, "maxvisibility");
        out.println(blizzardChance, "blizzardChance");
        out.println(blowingSandChance, "blowingSandChance");
        out.println(heavySnowfallChance, "heavySnowfallChance");
        out.println(lightRainfallChance, "lightRainfallChance");
        out.println(heavyRainfallChance, "heavyRainfallChance");
        out.println(moderateWindsChance, "moderateWindsChance");
        out.println(highWindsChance, "highWindsChance");
    }

    public AdvancedTerrain(String s) {
        StringTokenizer command = new StringTokenizer(s, "$");

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
        setMinVisibility(minVisibility);
        setMaxVisibility(maxVisibility);
        if (command.hasMoreTokens())
            this.setBlizzardChance(Integer.parseInt(command.nextToken()));
        if (command.hasMoreTokens())
            this.setBlowingSandChance(Integer.parseInt(command.nextToken()));
        if (command.hasMoreTokens())
            this.setHeavySnowfallChance(Integer.parseInt(command.nextToken()));
        if (command.hasMoreTokens())
            this.setLightRainfallChance(Integer.parseInt(command.nextToken()));
        if (command.hasMoreTokens())
            this.setHeavyRainfallChance(Integer.parseInt(command.nextToken()));
        if (command.hasMoreTokens())
            this.setModerateWindsChance(Integer.parseInt(command.nextToken()));
        if (command.hasMoreTokens())
            this.setHighWindsChance(Integer.parseInt(command.nextToken()));

    }

    public AdvancedTerrain() {
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String name) {
        displayName = name;
    }

    public boolean isStaticMap() {
        return staticMap;
    }

    public void setStaticMap(boolean map) {
        staticMap = map;
    }

    public boolean isVacuum() {
        return vacuum;
    }

    public void setVacuum(boolean vacuum) {
        this.vacuum = vacuum;
    }

    public int getXBoardSize() {
        return xBoardSize;
    }

    public void setXBoardSize(int size) {
        xBoardSize = size;
    }

    public int getYBoardSize() {
        return yBoardSize;
    }

    public void setYBoardSize(int size) {
        yBoardSize = size;
    }

    public int getLowTemp() {
        return lowTemp;
    }

    public void setLowTemp(int temp) {
        lowTemp = temp;
    }

    public int getHighTemp() {
        return highTemp;
    }

    public void setHighTemp(int temp) {
        highTemp = temp;
    }

    public double getGravity() {
        return gravity;
    }

    public void setGravity(double grav) {
        gravity = grav;
    }

    public int getNightChance() {
        return nightChance;
    }

    public void setNightChance(int chance) {
        nightChance = chance;
    }

    public int getNightTempMod() {
        return nightTempMod;
    }

    public void setNightTempMod(int mod) {
        nightTempMod = mod;
    }

    public String getStaticMapName() {
        return staticMapName;
    }

    public void setStaticMapName(String name) {
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

    public void setBlizzardChance(int chance){
        this.blizzardChance = chance;
    }
    
    public int getBlizzardChance(){
        return this.blizzardChance;
    }
    
    public void setBlowingSandChance(int chance){
        this.blowingSandChance = chance;
    }
    
    public int getBlowingSandChance(){
        return this.blowingSandChance;
    }
    
    public void setHeavySnowfallChance(int chance){
        this.heavySnowfallChance = chance;
    }
    
    public int getHeavySnowfallChance(){
        return this.heavySnowfallChance;
    }
    
    public void setLightRainfallChance(int chance){
        this.lightRainfallChance = chance;
    }
    
    public int getLightRainfallChance(){
        return this.lightRainfallChance;
    }
    
    public void setHeavyRainfallChance(int chance){
        this.heavyRainfallChance = chance;
    }
    
    public int getHeavyRainfallChance(){
        return this.heavyRainfallChance;
    }
    
    public void setModerateWindsChance(int chance){
        this.moderateWindsChance = chance;
    }
    
    public int getModerateWindsChance(){
        return this.moderateWindsChance;
    }
    
    public void setHighWindsChance(int chance){
        this.highWindsChance = chance;
    }

    public int getHighWindsChance(){
        return this.highWindsChance;
    }

    public AdvancedTerrain clone() {
        AdvancedTerrain clone = new AdvancedTerrain(this.toString());

        return clone;

    }
}
