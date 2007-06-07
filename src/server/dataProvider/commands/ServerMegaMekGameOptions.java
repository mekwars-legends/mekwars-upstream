/*
 * MekWars - Copyright (C) 2005
 * 
 * Original author - nmorris (urgru@users.sourceforge.net)
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 */

/**
 * @author Torren (Jason Tighe)
 * 08/18/2005
 * 
 * Sends the servers side gameoptions.xml to the client for editing.
 */


package server.dataProvider.commands;

import java.util.Date;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import common.CampaignData;
import common.util.BinWriter;

import server.dataProvider.ServerCommand;

/**
 * Retrieve all planet information (if the data cache is lost at client side)
 * 
 * @author Imi (immanuel.scholz@gmx.de)
 */
public class ServerMegaMekGameOptions implements ServerCommand {

    /**
     * @see server.dataProvider.ServerCommand#execute(java.util.Date,
     *      java.io.PrintWriter, common.CampaignData)
     */
    public void execute(Date timestamp, BinWriter out, CampaignData data)
            throws Exception {
        
        File gameOptionsFile = new File("./mmconf/gameoptions.xml");
        
        if ( !gameOptionsFile.exists() ){
            out.println("NoFileFound","GameOption");
            return;
        }
        
        FileInputStream gameOptions = new FileInputStream(gameOptionsFile);
        BufferedReader gameOption = new BufferedReader(new InputStreamReader(gameOptions));
        
        while (gameOption.ready()) {
            out.printStringln(gameOption.readLine(),"GameOption");
        }
    }
}
