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

import java.util.Vector;

import megamek.common.TechConstants;

import common.Unit;
import common.util.MD5;

import common.CampaignData;
import server.campaign.SUnit;
 
 public class  MWCyclopsUnitTemplate{
 
     public static String unitTemplateWrite(SUnit unit){
         StringBuilder message = new StringBuilder();

         try{
             message.append(MWCyclopsUtils.methodCallStart());
             message.append(MWCyclopsUtils.methodName("unitTemplate.write"));
             
             message.append(MWCyclopsUtils.paramsStart());
             message.append(MWCyclopsUtils.paramStart());
             message.append(MWCyclopsUtils.valueStart());

             message.append(createUnitTemplateStruct(unit));
             
             message.append(MWCyclopsUtils.valueEnd());
             message.append(MWCyclopsUtils.paramEnd());
             message.append(MWCyclopsUtils.paramsEnd());
             
             message.append(MWCyclopsUtils.methodCallEnd());
         }catch(Exception ex){
             CampaignData.mwlog.errLog(ex);
         }
 
         return message.toString();
     }
     
     public static String unitTemplateWriteFromList(Vector<SUnit> units){
         StringBuilder message = new StringBuilder();

         try{
             message.append(MWCyclopsUtils.methodCallStart());
             message.append(MWCyclopsUtils.methodName("unitTemplate.writeFromList"));
             
             message.append(MWCyclopsUtils.paramsStart());
             message.append(MWCyclopsUtils.paramStart());
             message.append(MWCyclopsUtils.valueStart());
             
             message.append(MWCyclopsUtils.arrayStart());
             message.append(MWCyclopsUtils.dataStart());
             
             for ( SUnit unit: units ){
                 message.append(MWCyclopsUtils.value(createUnitTemplateStruct(unit)));
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
     
     public static String createUnitTemplateStruct(SUnit unit){
         StringBuilder struct = new StringBuilder();
         
         struct.append(MWCyclopsUtils.structStart());
         
         struct.append(MWCyclopsUtils.structMember("id",MD5.getHashString(unit.getUnitFilename())));
         struct.append(MWCyclopsUtils.structMember("filename",unit.getUnitFilename()));
         struct.append(MWCyclopsUtils.structMember("name",unit.getEntity().getShortNameRaw()));
         struct.append(MWCyclopsUtils.structMember("chassis",unit.getEntity().getChassis()));
         struct.append(MWCyclopsUtils.structMember("model",unit.getEntity().getModel()));
         struct.append(MWCyclopsUtils.structMember("tonnage",(int)unit.getEntity().getWeight()));
         struct.append(MWCyclopsUtils.structMember("battlevalue",unit.getEntity().calculateBattleValue()));
         struct.append(MWCyclopsUtils.structMember("class",Unit.getWeightClassDesc(unit.getWeightclass())));
         struct.append(MWCyclopsUtils.structMember("category",Unit.getTypeClassDesc(unit.getType())));
         struct.append(MWCyclopsUtils.structMember("era",unit.getEntity().getYear()));
         struct.append(MWCyclopsUtils.structMember("techlevel",TechConstants.T_NAMES[unit.getEntity().getTechLevel()].replaceAll("_"," ")));

         struct.append(MWCyclopsUtils.structEnd());
         
         return struct.toString();
     }
}
 