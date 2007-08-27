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


import server.MWServ;
import server.campaign.CampaignMain;
import server.campaign.SArmy;
import server.campaign.SPlayer;
import server.campaign.operations.ShortOperation;

import common.Unit;
import common.util.MD5;
 
 public class  MWCyclopsOp{
     
     public static String opWrite(ShortOperation op){
         StringBuilder message = new StringBuilder();
         
         try{
             message.append(MWCyclopsUtils.methodCallStart());
             message.append(MWCyclopsUtils.methodName("op.write"));
             
             message.append(MWCyclopsUtils.paramsStart());
             message.append(MWCyclopsUtils.paramStart());
             message.append(MWCyclopsUtils.valueStart());
             
             message.append(createOpStruct(op));
             
             message.append(MWCyclopsUtils.valueEnd());
             message.append(MWCyclopsUtils.paramEnd());
             message.append(MWCyclopsUtils.paramsEnd());
             
             message.append(MWCyclopsUtils.methodCallEnd());
         }catch(Exception ex){
             MWServ.mwlog.errLog(ex);
         }

         
         return message.toString();
     }
     
     public static String createOpStruct(ShortOperation op){
         StringBuilder struct = new StringBuilder();
         
         struct.append(MWCyclopsUtils.structStart());
         
         struct.append(MWCyclopsUtils.structMember("id",op.getOpCyclopsID()));
         struct.append(MWCyclopsUtils.structMember("planet",MD5.getHashString(op.getTargetWorld().getName())));
         struct.append(MWCyclopsUtils.structMember("continent",MD5.getHashString(op.getEnvironment().getName())));
         struct.append(MWCyclopsUtils.structMember("players",createPlayerOpArray(op)));
         struct.append(MWCyclopsUtils.structMember("type","RD"));
         
         struct.append(MWCyclopsUtils.structEnd());

         return struct.toString();
     }
     
     public static String createPlayerOpArray(ShortOperation op){
         StringBuilder playerOpArray = new StringBuilder();
         
         playerOpArray.append(MWCyclopsUtils.arrayStart());
         playerOpArray.append(MWCyclopsUtils.dataStart());
         
         for ( String attacker : op.getAttackers().keySet() ){
             playerOpArray.append(MWCyclopsUtils.value(createPlayerOpStruct(attacker,op.getAttackers().get(attacker),"ATT",0)));
         }
         
         for ( String defender : op.getDefenders().keySet() ){
             playerOpArray.append(MWCyclopsUtils.value(createPlayerOpStruct(defender,op.getDefenders().get(defender),"DEF",1)));
         }

         playerOpArray.append(MWCyclopsUtils.dataEnd());
         playerOpArray.append(MWCyclopsUtils.arrayEnd());
         
         return playerOpArray.toString();
     }
     
     public static String createPlayerOpStruct(String playername, int armyid, String role, int team){
         StringBuilder playerOpStruct = new StringBuilder(MWCyclopsUtils.structStart());
         SPlayer player = CampaignMain.cm.getPlayer(playername);
         
         SArmy army = player.getArmy(armyid);
         
         playerOpStruct.append(MWCyclopsUtils.structMember("player",MD5.getHashString(playername.toLowerCase())));
         playerOpStruct.append(MWCyclopsUtils.structMember("role",role));
         playerOpStruct.append(MWCyclopsUtils.structMember("team",team));
         playerOpStruct.append(MWCyclopsUtils.structMember("battlevalue",army.getBV()));
         playerOpStruct.append(MWCyclopsUtils.structMember("faction",MD5.getHashString(player.getMyHouse().getName())));
         playerOpStruct.append(MWCyclopsUtils.structMember("payee",MD5.getHashString(player.getHouseFightingFor().getName())));
         
         playerOpStruct.append(MWCyclopsUtils.structEnd());
         return playerOpStruct.toString();
     }
     
     public static String createUnitOpArray(ShortOperation op){
         StringBuilder unitOpArray = new StringBuilder(MWCyclopsUtils.arrayStart());
         
         unitOpArray.append(MWCyclopsUtils.dataStart());
         
         for ( String playerName : op.getAllPlayersAndArmies().keySet() ){
             SPlayer player = CampaignMain.cm.getPlayer(playerName);
             SArmy army = player.getArmy(op.getAllPlayersAndArmies().get(playerName));
             
             for ( Unit unit : army.getUnits() ){
                 unitOpArray.append(MWCyclopsUtils.value(unit.getId()));
             }
         }
         
         unitOpArray.append(MWCyclopsUtils.dataEnd());
         unitOpArray.append(MWCyclopsUtils.arrayEnd());
         
         return unitOpArray.toString();
     }
     
     static String opConclude(ShortOperation op){
         StringBuilder message = new StringBuilder();
         
         try{
             message.append(MWCyclopsUtils.methodCallStart());
             message.append(MWCyclopsUtils.methodName("op.conclude"));
             
             message.append(MWCyclopsUtils.paramsStart());
             message.append(MWCyclopsUtils.paramStart());
             
             message.append(MWCyclopsUtils.value(op.getOpCyclopsID()));
             
             message.append(MWCyclopsUtils.paramEnd());
             message.append(MWCyclopsUtils.paramsEnd());
             
             message.append(MWCyclopsUtils.methodCallEnd());
         }catch(Exception ex){
             MWServ.mwlog.errLog(ex);
         }

         return message.toString();
     }
     
     static String opCancel(ShortOperation op){
         StringBuilder message = new StringBuilder();
         
         try{
             message.append(MWCyclopsUtils.methodCallStart());
             message.append(MWCyclopsUtils.methodName("op.cancel"));
             
             message.append(MWCyclopsUtils.paramsStart());
             message.append(MWCyclopsUtils.paramStart());
             
             message.append(MWCyclopsUtils.value(op.getOpCyclopsID()));
             
             message.append(MWCyclopsUtils.paramEnd());
             message.append(MWCyclopsUtils.paramsEnd());
             
             message.append(MWCyclopsUtils.methodCallEnd());
         }catch(Exception ex){
             MWServ.mwlog.errLog(ex);
         }

         return message.toString();
     }

}
 