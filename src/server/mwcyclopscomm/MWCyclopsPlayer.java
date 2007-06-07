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

import common.util.MD5;

import server.MMServ;
import server.campaign.CampaignMain;
import server.campaign.SPlayer;
 
 public class  MWCyclopsPlayer{
 
     public static String playerWrite(SPlayer player){

         String message = "";

         try{
             message += MWCyclopsUtils.methodCallStart();
             message += MWCyclopsUtils.methodName("player.write");
             
             message += MWCyclopsUtils.paramsStart();
             message += MWCyclopsUtils.paramStart();
             message += MWCyclopsUtils.valueStart();
             
             message += MWCyclopsUtils.structStart();
             message += MWCyclopsUtils.structMember("id",MD5.getHashString(player.getName().toLowerCase()));
             message += MWCyclopsUtils.structMember("username",player.getName());
             message += MWCyclopsUtils.structMember("desc",player.getFluffText().replaceAll("<","&lt").replaceAll(">","&gt"));
             message += MWCyclopsUtils.structMember("faction",MD5.getHashString(player.getMyHouse().getName()));
             message += MWCyclopsUtils.structMember("logo",player.getMyLogo());
             message += MWCyclopsUtils.structMember("camo"," ");
             message += MWCyclopsUtils.structMember("passwd"," ");
             message += MWCyclopsUtils.structMember("access",SPlayer.playerLevelDescription(CampaignMain.cm.getServer().getUserLevel(player.getName())));
             message += MWCyclopsUtils.structMember("level",CampaignMain.cm.getServer().getUserLevel(player.getName()));
             message += MWCyclopsUtils.structMember("lastseen",player.getLastOnline());

             message += MWCyclopsUtils.structEnd();
             
             message += MWCyclopsUtils.valueEnd();
             message += MWCyclopsUtils.paramEnd();
             message += MWCyclopsUtils.paramsEnd();
             
             message += MWCyclopsUtils.methodCallEnd();
         }catch(Exception ex){
             MMServ.mmlog.errLog(ex);
         }
         
         return message;
     }
      
}
 