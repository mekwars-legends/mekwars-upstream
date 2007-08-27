/*
 * MekWars - Copyright (C) 2004 
 * 
 * Derived from MegaMekNET (http://www.sourceforge.net/projects/megameknet)
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

package server.dataProvider;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;

import server.MWServ;

import common.CampaignData;
//import common.util.BinReader;
import common.util.BinWriter;



/**
 * 
 * @author Imi (immanuel.scholz@gmx.de)
 */
public class CommandTaskThread extends Thread {
    
    private Socket client;
    private CampaignData data;

    /**
     * Create a new thread to handle an incoming call
     */
    public CommandTaskThread(Socket client, CampaignData data) {
        try{
            this.client = client;
            this.data = data;
            this.client.setSoTimeout(12000);
        }catch (Exception ex){
            
        }
    }
    
    /**
     * @see java.lang.Runnable#run()
     */
    @Override
	public void run() {
        
            // timestamp is in this format
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

            MWServ.mwlog.infoLog("DataProvider call accepted from "+client.getInetAddress());
            BinWriter out = null;
            BufferedReader in = null;
            String cmdStr = "";
            String timeStr = "";
            
            try{
                in = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF8"));
                while ( (cmdStr = in.readLine() ) != null){
                    //read the command name
                    try {
                    	//cmdStr = in.readLine("cmd");
                    	timeStr = in.readLine();
                    	//System.err.println("timeStr: "+timeStr);
                    }catch (Exception e) {
                        in.close();
                    	MWServ.mwlog.errLog("Error getting data provider command or timestamp from client.");
                    	MWServ.mwlog.errLog(e);
                    	return;
                    }//end command name try/catch
                    
                    if ( out == null){
                    //set up output stream
                        try {
                        	out = new BinWriter(new PrintWriter(client.getOutputStream()));
                        } catch (Exception e) {
                            in.close();
                        	MWServ.mwlog.errLog("Error in data provider while creating output stream.");
                        	MWServ.mwlog.errLog(e);
                        	return;
                        } 
                    }//end output stream if
                    
                    //get the actual command class
                    Class cmdClass;
                    ServerCommand cmd;
                    try {
                    	cmdClass = Class.forName("server.dataProvider.commands."+cmdStr);
                    	cmd = (ServerCommand)cmdClass.newInstance();
                    } catch (Exception e) {
                        in.close();
                        out.close();
                    	MWServ.mwlog.errLog("Error creating dataprovider command: " + cmdStr);
                    	MWServ.mwlog.errLog(e);
                    	return;
                    }//end command class try/catch
                   
                    //writing timestamp
                    out.println(sdf.format(new Date()),"lasttimestamp");
                    
                    //execute command
                    try {
                    	cmd.execute(timeStr.equals("")?null:sdf.parse(timeStr),out, data);
                    } catch (Exception e) {
                        in.close();
                        out.close();
                    	MWServ.mwlog.errLog("Error executing dataprovider command: " + cmdStr);
                    	MWServ.mwlog.errLog(e);
                    	return;
                    }//end execute try/catch
                    out.flush();
                }//end While            
                try {
                    MWServ.mwlog.infoLog("Closing DataProvider call from "+client.getInetAddress());
                    in.close();
                    out.close();
                	client.close();
                    client = null;
                }catch (SocketException se ){
                    //no reason to report closed sockets.
                    client = null;
                    return;
                }catch (Exception e) {
                	MWServ.mwlog.errLog(e);
                	return;
                }//end client.close() try
            }catch (SocketException se ){
                //no reason to report closed sockets.
                return;
            }catch (SocketTimeoutException ste){
                try{
                    in.close();
                    out.close();
                    client.close();
                    MWServ.mwlog.infoLog("TimeOut DataProvider call from "+client.getInetAddress());
                }catch(Exception ex){}
                client = null;
                return;
            }catch (Exception ex){
                MWServ.mwlog.errLog(ex);
                return;
            }//end first try
            
    }//end run()
}
