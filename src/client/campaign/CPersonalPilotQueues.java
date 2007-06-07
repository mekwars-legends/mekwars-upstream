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

/*
 * Created:       03/25/05
 * Last refactor: 01/12/06
 */
package client.campaign;

import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.ArrayList;

import client.MWClient;

import common.Unit;
import common.campaign.pilot.Pilot;
import common.campaign.pilot.skills.PilotSkill;

/**
 * @author Torren (Jason Tighe)
 *
 * Client-side holder of Personal Pilot Queue information. The queue
 * is a collection of pilots, managed by a player, which may be moved
 * between eligible units (restricted by type and weightclass). This
 * client-side representation is necessary in order to draw menus and
 * controls in the CHQPanel.
 */

public class CPersonalPilotQueues {
	
	/*
	 * Don't need to synchronize on the client side. Two threads
	 * won't WRITE to these, although multiple threads may read.
	 */
	private ArrayList<LinkedList<Pilot>> mekPilots = new ArrayList<LinkedList<Pilot>>();
	private ArrayList<LinkedList<Pilot>> protoPilots = new ArrayList<LinkedList<Pilot>>();
	
	//CONSTRUCTOR    
	/**
	 * Simple param-free constructor that creates pilot-holding
	 * LinkedLists, in multiple weight classes (L -> A).
	 */
	public CPersonalPilotQueues() {
		
		for (int i = Unit.LIGHT; i <= Unit.ASSAULT; i++) {// for (0 - 3)
			mekPilots.add(i , new LinkedList<Pilot>());
			protoPilots.add(i, new LinkedList<Pilot>());
		}
		
	}
	
	//METHODS
	/**
	 * Rather than if/else'ing meks and protos throughout the other methods
	 * of the class, use a private get method which returns mek or proto as
	 * needed and then work on the arraylist without regard to type.
	 */
	private ArrayList<LinkedList<Pilot>> getUnitTypeQueue(int typeToGet) {
		
		if (typeToGet == Unit.PROTOMEK)
			return protoPilots;
		//else
		return mekPilots;
	}
	
	/**
	 * Private method which reads SPilot data from a PPQ string. Eliminates
	 * dulpicative code in formString's multiple loops thorugh the full data.
	 */
	private Pilot getPilotFromString(String pilotData) {
		
		StringTokenizer subTokenizer = new StringTokenizer(pilotData,"#");
		String pilotname = subTokenizer.nextToken();
		int exp = Integer.parseInt(subTokenizer.nextToken());
		int gunnery = Integer.parseInt(subTokenizer.nextToken());
		int piloting = Integer.parseInt(subTokenizer.nextToken());//will always be 5
		
		//set up the pilot
		Pilot pilot = new Pilot(pilotname,gunnery,piloting);
		pilot.setExperience(exp);
		
		//read skills, if any
		int skillAmount = Integer.parseInt(subTokenizer.nextToken());
		for (int i = 0; i < skillAmount; i++) {
			PilotSkill skill = new PilotSkill(
					Integer.parseInt(subTokenizer.nextToken()),subTokenizer.nextToken(),
					Integer.parseInt(subTokenizer.nextToken()),subTokenizer.nextToken());
			
			if (skill.getName().equals("Weapon Specialist") )//WS skill has an extra var
				pilot.setWeapon(subTokenizer.nextToken());
			
			if (skill.getName().equals("Trait") )//Trait skill has an extra var
				pilot.setCurrentFaction(subTokenizer.nextToken());

            if ( skill.getName().equals("Edge") ){
                pilot.setTac(Boolean.parseBoolean(subTokenizer.nextToken()));
                pilot.setKO(Boolean.parseBoolean(subTokenizer.nextToken()));
                pilot.setHeadHit(Boolean.parseBoolean(subTokenizer.nextToken()));
                pilot.setExplosion(Boolean.parseBoolean(subTokenizer.nextToken()));
            }

            pilot.getSkills().add(skill);
		}
		
		//read the kills, if any
		if (subTokenizer.hasMoreElements())
			pilot.setKills(Integer.parseInt(subTokenizer.nextToken()));

		//all done. whoopdie doo.
		return pilot;
	}
	
