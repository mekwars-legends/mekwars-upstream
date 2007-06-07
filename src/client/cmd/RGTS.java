/*
 * MekWars - Copyright (C) 2004 
 * 
 * Derived from MegaMekNET (http://www.sourceforge.net/projects/megamek)
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

package client.cmd;

import java.util.Enumeration;
import java.util.StringTokenizer;

import client.MWClient;

import common.Unit;
import common.util.UnitUtils;

import megamek.common.BattleArmor;
import megamek.common.BipedMech;
import megamek.common.CriticalSlot;
import megamek.common.Entity;
import megamek.common.IEntityRemovalConditions;
import megamek.common.Mech;
import megamek.common.MechWarrior;
import megamek.common.Player;
import megamek.common.Protomech;
import megamek.common.QuadMech;
import megamek.common.Tank;
import megamek.server.Server;

/**
 * @author Imi (immanuel.scholz@gmx.de)
 */
public class RGTS extends Command {
	
	private Server server = null;
	/**
	 * @param client
	 */
	public RGTS(MWClient mwclient) {
		super(mwclient);
	}
	
	/**
	 * @see server.cmd.Command#execute(java.lang.String)
	 */
	@Override
	public void execute(String input) {
		
		//MWClient.mwClientLog.clientErrLog("Inside RGTS");
		server = mwclient.getMyServer();
		StringBuilder result = new StringBuilder();
		String name = "";
		//Parse the real playername from the Modified In game one..
		String winnerName = "";
		if ( server.getGame().getVictoryTeam() != 0 ) {
			
			int numberOfWinners = 0;
			//Multiple Winners
			Enumeration<Player> en = server.getGame().getPlayers();
			while (en.hasMoreElements()) {
				Player p = en.nextElement();
				if (p.getTeam() == server.getGame().getVictoryTeam()) {
					StringTokenizer st = new StringTokenizer(p.getName().trim(), "~");
					name = "";
					while (st.hasMoreElements()) {
						name = st.nextToken().trim();
					}
					numberOfWinners++;
					
					winnerName += name;
					//some of the players set themselves as a team of 1. 
					//This keeps that from happening.
					if ( numberOfWinners > 0 )
						winnerName += "*";
				}
			}
			winnerName += "#";
		} 
		
		//Only one winner
		else {
			if (server.getGame().getVictoryPlayerId() == Player.PLAYER_NONE) {
				winnerName = "DRAW#";
			} else {
				winnerName = server.getGame().getPlayer(server.getGame().getVictoryPlayerId()).getName();
				StringTokenizer st = new StringTokenizer(winnerName, "~");
				name = "";
				while (st.hasMoreElements())
					name = st.nextToken().trim();
				winnerName = name + "#";
			}
		}
		
		result.append(winnerName);
		
		//Report the mech stat
		Enumeration<Entity> en = server.getGame().getDevastatedEntities();
		while (en.hasMoreElements()) {
			Entity ent = en.nextElement();
			if ( ent.getOwner().getName().startsWith("War Bot"))
				continue;
			result.append(this.serializeEntity(ent, true, false) + "#");
		}
		en = server.getGame().getGraveyardEntities();
		while (en.hasMoreElements()) {
			Entity ent = en.nextElement();
			if ( ent.getOwner().getName().startsWith("War Bot"))
				continue;
			if (ent instanceof Mech && ent.getInternal(Mech.LOC_CT) <= 0)
				result.append(this.serializeEntity(ent, true, true) + "#");              
			else
				result.append(this.serializeEntity(ent, true, false) + "#");
			
		}
		en = server.getGame().getEntities();
		while (en.hasMoreElements()) {
			Entity ent = en.nextElement();
			if ( ent.getOwner().getName().startsWith("War Bot"))
				continue;
			if (ent instanceof Mech && ent.getInternal(Mech.LOC_CT) <= 0)
				result.append(this.serializeEntity(ent, true, true) + "#");          
			else
				result.append(this.serializeEntity(ent, true, false) + "#");
		}
		en = server.getGame().getRetreatedEntities();
		while (en.hasMoreElements()) {
			Entity ent = en.nextElement();
			if ( ent.getOwner().getName().startsWith("War Bot"))
				continue;
			if (ent instanceof Mech && ent.getInternal(Mech.LOC_CT) <= 0)
				result.append(this.serializeEntity(ent, true, true) + "#");          
			else
				result.append(this.serializeEntity(ent, true, false) + "#");
		}
		
		if ( mwclient.getBuildingTemplate()!= null )
			result.append("BL*"+this.getBuildingsLeft());
		MWClient.mwClientLog.clientOutputLog("CR|" + result);
		
		//send the autoreport
		mwclient.serverSend("CR|" + result.toString());
		
		//we may assume that a server which reports a game is no longer "Running"
		mwclient.serverSend("SHS|" + mwclient.myUsername + "|Open");
	}
	
