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
import java.util.Iterator;

import common.Continent;
import common.House;
import common.Planet;
import common.UnitFactory;
import common.util.MD5;

import server.MMServ;
import server.campaign.CampaignMain;
import server.campaign.SPlanet;
 /**
  * Main Planet class for all planet.* calls made to cyclops
  * @author Torren Nov 10, 2005
  *
  */
 public class  MWCyclopsPlanet{
 
     public static String planetWrite(SPlanet planet){

         StringBuilder message = new StringBuilder();

         try{
             message.append(MWCyclopsUtils.methodCallStart());
             message.append(MWCyclopsUtils.methodName("planet.write"));
             
             message.append(MWCyclopsUtils.paramsStart());
             message.append(MWCyclopsUtils.paramStart());
             message.append(MWCyclopsUtils.valueStart());
             
             message.append(createPlanetStruct(planet)); 
             
             message.append(MWCyclopsUtils.valueEnd());
             message.append(MWCyclopsUtils.paramEnd());
             message.append(MWCyclopsUtils.paramsEnd());
             
             message.append(MWCyclopsUtils.methodCallEnd());
         }catch(Exception ex){
             MMServ.mmlog.errLog(ex);
         }
         
         return message.toString();
     }
      
     public static String planetWriteFromList(Collection<Planet> planets){
         
         StringBuilder message = new StringBuilder();

         try{
             message.append(MWCyclopsUtils.methodCallStart());
             message.append(MWCyclopsUtils.methodName("planet.writeFromList"));
             
             message.append(MWCyclopsUtils.paramsStart());
             message.append(MWCyclopsUtils.paramStart());
             message.append(MWCyclopsUtils.valueStart());
             message.append(MWCyclopsUtils.arrayStart());
             message.append(MWCyclopsUtils.dataStart());
             
             for ( Planet planet : planets ){
                 message.append(MWCyclopsUtils.value(createPlanetStruct(planet))); 
             }
             
             message.append(MWCyclopsUtils.dataEnd());
             message.append(MWCyclopsUtils.arrayEnd());
             
             message.append(MWCyclopsUtils.valueEnd());
             message.append(MWCyclopsUtils.paramEnd());
             message.append(MWCyclopsUtils.paramsEnd());
             
             message.append(MWCyclopsUtils.methodCallEnd());
         }catch(Exception ex){
             MMServ.mmlog.errLog(ex);
         }
         
         return message.toString();
     }
     
     public static String createPlanetStruct(Planet planet){
         StringBuilder struct = new StringBuilder();
         
         struct.append(MWCyclopsUtils.structStart());
         struct.append(MWCyclopsUtils.structMember("id",MD5.getHashString(planet.getName())));
         struct.append(MWCyclopsUtils.structMember("name",planet.getName()));
         struct.append(MWCyclopsUtils.structMember("coordX",(int)planet.getPosition().x));
         struct.append(MWCyclopsUtils.structMember("coordY",(int)planet.getPosition().y));
         String Owner = "<string></string>";
         if (  planet.getPlanetOwner()!= null )
             Owner = MD5.getHashString(CampaignMain.cm.getData().getHouse(planet.getPlanetOwner()).getName());
         
         struct.append(MWCyclopsUtils.structMember("owner",Owner));
         struct.append(MWCyclopsUtils.structMember("homeworld",planet.isHomeWorld()));
         
         StringBuilder influenceArray = new StringBuilder(MWCyclopsUtils.arrayStart());
         influenceArray.append(MWCyclopsUtils.dataStart());
         for ( House house : planet.getInfluence().getHouses() ){
             influenceArray.append(MWCyclopsUtils.value(createPlanetInfluenceStruct(house,planet)));
         }
         
         influenceArray.append(MWCyclopsUtils.dataEnd());
         influenceArray.append(MWCyclopsUtils.arrayEnd());
         struct.append(MWCyclopsUtils.structMember("influences",influenceArray.toString()));
         
         
         StringBuilder continentArray = new StringBuilder(MWCyclopsUtils.arrayStart());
         int maxprob = planet.getEnvironments().getTotalEnivronmentPropabilities();
         continentArray.append(MWCyclopsUtils.dataStart());
         for ( Continent contintent : planet.getEnvironments().toArray() ){
             continentArray.append(MWCyclopsUtils.value(createPlanetContinent(contintent,maxprob)));
         }
         
         continentArray.append(MWCyclopsUtils.dataEnd());
         continentArray.append(MWCyclopsUtils.arrayEnd());
         struct.append(MWCyclopsUtils.structMember("continents",continentArray.toString()));

         boolean hasEnhancements = false;
         StringBuilder enhancementArray = new StringBuilder(MWCyclopsUtils.arrayStart());
         enhancementArray.append(MWCyclopsUtils.dataStart());
         
         if ( planet.getBaysProvided() > 0 ){
             enhancementArray.append(MWCyclopsUtils.value(createPlanetBayEnhancementStruct(planet.getBaysProvided())));
             hasEnhancements = true;
         }
         
         Iterator it = planet.getUnitFactories().iterator();
         while ( it.hasNext() ){
             hasEnhancements = true;
             UnitFactory factory = (UnitFactory)it.next();
             enhancementArray.append(MWCyclopsUtils.value(createPlanetFactoryEnhancementStruct(factory)));
         }
         enhancementArray.append(MWCyclopsUtils.dataEnd());
         enhancementArray.append(MWCyclopsUtils.arrayEnd());

         if ( hasEnhancements )
             struct.append(MWCyclopsUtils.structMember("enhancements",enhancementArray.toString()));
         
         struct.append(MWCyclopsUtils.structEnd());
         
         
         return struct.toString();
     }
     
     public static String createPlanetInfluenceStruct(House house, Planet planet){
         StringBuilder influenceStruct = new StringBuilder(MWCyclopsUtils.structStart());
         
         influenceStruct.append(MWCyclopsUtils.structMember("faction",MD5.getHashString(house.getName())));
         influenceStruct.append(MWCyclopsUtils.structMember("influence",planet.getInfluence().getInfluence(house.getId())));
         influenceStruct.append(MWCyclopsUtils.structEnd());
         
         return influenceStruct.toString();
     }
     
     public static String createPlanetContinent(Continent continent, int MaxSize){
         StringBuilder environmentStruct = new StringBuilder(MWCyclopsUtils.structStart());

         
         environmentStruct.append(MWCyclopsUtils.structMember("id",MD5.getHashString(continent.getEnvironment().getName())));
         environmentStruct.append(MWCyclopsUtils.structMember("weight",(continent.getSize()*100)/MaxSize));
         environmentStruct.append(MWCyclopsUtils.structMember("hill",continent.getEnvironment().getHillyness()-1/25));
         environmentStruct.append(MWCyclopsUtils.structMember("vegetation",continent.getEnvironment().getForestHeavyProb()-1/25));
         environmentStruct.append(MWCyclopsUtils.structMember("water",continent.getEnvironment().getWaterDeepProb()-1/25));
         environmentStruct.append(MWCyclopsUtils.structMember("river",continent.getEnvironment().getRiverProb()>0));
         environmentStruct.append(MWCyclopsUtils.structMember("road",continent.getEnvironment().getRoadProb()>0));
         environmentStruct.append(MWCyclopsUtils.structMember("crater",continent.getEnvironment().getCraterProb()>0));
         environmentStruct.append(MWCyclopsUtils.structMember("rough",continent.getEnvironment().getRoughMinHexes()>0 && continent.getEnvironment().getRoughMaxHexes()>0));
         environmentStruct.append(MWCyclopsUtils.structMember("swamp",continent.getEnvironment().getSwampMinHexes()>0 && continent.getEnvironment().getSwampMaxHexes()>0));
         environmentStruct.append(MWCyclopsUtils.structMember("ice",continent.getEnvironment().getIceMinHexes()>0 && continent.getEnvironment().getIceMaxHexes()>0));
         environmentStruct.append(MWCyclopsUtils.structMember("build",continent.getEnvironment().getMinBuildings()>0 && continent.getEnvironment().getMaxBuildings()>0));
         environmentStruct.append(MWCyclopsUtils.structEnd());
         
         return environmentStruct.toString();
     }

     public static String createPlanetBayEnhancementStruct(int bays){
         StringBuilder enhancementStruct = new StringBuilder(MWCyclopsUtils.structStart());

         enhancementStruct.append(MWCyclopsUtils.structMember("id",MD5.getHashString("warehouses")));
         enhancementStruct.append(MWCyclopsUtils.structMember("desc","Warehouses"));
         enhancementStruct.append(MWCyclopsUtils.structMember("type","BAY"));
         enhancementStruct.append(MWCyclopsUtils.structMember("rank",bays));
         enhancementStruct.append(MWCyclopsUtils.structEnd());
         
         return enhancementStruct.toString();
     }

     public static String createPlanetFactoryEnhancementStruct(UnitFactory factory){
         StringBuilder enhancementStruct = new StringBuilder(MWCyclopsUtils.structStart());
         String desc = factory.getSize()+" "
         +factory.getFullTypeString() + factory.getName() + " built by " + factory.getFounder();
         
         enhancementStruct.append(MWCyclopsUtils.structMember("id",MD5.getHashString(desc)));
         enhancementStruct.append(MWCyclopsUtils.structMember("desc",desc ));
         enhancementStruct.append(MWCyclopsUtils.structMember("type",factory.getTypeString()+factory.getSize().substring(0,1)));
         enhancementStruct.append(MWCyclopsUtils.structMember("rank",factory.getBestTypeProducable()));
         enhancementStruct.append(MWCyclopsUtils.structEnd());
         
         return enhancementStruct.toString();
     }
}
 