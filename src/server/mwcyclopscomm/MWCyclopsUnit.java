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

import java.util.List;

import common.util.MD5;

import server.MWServ;
import server.campaign.SUnit;
 
/**
 * Cyclops method for units. Write WriteFromList
 * @author Torren Nov 13, 2005
 *
 */
 public class  MWCyclopsUnit{
     
     public static String unitWrite(SUnit unit, String Player, String House){
         StringBuilder message = new StringBuilder();
         
         try{
             message.append(MWCyclopsUtils.methodCallStart());
             message.append(MWCyclopsUtils.methodName("unit.write"));
             
             message.append(MWCyclopsUtils.paramsStart());
             message.append(MWCyclopsUtils.paramStart());
             message.append(MWCyclopsUtils.valueStart());
             
             message.append(createUnitStruct(unit,Player,House));
             
             message.append(MWCyclopsUtils.valueEnd());
             message.append(MWCyclopsUtils.paramEnd());
             message.append(MWCyclopsUtils.paramsEnd());
             
             message.append(MWCyclopsUtils.methodCallEnd());
         }catch(Exception ex){
             MWServ.mwlog.errLog(ex);
         }

         
         return message.toString();
     }
     
     public static String unitWriteFromList(List<SUnit> units,String Player,String House){
         StringBuilder message = new StringBuilder();
         
         try{
             message.append(MWCyclopsUtils.methodCallStart());
             message.append(MWCyclopsUtils.methodName("unit.writeFromList"));
             
             message.append(MWCyclopsUtils.paramsStart());
             message.append(MWCyclopsUtils.paramStart());
             message.append(MWCyclopsUtils.valueStart());
             message.append(MWCyclopsUtils.arrayStart());
             message.append(MWCyclopsUtils.dataStart());
             
             for (SUnit unit : units ){
                 message.append(MWCyclopsUtils.value(createUnitStruct(unit,Player,House)));
             }
             
             message.append(MWCyclopsUtils.dataEnd());
             message.append(MWCyclopsUtils.arrayEnd());
             
             message.append(MWCyclopsUtils.valueEnd());
             message.append(MWCyclopsUtils.paramEnd());
             message.append(MWCyclopsUtils.paramsEnd());
             
             message.append(MWCyclopsUtils.methodCallEnd());
         }catch(Exception ex){
             MWServ.mwlog.errLog(ex);
         }

         return message.toString();
     }
     
     public static String unitDestroy(String unitid, String reason, String opid, String destroyingPlayer, String destroyingUnit){
         StringBuilder message = new StringBuilder();
         
         try{
             message.append(MWCyclopsUtils.methodCallStart());
             message.append(MWCyclopsUtils.methodName("unit.destroy"));
             
             message.append(MWCyclopsUtils.paramsStart());
             message.append(MWCyclopsUtils.paramStart());

             message.append(MWCyclopsUtils.value(unitid));
             message.append(MWCyclopsUtils.value(reason));
             message.append(MWCyclopsUtils.value(opid));
             message.append(MWCyclopsUtils.value(destroyingPlayer));
             message.append(MWCyclopsUtils.value(destroyingUnit));
             
             message.append(MWCyclopsUtils.paramEnd());
             message.append(MWCyclopsUtils.paramsEnd());
             
             message.append(MWCyclopsUtils.methodCallEnd());
         }catch(Exception ex){
             MWServ.mwlog.errLog(ex);
         }

         
         return message.toString();
     }

     public static String unitChangeOwnership(String unitID, String Player, String House, String opID, String reason){
         StringBuilder message = new StringBuilder();
         
         try{
             message.append(MWCyclopsUtils.methodCallStart());
             message.append(MWCyclopsUtils.methodName("unit.changeOwnership"));
             
             message.append(MWCyclopsUtils.paramsStart());
             message.append(MWCyclopsUtils.paramStart());
             message.append(MWCyclopsUtils.valueStart());
             
             message.append(createUnitOwnershipStruct(unitID,Player,House,opID,reason));
             
             message.append(MWCyclopsUtils.valueEnd());
             message.append(MWCyclopsUtils.paramEnd());
             message.append(MWCyclopsUtils.paramsEnd());
             
             message.append(MWCyclopsUtils.methodCallEnd());
         }catch(Exception ex){
             MWServ.mwlog.errLog(ex);
         }

         
         return message.toString();
     }

     public static String createUnitStruct(SUnit unit,String Player, String House){
                  
         StringBuilder struct = new StringBuilder();
         
         struct.append(MWCyclopsUtils.structStart());
         
         struct.append(MWCyclopsUtils.structMember("id",unit.getId()));
         struct.append(MWCyclopsUtils.structMember("template",MD5.getHashString(unit.getUnitFilename())));
         struct.append(MWCyclopsUtils.structMember("buildplace",unit.getProducer()));
         struct.append(MWCyclopsUtils.structMember("buildfaction",MD5.getHashString(House)));
         struct.append(MWCyclopsUtils.structMember("player",MD5.getHashString(Player.toLowerCase())));
         struct.append(MWCyclopsUtils.structMember("faction",MD5.getHashString(House)));

         struct.append(MWCyclopsUtils.structEnd());

         return struct.toString();
     }

     public static String createUnitOwnershipStruct(String unitid,String Player, String House, String opID, String reason){
         StringBuilder struct = new StringBuilder();
         
         struct.append(MWCyclopsUtils.structStart());
         
         struct.append(MWCyclopsUtils.structMember("id",unitid));
         struct.append(MWCyclopsUtils.structMember("type",reason));
         struct.append(MWCyclopsUtils.structMember("player",MD5.getHashString(Player.toLowerCase())));
         struct.append(MWCyclopsUtils.structMember("faction",MD5.getHashString(House)));
         struct.append(MWCyclopsUtils.structMember("op",MD5.getHashString(House)));

         struct.append(MWCyclopsUtils.structEnd());

         return struct.toString();
     }
}
 