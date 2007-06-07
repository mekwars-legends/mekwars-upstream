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

package server.campaign.commands;

import java.util.Enumeration;
import java.util.StringTokenizer;

import server.campaign.CampaignMain;
import server.campaign.SPlayer;
import server.campaign.SArmy;

public class CheckAttackCommand implements Command {
	
	
	int accessLevel = 0;
	public int getExecutionLevel(){return accessLevel;}
	public void setExecutionLevel(int i) {accessLevel = i;}
	
	public void process(StringTokenizer command,String Username) {
		
		if (accessLevel != 0) {
			int userLevel = CampaignMain.cm.getServer().getUserLevel(Username);
			if(userLevel < getExecutionLevel()) {
				CampaignMain.cm.toUser("Insufficient access level for command. Level: " + userLevel + ". Required: " + accessLevel + ".",Username,true);
				return;
			}
		}
		
		SPlayer p = CampaignMain.cm.getPlayer(Username);
		
		//break if player isnt active
		boolean canProbeFromReserve = CampaignMain.cm.getBooleanConfig("ProbeInReserve");
		if(!canProbeFromReserve && p.getDutyStatus() < SPlayer.STATUS_ACTIVE){
			CampaignMain.cm.toUser("You are not on the frontline. You can't probe enemy forces from reserve!", Username, true);
			return;
		}
		
		//don't allow fighting players to /c ca spot for their comrades
		if(p.getDutyStatus() == SPlayer.STATUS_FIGHTING) {
			CampaignMain.cm.toUser("You should focus on playing your game!", Username, true);
			return;
		}
		
		//if not fighting, check to make sure minactivetime is met
		boolean minActiveMet = System.currentTimeMillis() - p.getActiveSince() >= Long.parseLong(CampaignMain.cm.getConfig("MinActiveTime")) * 1000;
		if (!canProbeFromReserve && !minActiveMet) {
			CampaignMain.cm.toUser("You're still on your way to the frontline. Contact an intelligence officer once you arrive at your post.",Username,true);
			return;
		}
		
		//don't allow uncontracted mercs to /c ca spot for their friends
		if (p.getMyHouse().isMercHouse() && p.getHouseFightingFor() == p.getMyHouse()) {
			CampaignMain.cm.toUser("You are not under contract!", Username, true);
			return;
		}
		
		//All the tosses are passed, so check to see if the return should use normal or Operations BVs.
		boolean usingOpRules = CampaignMain.cm.getBooleanConfig("UseOperationsRule");
		
		String Desc = "<br>";//output
		
		//if command has more elements, spit out checkattack for specific armies. no real reason for
		//a user to do this through the CLI, but it is called by right clicking armies in HQ
		if (command.hasMoreElements()) {
			Desc = "<br><font color=\"black\">Army "; 
			
			int armyID = -1;
			try {
				armyID = Integer.parseInt(command.nextToken());
			} catch (Exception e) {
				CampaignMain.cm.toUser("Improper format. Try: /c checkattack or /c checkattack#armyid", Username, true);
				return;
			}
			
			SArmy arm = p.getArmy(armyID);
			if (arm == null) {
				CampaignMain.cm.toUser("Army #" + armyID + " doesn't exist.", Username, true);
				return;
			}
			
			Desc += arm.getID() + " (" + arm.getBV()+ " BV) ";
			Desc += " may attack: ";
			
			Enumeration targets = arm.getOpponents().elements();
			while (targets.hasMoreElements()) {
				SArmy currTarget = (SArmy)targets.nextElement();
				SPlayer currTargetP = CampaignMain.cm.getPlayer(currTarget.getPlayerName());
				String coloredHouseName = currTargetP.getMyHouse().getHouseFightingFor(currTargetP).getColoredName();
				
				//adjust return for infantry settings
				if(new Boolean(CampaignMain.cm.getConfig("ShowInfInCheckAttack")).booleanValue())
					Desc += coloredHouseName + "(" + currTarget.getAmountOfUnits() + ")";
				else 
					Desc += coloredHouseName + "(" + currTarget.getAmountOfUnitsWithoutInfantry() + ")";
					
				if (targets.hasMoreElements())
					Desc += ", ";
			}//end while(more targets)
		}//end (if for a specific army)
		
		//otherwise, loop out *all* the armies
		else {
			Desc = "<font color=\"black\">Intelligence reports the following attack options:<br>";
			Enumeration e = p.getArmies().elements();
			while (e.hasMoreElements()) {
				SArmy arm = (SArmy)e.nextElement();
				if (arm != null) {
					Desc += "Army " + arm.getID() ;
					if ( usingOpRules )
						Desc += " (" + arm.getBV() + " BV)";
					Desc += ": ";
					
					Enumeration targets = arm.getOpponents().elements();
					while (targets.hasMoreElements()) {
						SArmy currTarget = (SArmy)targets.nextElement();
						SPlayer currTargetP = CampaignMain.cm.getPlayer(currTarget.getPlayerName());
                        if ( currTargetP == null )
                            continue;
						String coloredHouseName = currTargetP.getMyHouse().getHouseFightingFor(currTargetP).getColoredName();
						Desc += coloredHouseName + "(" + currTarget.getAmountOfUnits() + ")";

						if ( usingOpRules ) {
							if (  arm.getAmountOfUnits() > currTarget.getAmountOfUnits() )
								Desc += "(BV Against: "+ arm.getOperationsBV(currTarget) +")";
							else
								Desc += "(BV Against: "+ arm.getBV() +")";
						}
						if (targets.hasMoreElements())
							Desc += ", ";
					}//end while(more targets)
					Desc += "<br>";
				}
			}
		}

		CampaignMain.cm.toUser(Desc + "</font><br>",Username,true);

	}
	
}//end CheckAttackCommand