	public String serializeEntity (Entity e, boolean fullStatus, boolean forceDevastate) {
		
		StringBuilder result = new StringBuilder();
		boolean useRepairs = mwclient.isUsingAdvanceRepairs();
		
		if (fullStatus) {
			if ( !(e instanceof MechWarrior))
			{
				result.append(e.getExternalId() + "*");
				result.append(e.getOwner().getName() + "*");
				result.append(e.getCrew().getHits() + "*");
				
				if (forceDevastate)
					result.append(IEntityRemovalConditions.REMOVE_DEVASTATED + "*");
				else
					result.append(e.getRemovalCondition() + "*");
				
				if ( e instanceof BipedMech )
					result.append(Unit.MEK +"*");
				else if ( e instanceof QuadMech )
					result.append(Unit.QUAD + "*");
				else if ( e instanceof Tank)
					result.append(Unit.VEHICLE +"*");
				else if ( e instanceof Protomech)
					result.append(Unit.PROTOMEK +"*");
				else if ( e instanceof BattleArmor )
					result.append(Unit.BATTLEARMOR+"*");
				else
					result.append(Unit.INFANTRY +"*");
				//result.append(e.getMovementType() + "*"); bad code
				//Collect kills
				Enumeration<Entity> en = e.getKills();
				//No kills? Add an empty space
				if (!en.hasMoreElements())
					result.append(" *");
				while (en.hasMoreElements()) {
					Entity kill = en.nextElement();
					result.append(kill.getExternalId());
					if (en.hasMoreElements())
						result.append("~");
					else
						result.append("*");
				}
			}
			
			if (e instanceof Mech ) {
				result.append(e.getCrew().isUnconscious() + "*");
				result.append(e.getInternal(Mech.LOC_CT) + "*");
				result.append(e.getInternal(Mech.LOC_HEAD) + "*");
				result.append(e.getInternal(Mech.LOC_LLEG) + "*");
				result.append(e.getInternal(Mech.LOC_RLEG) + "*");
				result.append(e.getInternal(Mech.LOC_LARM) + "*");
				result.append(e.getInternal(Mech.LOC_RARM) + "*");
				result.append(e.getBadCriticals(CriticalSlot.TYPE_SYSTEM, Mech.SYSTEM_GYRO, Mech.LOC_CT) + "*");
				result.append(((Mech)e).getCockpitType()+"*");
				if ( useRepairs ){
					result.append(UnitUtils.unitBattleDamage(e)+"*");
				}
			} else if (e instanceof Tank ) {
				result.append(e.isRepairable() + "*");
				result.append(e.isImmobile() + "*");
				result.append(e.getCrew().isDead() + "*");
				if ( useRepairs ){
					result.append(UnitUtils.unitBattleDamage(e)+"*");
				}
			}
			else if (e instanceof MechWarrior) {
				MechWarrior mw = (MechWarrior)e;
				result.append("MW*");
				result.append(mw.getOriginalRideExternalId() + "*");
				result.append(mw.getPickedUpByExternalId() + "*");
				result.append(mw.isDestroyed()+"*");
			}
			
			if (  e.isOffBoard() ){
				result.append(e.getOffBoardDistance()+"*");
			}
		}
		
		/*
		 * FullStatus is used when autoreporting. This status, which 
		 * sends less information, is used for InProgressUpdates.
		 */
		else {
			//if the entity is a mechwarrior, send an IPU command
			//(InProgressUpdate) to the server.
			if (e instanceof MechWarrior) {
				MechWarrior mw = (MechWarrior)e;
				result.append("MW*" + mw.getOriginalRideExternalId() + "*");
				result.append(mw.getPickedUpByExternalId() + "*");
				result.append(mw.isDestroyed()+"*");
			} 
			
			//else (the entity is a real unit)
			else {
				result.append(e.getOwner().getName() + "*");
				result.append(e.getExternalId() + "*");
				
				if (forceDevastate)
					result.append(IEntityRemovalConditions.REMOVE_DEVASTATED + "*");
				else
					result.append(e.getRemovalCondition() + "*");
				
				if (e instanceof Mech ) {
					result.append(e.getInternal(Mech.LOC_CT) + "*");
					result.append(e.getInternal(Mech.LOC_HEAD) + "*");
				} else {
					result.append("1*");
					result.append("1*");
				}
				result.append(e.isRepairable() + "*");
			}
		}//end else(un-full status)
		
		return result.toString();
	}
	
	
	public int getBuildingsLeft(){
		Enumeration buildings = server.getGame().getBoard().getBuildings();
		int buildingCount = 0;
		while ( buildings.hasMoreElements() ){
			buildings.nextElement();
			buildingCount++;
		}
		return buildingCount;
	}
	
}
