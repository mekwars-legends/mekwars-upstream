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

package client.util;

import java.util.Enumeration;

import megamek.common.Aero;
import megamek.common.BattleArmor;
import megamek.common.BipedMech;
import megamek.common.CriticalSlot;
import megamek.common.Entity;
import megamek.common.IEntityRemovalConditions;
import megamek.common.Mech;
import megamek.common.MechWarrior;
import megamek.common.Protomech;
import megamek.common.QuadMech;
import megamek.common.Tank;

import common.Unit;
import common.util.UnitUtils;

public class SerializeEntity{
	public static String serializeEntity (Entity e, boolean fullStatus, boolean forceDevastate, boolean useRepairs) {
		
		StringBuilder result = new StringBuilder();

		if (fullStatus) {
			if ( !(e instanceof MechWarrior))
			{
				result.append(e.getExternalId() + "*");
				result.append(e.getOwner().getName().trim() + "*");
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
                else if ( e instanceof Aero )
                    result.append(Unit.AERO+"*");
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
	            result.append(UnitUtils.getEntityFileName(e));
			} else if (e instanceof Tank ) {
				result.append(e.isRepairable() + "*");
				result.append(e.isImmobile() + "*");
				result.append(e.getCrew().isDead() + "*");
				if ( useRepairs ){
					result.append(UnitUtils.unitBattleDamage(e)+"*");
				}
	            result.append(UnitUtils.getEntityFileName(e));
			}else if (e instanceof Aero ) {
                result.append(e.isRepairable() + "*");
                result.append(e.isImmobile() + "*");
                result.append(e.getCrew().isDead() + "*");
                result.append(UnitUtils.getEntityFileName(e));
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
		} //end else(un-full status)
		
		return result.toString();
	}
}