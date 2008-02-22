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

import java.util.Hashtable;

import common.CampaignData;
import server.campaign.pilot.skills.SPilotSkill;
 /**
  * Pilot skllls used by MW server sent to
  * Cyclops
  * @author Torren Nov 11, 2005
  *
  */
 public class  MWCyclopsSkill{
     
     public static String skillWrite(SPilotSkill skill){

         String message = "";

         try{
             message += MWCyclopsUtils.methodCallStart();
             message += MWCyclopsUtils.methodName("skill.write");
             
             message += MWCyclopsUtils.paramsStart();
             message += MWCyclopsUtils.paramStart();
             message += MWCyclopsUtils.valueStart();
             
             message += createSkillStruct(skill); 
                 
             message += MWCyclopsUtils.valueEnd();
             message += MWCyclopsUtils.paramEnd();
             message += MWCyclopsUtils.paramsEnd();
             
             message += MWCyclopsUtils.methodCallEnd();
         }catch(Exception ex){
             CampaignData.mwlog.errLog(ex);
         }
         
         return message;
     }
 
     public static String skillWriteFromList(Hashtable<Integer,SPilotSkill> pilotSkills){

         String message = "";

         try{
             message += MWCyclopsUtils.methodCallStart();
             message += MWCyclopsUtils.methodName("skill.writeFromList");
             
             message += MWCyclopsUtils.paramsStart();
             message += MWCyclopsUtils.paramStart();
             message += MWCyclopsUtils.valueStart();
             
             message += MWCyclopsUtils.arrayStart();
             message += MWCyclopsUtils.dataStart();
             
             for ( SPilotSkill skill : pilotSkills.values() ){
                 message += MWCyclopsUtils.value(createSkillStruct(skill)); 
             }
             
             message += MWCyclopsUtils.dataEnd();
             message += MWCyclopsUtils.arrayEnd();
             message += MWCyclopsUtils.valueEnd();
             message += MWCyclopsUtils.paramEnd();
             message += MWCyclopsUtils.paramsEnd();
             
             message += MWCyclopsUtils.methodCallEnd();
         }catch(Exception ex){
             CampaignData.mwlog.errLog(ex);
         }
         
         return message;
     }
 
     private static String createSkillStruct(SPilotSkill skill){
         String struct = "";
         
         struct += MWCyclopsUtils.structStart();
         struct += MWCyclopsUtils.structMember("id",skill.getId());
         struct += MWCyclopsUtils.structMember("code",skill.getAbbreviation());
         struct += MWCyclopsUtils.structMember("desc",skill.getDescription());
         struct += MWCyclopsUtils.structEnd();
         
         return struct;
     }
}
 