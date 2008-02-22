/*
 * MekWars - Copyright (C) 2004 
 * 
 * Original Author: nmorris (urgru@users.sourceforge.net)
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

package server.dataProvider.commands;

import java.util.Date;
//import java.io.BufferedReader;
//import java.io.FileInputStream;
//import java.io.InputStreamReader;
import java.io.File;

import common.CampaignData;
import common.util.BinWriter;
import common.util.MD5;

import common.CampaignData;
import server.dataProvider.ServerCommand;
//import server.campaign.CampaignMain;

/**
 * Retrieve the MD5 of the current campaignconfig file.
 */
public class ServerMegaMekGameOptionsMD5 implements ServerCommand {

    public void execute(Date timestamp, BinWriter out, CampaignData data) throws Exception {
    	
    	String ServerMegaMekGameOptionsMD5 = "";
        File ServerMegaMekGameOptions = new File("./mmconf/gameoptions.xml");
    	if (ServerMegaMekGameOptions.exists()) {
            ServerMegaMekGameOptionsMD5 = MD5.getHashString(ServerMegaMekGameOptions);
    	} else {
    		CampaignData.mwlog.mainLog("gameoptions.xml didn't exist. returning bum string to requesting client.");
    	}
       
        out.println(ServerMegaMekGameOptionsMD5, "ServerMegaMekGameOptionsMD5");
    }
}
