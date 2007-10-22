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

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import server.MWServ;

import common.CampaignData;


/**
 * Starts a server which provides diffs to data files for the clients.  
 * @author Imi (immanuel.scholz@gmx.de)
 */
public class Server implements Runnable {
	
	private CampaignData data;
	private int dataPort;
	private String IpAddress;
	
	public Server(CampaignData data, int dataPort, String IpAddress) {
		this.data = data;
		this.dataPort = dataPort;
		this.IpAddress = IpAddress;
	}
	
	/**
	 * Starts the server. It blocks forever, so make sure to run this
	 * in an extra thread.
	 * @throws IOException
	 */
	public void run() {
		MWServ.mwlog.mainLog("DataProvider: startup...");
		
		//open and bind a socket and wait for incoming calls     
		//If bindip is "-1", we want to bind to all available interfaces.
		ServerSocket server = null;
		
		try {
			
			if (IpAddress.equals("-1"))
				server = new ServerSocket(dataPort,0,null);
			else
				server = new ServerSocket(dataPort,0,InetAddress.getByName(IpAddress));
			
		} catch (IOException e) {
			MWServ.mwlog.errLog("Shutting down because:");
			MWServ.mwlog.errLog(e);
			MWServ.mwlog.mainLog("DataProvider: Could not create server socket. Shutting down.");
			MWServ.mwlog.infoLog("DataProvider: Could not create server socket. Shutting down.");
			return;
		}
		
		MWServ.mwlog.mainLog("DataProvider: server created at port "+dataPort+". Address "+IpAddress+". Waiting for calls...");
		
		//listen for new data requests until an error occurs, or forever.
		while(true) {
			
			try{
				
				Socket client = server.accept();
				new CommandTaskThread(client,data).start();
			
			} catch(OutOfMemoryError OOM) {
				
				MWServ.mwlog.errLog("Out of Memory while opening dataprovider socket:");
				MWServ.mwlog.errLog(OOM.toString());
				
				/*
                 * Ok so too many socket connections lets try a reset
                 * --Torren.
                 */
                try{
                    server.close();
                    server = null;
                    System.gc();
                    if (IpAddress.equals("-1"))
                        server = new ServerSocket(dataPort,0,null);
                    else
                        server = new ServerSocket(dataPort,0,InetAddress.getByName(IpAddress));
                } catch(Exception ex){
                    MWServ.mwlog.errLog("Shutting down because:");
                    MWServ.mwlog.errLog(ex);
                    MWServ.mwlog.mainLog("DataProvider: Could not create server socket. Shutting down.");
                    MWServ.mwlog.infoLog("DataProvider: Could not create server socket. Shutting down.");
                    return;
                }
			} catch (IOException e) {
				MWServ.mwlog.errLog("Dataprovider IO Exception:");
				MWServ.mwlog.errLog(e);
			} catch(Exception ex) {
				MWServ.mwlog.errLog(ex);
			}
		}
		
	}//end run()
	
}//end Server.java
