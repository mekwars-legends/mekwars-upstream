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

import megamek.common.PlanetaryConditions;

import common.util.BinReader;
import common.util.BinWriter;
import common.util.TokenReader;

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
    private int duskChance = 0;
    private int fullMoonChance = 0;
    private int moonlessChance = 0;
    private int pitchBlackChance = 0;
    private int nightTempMod = 0;
    private int minVisibility = 100;
    private int maxVisibility = 100;
    private int atmosphere = PlanetaryConditions.ATMO_STANDARD;
    
    private int lightRainfallChance = 0;
    private int moderateRainfallChance = 0;
    private int heavyRainfallChance = 0;
    private int downPourChance = 0;
    
    private int lightSnowfallChance = 0;
    private int moderateSnowfallChance = 0;
    private int heavySnowfallChance = 0;
    private int sleetChance = 0;
    private int iceStormChance = 0;
    private int lightHailChance = 0;
    private int heavyHailChance = 0;

    private int lightWindsChance = 0;
    private int moderateWindsChance = 0;
    private int strongWindsChance = 0;
    private int stormWindsChance = 0;
    private int tornadoF13WindsChance = 0;
    private int tornadoF4WindsChance = 0;
    
    private int lightFogChance = 0;
    private int heavyFogChance = 0;

    private int emiChance = 0;
    
    //MegaMek Planetary Conditions
    //set up the specific conditions
    private int lightConditions = PlanetaryConditions.L_DAY;
    private int weatherConditions = PlanetaryConditions.WE_NONE;
    private int windStrength = PlanetaryConditions.WI_NONE;
    private int windDirection = PlanetaryConditions.WI_NONE;
    private int maxWindStrength = PlanetaryConditions.WI_TORNADO_F4;
    private boolean shiftWindDirection = false;
    private boolean shiftWindStrength = false;
    private int fog = PlanetaryConditions.FOG_NONE;
    private int temperature = 25;
    private boolean emi = false;
    private boolean terrainAffected = true;

    public String toString() {
        String result = "";
        result = "$";
        
        if ( displayName.trim().length() < 1 )
            result += "Terrain";
        else
            result += displayName;
        
        result += "$";
        result += xSize;
        result += "$";
        result += ySize;
        result += "$";
        result += staticMap;
        result += "$";
        result += xBoardSize;
        result += "$";
        result +=  yBoardSize;
        result +=  "$";
        result +=  lowTemp;
        result +=  "$";
        result +=  highTemp;
        result +=  "$";
        result +=  gravity;
        result +=  "$";
        result +=  vacuum; 
        result +=  "$"; 
        result +=  fullMoonChance; 
        result +=  "$"; 
        result +=  nightTempMod; 
        result +=  "$"; 
        result +=  staticMapName; 
        result +=  "$"; 
        result +=  minVisibility; 
        result +=  "$"; 
        result +=  maxVisibility; 
        result +=  "$"; 
        result +=  moderateRainfallChance; 
        result +=  "$"; 
        result +=  moderateSnowfallChance; 
        result +=  "$"; 
        result +=  heavySnowfallChance; 
        result +=  "$"; 
        result +=  lightRainfallChance; 
        result +=  "$"; 
        result +=  heavyRainfallChance; 
        result +=  "$"; 
        result +=  moderateWindsChance; 
        result +=  "$"; 
        result +=  strongWindsChance;
        result +=  "$";
        result +=  downPourChance;
        result +=  "$";
        result +=  lightSnowfallChance;
        result +=  "$";
        result +=  sleetChance;
        result +=  "$";
        result +=  iceStormChance;
        result +=  "$";
        result += lightHailChance;
        result +=  "$";
        result += heavyHailChance;
        result +=  "$";
        result += stormWindsChance;
        result +=  "$";
        result += tornadoF13WindsChance;
        result +=  "$";
        result += tornadoF4WindsChance;
        result +=  "$";
        result += atmosphere;
        result +=  "$";
        result += lightFogChance;
        result +=  "$";
        result += heavyFogChance;
        result +=  "$";
        result += duskChance;
        result +=  "$";
        result += moonlessChance;
        result +=  "$";
        result += pitchBlackChance;
        result +=  "$";
        result += emiChance;
        result +=  "$"; 
        result += lightWindsChance; 
        

        return result;
    }

    public void binIn(BinReader in) throws IOException {
        displayName = in.readLine("displayName");
        this.setStaticMap(in.readBoolean("staticMap"));
        xSize = in.readInt("xSize");
        ySize = in.readInt("ySize");
        xBoardSize = in.readInt("xBoardSize");
        yBoardSize = in.readInt("yBoardSize");
        lowTemp = in.readInt("lowTemp");
        highTemp = in.readInt("highTemp");
        gravity = in.readDouble("gravity");
        vacuum = in.readBoolean("vacuum");
        fullMoonChance = in.readInt("nightChance");
        nightTempMod = in.readInt("nightTempMod");
        staticMapName = in.readLine("staticMapName");
        minVisibility = in.readInt("minvisibility");
        maxVisibility = in.readInt("maxvisibility");
        moderateRainfallChance = in.readInt("moderateRainfallChance");
        moderateSnowfallChance = in.readInt("moderateSnowfallChance");
        heavySnowfallChance = in.readInt("heavySnowfallChance");
        lightRainfallChance = in.readInt("lightRainfallChance");
        heavyRainfallChance = in.readInt("heavyRainfallChance");
        lightWindsChance = in.readInt("lightWindsChance");
        moderateWindsChance = in.readInt("moderateWindsChance");
        strongWindsChance = in.readInt("strongWindsChance");
        downPourChance = in.readInt("downPourChance");
        lightSnowfallChance = in.readInt("lightSnowfallChance");
        sleetChance = in.readInt("sleetChance");
        iceStormChance = in.readInt("iceStormChance");
        lightHailChance = in.readInt("lightHailChance");
        heavyHailChance = in.readInt("heavyHailChance");
        stormWindsChance = in.readInt("stormWindsChance");
        tornadoF13WindsChance = in.readInt("tornadoF13WindsChance");
        tornadoF4WindsChance = in.readInt("tornadoF4WindsChance");
        atmosphere = in.readInt("atmosphere");
        lightFogChance = in.readInt("lightFogChance");
        heavyFogChance = in.readInt("heavyFogChance");
        duskChance = in.readInt("duskChance");
        moonlessChance = in.readInt("moonlessChance");
        pitchBlackChance = in.readInt("pitchBlackChance");
        emiChance = in.readInt("emiChance");
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
        out.println(fullMoonChance, "nightChance");
        out.println(nightTempMod, "nightTempMod");
        out.println(staticMapName, "staticMapName");
        out.println(minVisibility, "minvisibility");
        out.println(maxVisibility, "maxvisibility");
        out.println(moderateRainfallChance, "moderateRainfallChance");
        out.println(moderateSnowfallChance, "moderateSnowfallChance");
        out.println(heavySnowfallChance, "heavySnowfallChance");
        out.println(lightRainfallChance, "lightRainfallChance");
        out.println(heavyRainfallChance, "heavyRainfallChance");
        out.println(lightWindsChance, "lightWindsChance");
        out.println(moderateWindsChance, "moderateWindsChance");
        out.println(strongWindsChance, "strongWindsChance");
        out.println(downPourChance, "downPourChance");
        out.println(lightSnowfallChance, "lightSnowfallChance");
        out.println(sleetChance, "sleetChance");
        out.println(iceStormChance, "iceStormChance");
        out.println(lightHailChance, "lightHailChance");
        out.println(heavyHailChance, "heavyHailChance");
        out.println(stormWindsChance, "stormWindsChance");
        out.println(tornadoF13WindsChance, "tornadoF13WindsChance");
        out.println(tornadoF4WindsChance, "tornadoF4WindsChance");
        out.println(atmosphere, "atmosphere");
        out.println(lightFogChance, "lightFogChance");
        out.println(heavyFogChance, "heavyFogChance");
        out.println(duskChance, "duskChance");
        out.println(moonlessChance, "moonlessChance");
        out.println(pitchBlackChance, "pitchBlackChance");
        out.println(emiChance, "emiChance");
    }

    public AdvancedTerrain(String s) {
        StringTokenizer command = new StringTokenizer(s, "$");

        setDisplayName(TokenReader.readString(command));
        setXSize(TokenReader.readInt(command));
        setYSize(TokenReader.readInt(command));
        setStaticMap(TokenReader.readBoolean(command));
        setXBoardSize(TokenReader.readInt(command));
        setYBoardSize(TokenReader.readInt(command));
        setLowTemp(TokenReader.readInt(command));
        setHighTemp(TokenReader.readInt(command));
        setGravity(TokenReader.readDouble(command));
        setVacuum(TokenReader.readBoolean(command));
        setNightChance(TokenReader.readInt(command));
        setNightTempMod(TokenReader.readInt(command));
        setStaticMapName(TokenReader.readString(command));
        setMinVisibility(TokenReader.readInt(command));
        setMaxVisibility(TokenReader.readInt(command));
        this.setModerateRainFallChance(TokenReader.readInt(command));
        this.setModerateSnowFallChance(TokenReader.readInt(command));
        this.setHeavySnowfallChance(TokenReader.readInt(command));
        this.setLightRainfallChance(TokenReader.readInt(command));
        this.setHeavyRainfallChance(TokenReader.readInt(command));
        this.setModerateWindsChance(TokenReader.readInt(command));
        this.setStrongWindsChance(TokenReader.readInt(command));
        setDownPourChance(TokenReader.readInt(command));
        setLightSnowfallChance(TokenReader.readInt(command));
        setSleetChance(TokenReader.readInt(command));
        setIceStormChance(TokenReader.readInt(command));
        setLightHailChance(TokenReader.readInt(command));
        setHeavyHailChance(TokenReader.readInt(command));
        setStormWindsChance(TokenReader.readInt(command));
        setTornadoF13WindChance(TokenReader.readInt(command));
        setTornadoF4WindsChance(TokenReader.readInt(command));
        setAtmosphere(TokenReader.readInt(command));
        setLightFogChance(TokenReader.readInt(command));
        setHeavyfogChance(TokenReader.readInt(command));
        setDuskChance(TokenReader.readInt(command));
        setMoonLessNightChance(TokenReader.readInt(command));
        setPitchBlackNightChance(TokenReader.readInt(command));
        setEMIChance(TokenReader.readInt(command));
        setLightWindChance(TokenReader.readInt(command));
        
        //MegaMek Planetary Conditions this should always be last
        setLightConditions(TokenReader.readInt(command));
        setWeatherConditions(TokenReader.readInt(command));
        setWindStrength(TokenReader.readInt(command));
        setWindDirection(TokenReader.readInt(command));
        setShiftingWindDirection(TokenReader.readBoolean(command));
        setShiftingWindStrength(TokenReader.readBoolean(command));
        setFog(TokenReader.readInt(command));
        setTemperature(TokenReader.readInt(command));
        setEMI(TokenReader.readBoolean(command));
        setTerrainAffected(TokenReader.readBoolean(command));
        setMaxWindStrength(TokenReader.readInt(command));
        
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

    @Deprecated
    public boolean isVacuum() {
        return vacuum;
    }

    @Deprecated
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

    public int getDuskChance() {
        return duskChance;
    }

    public void setDuskChance(int chance) {
        duskChance = chance;
    }

    public int getNightChance() {
        return fullMoonChance;
    }

    public void setNightChance(int chance) {
        fullMoonChance = chance;
    }

    public int getMoonLessNightChance() {
        return moonlessChance;
    }

    public void setMoonLessNightChance(int chance) {
        moonlessChance = chance;
    }

    public int getPitchBlackNightChance() {
        return pitchBlackChance;
    }

    public void setPitchBlackNightChance(int chance) {
        pitchBlackChance = chance;
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

    @Deprecated
    public int getMinVisibility() {
        return minVisibility;
    }

    @Deprecated
    public int getMaxVisibility() {
        return maxVisibility;
    }

    @Deprecated
    public void setMinVisibility(int minVisibility) {
        this.minVisibility = minVisibility;
    }

    @Deprecated
    public void setMaxVisibility(int maxVisibility) {
        this.maxVisibility = maxVisibility;
    }

    public void setModerateSnowFallChance(int chance){
        this.moderateSnowfallChance = chance;
    }
    
    public int getModerateSnowFallChance(){
        return this.moderateSnowfallChance;
    }
    
    public void setModerateRainFallChance(int chance){
        this.moderateRainfallChance = chance;
    }
    
    public int getModerateRainFallChance(){
        return this.moderateRainfallChance;
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
    
    public void setStrongWindsChance(int chance){
        this.strongWindsChance = chance;
    }

    public int getStrongWindsChance(){
        return this.strongWindsChance;
    }

    public void setStormWindsChance(int chance){
        this.stormWindsChance = chance;
    }

    public int getStormWindsChance(){
        return this.stormWindsChance;
    }

    public void setLightWindChance(int chance){
        this.lightWindsChance = chance;
    }

    public int getLightWindsChance(){
        return this.lightWindsChance;
    }

    public void setTornadoF13WindChance(int chance){
        this.tornadoF13WindsChance = chance;
    }

    public int getTornadoF13WindsChance(){
        return this.tornadoF13WindsChance;
    }

    public void setTornadoF4WindsChance(int chance){
        this.tornadoF4WindsChance = chance;
    }

    public int getTornadoF4WindsChance(){
        return this.tornadoF4WindsChance;
    }

    public void setDownPourChance(int chance){
        this.downPourChance = chance;
    }

    public int getDownPourChance(){
        return this.downPourChance;
    }

    public void setLightSnowfallChance(int chance){
        this.lightSnowfallChance = chance;
    }

    public int getLightSnowfallChance(){
        return this.lightSnowfallChance;
    }

    public void setSleetChance(int chance){
        this.sleetChance = chance;
    }

    public int getSleetChance(){
        return this.sleetChance;
    }

    public void setIceStormChance(int chance){
        this.iceStormChance = chance;
    }

    public int getIceStormChance(){
        return this.iceStormChance;
    }

    public void setLightHailChance(int chance){
        this.lightHailChance = chance;
    }

    public int getLightHailChance(){
        return this.lightHailChance;
    }

    public void setHeavyHailChance(int chance){
        this.heavyHailChance = chance;
    }

    public int getHeavyHailChance(){
        return this.heavyHailChance;
    }

    public AdvancedTerrain clone() {
        AdvancedTerrain clone = new AdvancedTerrain(this.toString());

        return clone;
    }

    public void setLightConditions(int light) {
        this.lightConditions = light;
    }
    
    public int getLightConditions() {
        return this.lightConditions;
    }
    
    public void setWeatherConditions(int weather) {
        this.weatherConditions = weather;
    }
    
    public int getWeatherConditions() {
        return this.weatherConditions;
    }
    
    public void setWindStrength(int wind) {
        this.windStrength = wind;
    }
    
    public int getWindStrength() {
        return this.windStrength;
    }
    
    public void setWindDirection(int dir) {
        this.windDirection = dir;
    }
    
    public int getWindDirection() {
        return this.windDirection;
    }
    
    public void setShiftingWindDirection(boolean shift) {
        this.shiftWindDirection = shift;
    }
    
    public boolean hasShifitingWindDirection() {
        return this.shiftWindDirection;
    }
    
    public void setShiftingWindStrength(boolean strength) {
        this.shiftWindStrength= strength;
    }
    
    public boolean hasShifitingWindStrength() {
        return this.shiftWindStrength;
    }
    
    public String toStringPlanetaryConditions() {
        StringBuilder results = new StringBuilder();
        
        results.append(this.toString());
        results.append("$");
        results.append(lightConditions);
        results.append("$");
        results.append(weatherConditions);
        results.append("$");
        results.append(windStrength);
        results.append("$");
        results.append(windDirection);
        results.append("$");
        results.append(shiftWindDirection);
        results.append("$");
        results.append(shiftWindStrength);
        results.append("$");
        results.append(fog);
        results.append("$");
        results.append(temperature);
        results.append("$");
        results.append(emi);
        results.append("$");
        results.append(terrainAffected);
        results.append("$");
        results.append(maxWindStrength);

        return results.toString();
        
    }

    public boolean isTerrainAffected() {
        return this.terrainAffected;
    }
    
    public void setTerrainAffected(boolean terrain) {
        this.terrainAffected = terrain;
    }

    public boolean hasEMI() {
        return this.emi;
    }
    
    public void setEMI(boolean emi) {
        this.emi = emi;
    }
    
    public int getTemperature() {
        return this.temperature;
    }
    
    public void setTemperature(int temp) {
        this.temperature = temp;
    }
    
    public int getFog() {
        return this.fog;
    }
    
    public void setFog(int fog) {
        this.fog = fog;
    }
    
    public int getAtmosphere() {
        return this.atmosphere;
    }
    
    public void setAtmosphere(int atmo) {
        
        if ( atmo < 0 || atmo > PlanetaryConditions.ATMO_VHIGH ) {
            atmo = PlanetaryConditions.ATMO_STANDARD;
        }
        this.atmosphere = atmo;
    }
    
    public int getLightFogChance() {
        return this.lightFogChance;
    }
    
    public void setLightFogChance(int chance) {
        this.lightFogChance = chance;
    }
    
    public int getHeavyFogChance() {
        return this.heavyFogChance;
    }
    
    public void setHeavyfogChance(int chance) {
        this.heavyFogChance = chance;
    }
    
    public int getEMIChance(){
        return this.emiChance;
    }
    
    public void setEMIChance(int chance){
        this.emiChance = chance;
    }
    
    public void setMaxWindStrength(int wind){
        this.maxWindStrength = wind;
    }
    
    public int getMaxWindStrength(){
        return this.maxWindStrength;
    }
    
}
