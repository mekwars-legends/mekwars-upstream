/*
 * MekWars - Copyright (C) 2005 
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


/**
 * 
 * @author Torren (Jason Tighe) 11.9.05 
 * Main Class used to communicate with a Cyclops RPC Server
 * 
 * With Lots of help from Guibod
 * http://muposerver.dyndns.org/devel/cyclops
 * 
 */

/*
 * Thanks to www.koders.com
 * for all the ideas.
 * 
 */

package server.mwcyclopscomm;

import java.util.LinkedList;

import common.Unit;
import common.campaign.pilot.Pilot;
import common.campaign.pilot.skills.PilotSkill;
import common.util.MD5;

import common.CampaignData;
import server.campaign.SPersonalPilotQueues;
import server.campaign.pilot.SPilot;
import server.campaign.pilot.skills.SPilotSkill;

public class  MWCyclopsPilot{
	
	public static String pilotWrite(SPilot pilot, String player){
		StringBuilder message = new StringBuilder();
		
		try{
			message.append(MWCyclopsUtils.methodCallStart());
			message.append(MWCyclopsUtils.methodName("pilot.write"));
			
			message.append(MWCyclopsUtils.paramsStart());
			message.append(MWCyclopsUtils.paramStart());
			message.append(MWCyclopsUtils.valueStart());
			
			message.append(createPilotStruct(pilot,player));
			
			message.append(MWCyclopsUtils.valueEnd());
			message.append(MWCyclopsUtils.paramEnd());
			message.append(MWCyclopsUtils.paramsEnd());
			
			message.append(MWCyclopsUtils.methodCallEnd());
		}catch(Exception ex){
			CampaignData.mwlog.errLog(ex);
		}
		
		return message.toString();
	}
	
	public static String pilotWriteFromList(SPersonalPilotQueues pilots,String player){
		
		StringBuilder message = new StringBuilder();
		
		try{
			message.append(MWCyclopsUtils.methodCallStart());
			message.append(MWCyclopsUtils.methodName("pilot.writeFromList"));
			
			message.append(MWCyclopsUtils.paramsStart());
			message.append(MWCyclopsUtils.paramStart());
			message.append(MWCyclopsUtils.valueStart());
			message.append(MWCyclopsUtils.arrayStart());
			message.append(MWCyclopsUtils.dataStart());
			
			for ( int type = 0; type < 2; type++)
				for ( int weight = 0; weight <= Unit.ASSAULT; weight++ ){
					LinkedList<Pilot> list = pilots.getPilotQueue(type,weight);
					for (Pilot p : list) {
						SPilot pilot = (SPilot)p;//we know this is a safe cast ...
						if ( !pilot.getName().equalsIgnoreCase("vacant") && pilot.getGunnery() != 99 && pilot.getPiloting() != 99 )
							message.append(MWCyclopsUtils.value(createPilotStruct(pilot,player)));
					}
				}
			
			message.append(MWCyclopsUtils.dataEnd());
			message.append(MWCyclopsUtils.arrayEnd());
			
			message.append(MWCyclopsUtils.valueEnd());
			message.append(MWCyclopsUtils.paramEnd());
			message.append(MWCyclopsUtils.paramsEnd());
			
			message.append(MWCyclopsUtils.methodCallEnd());
		}catch(Exception ex){
			CampaignData.mwlog.errLog(ex);
		}
		
		return message.toString();
	}
	
