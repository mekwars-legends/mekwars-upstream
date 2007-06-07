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

import java.util.Collection;

import common.House;
import common.util.MD5;

import server.MMServ;
import server.campaign.SHouse;
 /**
  * Main House class for all house.* commands sent to cyclops
  * @author Torren Nov 10, 2005
  *
  */
 public class MWCyclopsHouse{
     
     public static String houseWrite(SHouse house){

         String message = "";

         try{
             message += MWCyclopsUtils.methodCallStart();
             message += MWCyclopsUtils.methodName("faction.write");
             
             message += MWCyclopsUtils.paramsStart();
             message += MWCyclopsUtils.paramStart();
             message += MWCyclopsUtils.valueStart();
         
             message += createHouseStruct(house);
             
             message += MWCyclopsUtils.valueEnd();
             message += MWCyclopsUtils.paramEnd();
             message += MWCyclopsUtils.paramsEnd();
             
             message += MWCyclopsUtils.methodCallEnd();
         }catch(Exception ex){
             MMServ.mmlog.errLog(ex);
         }
         
         return message;
     }
     
     public static String houseWriteFromList(Collection<House> houses){

         String message = "";

         try{
             message += MWCyclopsUtils.methodCallStart();
             message += MWCyclopsUtils.methodName("faction.writeFromList");
             
             message += MWCyclopsUtils.paramsStart();
             message += MWCyclopsUtils.paramStart();
             message += MWCyclopsUtils.valueStart();
         
             message += MWCyclopsUtils.arrayStart();
             message += MWCyclopsUtils.dataStart();
             
             for ( House house : houses ){
                 message += MWCyclopsUtils.value(createHouseStruct(house));
             }
             
             message += MWCyclopsUtils.dataEnd();
             message += MWCyclopsUtils.arrayEnd();
             
             message += MWCyclopsUtils.valueEnd();
             message += MWCyclopsUtils.paramEnd();
             message += MWCyclopsUtils.paramsEnd();
             
             message += MWCyclopsUtils.methodCallEnd();
         }catch(Exception ex){
             MMServ.mmlog.errLog(ex);
         }
         
         return message;
     }
     
     private static String createHouseStruct(House house){
         String struct = "";
         
         struct += MWCyclopsUtils.structStart();
         struct += MWCyclopsUtils.structMember("id",MD5.getHashString(house.getName()));
         struct += MWCyclopsUtils.structMember("name",house.getName());
         struct += MWCyclopsUtils.structMember("logo",house.getLogo());
         struct += MWCyclopsUtils.structMember("abbrev",house.getAbbreviation());
         struct += MWCyclopsUtils.structMember("color",house.getHouseColor());
         struct += MWCyclopsUtils.structMember("motd",((SHouse)house).getMotd());
         struct += MWCyclopsUtils.structMember("desc","Faction "+house.getName());
         struct += MWCyclopsUtils.structMember("initialRanking",((SHouse)house).getInitialHouseRanking());
         struct += MWCyclopsUtils.structEnd();

         return struct;
     }
}
 