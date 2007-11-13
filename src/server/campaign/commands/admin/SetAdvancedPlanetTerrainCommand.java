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

package server.campaign.commands.admin;

import java.util.StringTokenizer;

import common.AdvancedTerrain;

import server.campaign.commands.Command;
import server.campaign.CampaignMain;
import server.campaign.SPlanet;
import server.MWChatServer.auth.IAuthenticator;


@SuppressWarnings({"unchecked","serial"})
public class SetAdvancedPlanetTerrainCommand implements Command {
	
	int accessLevel = IAuthenticator.ADMIN;
	String syntax = "Planet Name#Terrain ID#Display Name#XSize#YSize#StaticMap(bool)#Xboard#YBoard#Low Temp#High Temp#Gravity(Double)#Vaccum(bool)#Night Chance#Night Temp Mod#Static Map(bool)#Min Visibility#Max Visibility#";
	public int getExecutionLevel(){return accessLevel;}
	public void setExecutionLevel(int i) {accessLevel = i;}
	public String getSyntax() { return syntax;}
	
	public void process(StringTokenizer command,String Username) {
		
		//access level check
		int userLevel = CampaignMain.cm.getServer().getUserLevel(Username);
		if(userLevel < getExecutionLevel()) {
			CampaignMain.cm.toUser("AM:Insufficient access level for command. Level: " + userLevel + ". Required: " + accessLevel + ".",Username,true);
			return;
		}
		
		SPlanet planet =  (SPlanet) CampaignMain.cm.getData().getPlanetByName(command.nextToken());
		if ( planet == null ) {
			CampaignMain.cm.toUser("Unknown Planet",Username,true);
			return;
		}
		
		int id = Integer.parseInt(command.nextToken());
		
		AdvancedTerrain aTerrain = new AdvancedTerrain();
		
		aTerrain = planet.getAdvancedTerrain().get(new Integer(id));
		
		if ( aTerrain == null){
			CampaignMain.cm.toUser("Could not find that terrain on planet "+planet.getName(),Username,true);
			return;
		}
		
		aTerrain.setDisplayName(command.nextToken());
		aTerrain.setXSize(Integer.parseInt(command.nextToken()));
		aTerrain.setYSize(Integer.parseInt(command.nextToken()));
		aTerrain.setStaticMap(new Boolean(command.nextToken()).booleanValue());
		aTerrain.setXBoardSize(Integer.parseInt(command.nextToken()));
		aTerrain.setYBoardSize(Integer.parseInt(command.nextToken()));
		aTerrain.setLowTemp(Integer.parseInt(command.nextToken()));
		aTerrain.setHighTemp(Integer.parseInt(command.nextToken()));
		aTerrain.setGravity(Double.parseDouble(command.nextToken()));
		aTerrain.setVacuum(new Boolean(command.nextToken()).booleanValue());
		aTerrain.setNightChance(Integer.parseInt(command.nextToken()));
		aTerrain.setNightTempMod(Integer.parseInt(command.nextToken()));
		aTerrain.setStaticMapName(command.nextToken());
		
		if ( command.hasMoreTokens() )
			aTerrain.setMinVisibility(Integer.parseInt(command.nextToken()));
		
		if ( command.hasMoreTokens() )
			aTerrain.setMaxVisibility(Integer.parseInt(command.nextToken()));
		
		planet.getAdvancedTerrain().put(new Integer(id),aTerrain);
		planet.setAdvancedTerrain(planet.getAdvancedTerrain());
		planet.updated();
		
        if(CampaignMain.cm.isUsingMySQL())
        	planet.toDB();
		
		CampaignMain.cm.toUser("Advanced Terrain set for terrain: "+aTerrain.getDisplayName()+" on planet "+planet.getName(),Username,true);
		//server.MWServ.mwlog.modLog(Username + " set Advanced Terrain for terrain: "+aTerrain.getDisplayName()+" on planet "+planet.getName());
		CampaignMain.cm.doSendModMail("NOTE",Username + " has set Advanced Terrain for terrain: "+aTerrain.getDisplayName()+" on planet "+planet.getName());
		
	}
}