	public static String pilotKill(SPilot pilot, String opID){
		StringBuilder message = new StringBuilder();
		
		try{
			message.append(MWCyclopsUtils.methodCallStart());
			message.append(MWCyclopsUtils.methodName("pilot.kill"));
			
			message.append(MWCyclopsUtils.paramsStart());
			message.append(MWCyclopsUtils.paramStart());
			message.append(MWCyclopsUtils.valueStart());
			
			message.append(createPilotKillStruct(pilot,opID));
			
			message.append(MWCyclopsUtils.valueEnd());
			message.append(MWCyclopsUtils.paramEnd());
			message.append(MWCyclopsUtils.paramsEnd());
			
			message.append(MWCyclopsUtils.methodCallEnd());
		}catch(Exception ex){
			CampaignData.mwlog.errLog(ex);
		}
		
		return message.toString();
	}
	
	
	public static String pilotRetire(SPilot pilot){
		StringBuilder message = new StringBuilder();
		
		try{
			message.append(MWCyclopsUtils.methodCallStart());
			message.append(MWCyclopsUtils.methodName("pilot.retire"));
			
			message.append(MWCyclopsUtils.paramsStart());
			message.append(MWCyclopsUtils.paramStart());
			message.append(MWCyclopsUtils.valueStart());
			
			message.append(createPilotRetireStruct(pilot));
			
			message.append(MWCyclopsUtils.valueEnd());
			message.append(MWCyclopsUtils.paramEnd());
			message.append(MWCyclopsUtils.paramsEnd());
			
			message.append(MWCyclopsUtils.methodCallEnd());
		}catch(Exception ex){
			CampaignData.mwlog.errLog(ex);
		}
		
		return message.toString();
	}
	
	public static String createPilotStruct(SPilot pilot, String player){
		StringBuilder struct = new StringBuilder();
		
		struct.append(MWCyclopsUtils.structStart());
		
		struct.append(MWCyclopsUtils.structMember("id",pilot.getPilotId()));
		struct.append(MWCyclopsUtils.structMember("faction",MD5.getHashString(pilot.getCurrentFaction())));
		struct.append(MWCyclopsUtils.structMember("player",MD5.getHashString(player.toLowerCase())));
		struct.append(MWCyclopsUtils.structMember("piloting",pilot.getPiloting()));
		struct.append(MWCyclopsUtils.structMember("gunnery",pilot.getGunnery()));
		struct.append(MWCyclopsUtils.structMember("desc",pilot.getName()));
		struct.append(MWCyclopsUtils.structMember("bodycount",pilot.getKills()));
		
		if ( pilot.getSkills().size() > 0 ){
			StringBuilder skillArray = new StringBuilder();
			skillArray.append(MWCyclopsUtils.arrayStart());
			skillArray.append(MWCyclopsUtils.dataStart());
			
			for (PilotSkill skills : pilot.getSkills().getPilotSkills()){
				
				SPilotSkill skill = (SPilotSkill)skills;
				
				skillArray.append(MWCyclopsUtils.value(createPilotSkillStruct(skill)));
			}
			
			skillArray.append(MWCyclopsUtils.dataEnd());
			skillArray.append(MWCyclopsUtils.arrayEnd());
			
			struct.append(MWCyclopsUtils.structMember("skills",skillArray.toString()));
			
		}
		struct.append(MWCyclopsUtils.structEnd());
		
		return struct.toString();
	}
	
	public static String createPilotSkillStruct(SPilotSkill skill){
		StringBuilder skillStruct = new StringBuilder(MWCyclopsUtils.structStart());
		
		skillStruct.append(MWCyclopsUtils.structMember("skill",skill.getId()));
		skillStruct.append(MWCyclopsUtils.structMember("level",Math.max(skill.getLevel(),1)));
		
		skillStruct.append(MWCyclopsUtils.structEnd());
		
		return skillStruct.toString();
	}
	
	public static String createPilotKillStruct(SPilot pilot, String opID){
		StringBuilder struct = new StringBuilder();
		
		struct.append(MWCyclopsUtils.structStart());
		
		struct.append(MWCyclopsUtils.structMember("id",pilot.getPilotId()));
		struct.append(MWCyclopsUtils.structMember("op",opID));
		
		struct.append(MWCyclopsUtils.structEnd());
		
		return struct.toString();
	}
	
	public static String createPilotRetireStruct(SPilot pilot){
		StringBuilder struct = new StringBuilder();
		
		struct.append(MWCyclopsUtils.structStart());
		
		struct.append(MWCyclopsUtils.structMember("id",pilot.getPilotId()));
		
		struct.append(MWCyclopsUtils.structEnd());
		
		return struct.toString();
	}
	
}
