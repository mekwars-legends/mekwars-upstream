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

package server.util;

import java.io.PrintWriter;
import java.net.Socket;

import server.MMServ;
import server.campaign.CampaignMain;

public class TrackerThread extends Thread {
	
	//VARIABLE
	MMServ serv;
	
	//CONSTRUCTOS
	public TrackerThread(MMServ s) {
		serv = s;
		MMServ.mmlog.infoLog("Created TrackerThread");
	}
	
	//METHODS
	public void extendedWait(int time) {
		try {
			this.wait(time);
		} catch (Exception ex) {
			MMServ.mmlog.errLog(ex);
		}
	}
	
	@Override
	public synchronized void run() {
		
		MMServ.mmlog.infoLog("TrackerThread running.");
		
		//core info
		String name = serv.getConfigParam("SERVERNAME");
		String link = serv.getConfigParam("TRACKERLINK");
		String desc = serv.getConfigParam("TRACKERDESC");
		
		//ip of tracker
		String trackerAddress = serv.getConfigParam("TRACKERADDRESS");
		
		/*
		 * Immediately send core info to tracker.
		 */
		Socket sock;
		try {
			MMServ.mmlog.infoLog("TrackerThread attempting to send ServerStart information.");
			sock = new Socket(trackerAddress, 13731);//fixed port
			PrintWriter pw = new PrintWriter(sock.getOutputStream());
			
			pw.println("SS%" + name + "%" + link + "%" + MMServ.SERVER_VERSION + "%" + desc);
			pw.flush();
			pw.close();
			MMServ.mmlog.infoLog("TrackerThread sent server start information.");
		} catch (Exception e) {
			MMServ.mmlog.infoLog("TrackerThread could not contact tracker. Shutting down.");
			MMServ.mmlog.errLog("Could not contact tracker. Shutting down trackerthread.");
			MMServ.mmlog.errLog(e);
			return;
		}
		
		/*
		 * socket is closed server side after this
		 * line is read from a buffered stream.
		 */ 
		
		/*
		 * now that the first run is done, start a forever-long
		 * phone home loop which updates tracker with player
		 * counts, game counts, etc. every 10 minutes.
		 */
		try {
			while (true) {
				
				//10 minute wait between updates
				this.extendedWait(600000);
				
				//set up substrings
				//name - already saved from ServerStart
				int playersOnline = serv.userCount(false);
				int gamesInProgress = 0;
				int gamesCompleted = 0;
				
				//get campaign main. if not null, get game info.
				CampaignMain campaign = serv.getCampaign();
				if (campaign != null) {
					gamesInProgress = campaign.getOpsManager().getRunningOps().size();
					gamesCompleted = campaign.getGamesCompleted();
					campaign.setGamesCompleted(0);
				}
				
				//string to send to tracker
				String toSend = "PH%" + name + "%" + playersOnline + "%" + gamesInProgress + "%" + gamesCompleted;
				
				//set up a socket to the tracker and send this record
				try {
					MMServ.mmlog.infoLog("TrackerThread attempting to send PhoneHome information.");
					sock = new Socket(trackerAddress, 13731);//fixed port
					PrintWriter pw = new PrintWriter(sock.getOutputStream());
					
					pw.println(toSend);
					pw.flush();
					pw.close();
					MMServ.mmlog.infoLog("TrackerThread sent PH% information.");
				} catch (Exception e) {
					MMServ.mmlog.infoLog("TrackerThread could not reach tracker for PH%.");
					MMServ.mmlog.errLog("Could not contact tracker.");
					MMServ.mmlog.errLog(e);
				}
			}//end (forever)	
		}
		catch (Exception ex) {
			MMServ.mmlog.errLog(ex);
		}
	}
}