	/**
	 * Method to add a pilot to the client side queue. This discrete
	 * update saves bandwidth by allowing a single pilot (instead of
	 * the whole queue, as was done in the past) to be sent down when
	 * a game ends w/ a dispossessed pilot, a new pilot is hired, etc.
	 * 
	 * Format: PL|AP2PPQ|Unit Type|Unit Weight Class|Pilot Data
	 */
	public void addPilot(StringTokenizer ST){
        try {
            int pilotType = Integer.parseInt(ST.nextToken());
            int pilotClass = Integer.parseInt(ST.nextToken());
            Pilot pilot = getPilotFromString(ST.nextToken());
            
            this.getUnitTypeQueue(pilotType).get(pilotClass).addLast(pilot);
        } catch(Exception ex) {
            MWClient.mwClientLog.clientErrLog("Error while adding pilot to PPQ");
            MWClient.mwClientLog.clientErrLog(ex);
        }
        
    }
	
	/**
	 * Method that removes a specific pilot from the PPQ. This
	 * discrete update saves bandwidth by eliminating the need
	 * to send the entire hangar to the player when a pilot is
	 * removed.
	 * 
	 * Format: PL|RPPPQ|Unit Type|Unit Weight|Position
	 */
	public void removePilot(StringTokenizer ST){
        
		try{
            int pilotType = Integer.parseInt(ST.nextToken());
            int pilotClass = Integer.parseInt(ST.nextToken());
            int pilotPosition = Integer.parseInt(ST.nextToken());
            
            this.getUnitTypeQueue(pilotType).get(pilotClass).remove(pilotPosition);
        } catch(Exception ex){
            MWClient.mwClientLog.clientErrLog("Unable to remove pilot form queue");
            MWClient.mwClientLog.clientErrLog(ex);
        }
    }
	
	/**
	 * Method that returns a particular class/size queue. Used
	 * throughout the client code to fecth queue, which are then
	 * iterated in order to draw menus, dialog boxes, etc.
	 * 
	 * Because these queues are always created in the constructor,
	 * they will never be null, even if a LIGHTONLY option for vehs
	 * or infantry is enabled.
	 */
	public LinkedList getPilotQueue(int unitType, int weightClass) {
		return this.getUnitTypeQueue(unitType).get(weightClass);
	}
	
	/**
	 * Convert a server-generated String into usedful data - actual
	 * pilots, in proper type and class-based LinkedLists.
	 * 
	 * NOTE: String send by the server is generated in SPPQueues.java,
	 *       and delimited with $'s (main) and #'s (subtokens).
	 */
	public void fromString(String stringFromServer){
		
		StringTokenizer mainTokenizer = new StringTokenizer(stringFromServer,"$");
		
		//first, clear all existing pilots from the linked lists
		for (LinkedList<Pilot> currList : mekPilots)
			currList.clear();
		for (LinkedList<Pilot> currList : protoPilots)
			currList.clear();
		
		//loop once to read in meks (light -> assault lists)
		for (int weightClass = Unit.LIGHT; weightClass <= Unit.ASSAULT; weightClass++) {
			
			int listSize = Integer.parseInt(mainTokenizer.nextToken());
			for (int count = 0 ; count < listSize; count++) {
				Pilot toAdd = this.getPilotFromString(mainTokenizer.nextToken());
				this.getUnitTypeQueue(Unit.MEK).get(weightClass).addLast(toAdd);
			}
		}
		
		//loop a second time to read in protomeks (light -> assault lists)
		for (int weightClass = Unit.LIGHT; weightClass <= Unit.ASSAULT; weightClass++) {
			
			int listSize = Integer.parseInt(mainTokenizer.nextToken());
			for (int count = 0 ; count < listSize; count++) {
				Pilot toAdd = this.getPilotFromString(mainTokenizer.nextToken());
				this.getUnitTypeQueue(Unit.PROTOMEK).get(weightClass).addLast(toAdd);
			}
		}
	}
	
}//end CPPQ