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

package server.campaign;

import common.CampaignData;

/**
 * @author urgru
 * A barebones timing thread which calls slices in CampaignMain.
 * 
 * Created in CM as follows:
 * SThread = new SliceThread(this, Integer.parseInt(getConfig("SliceTime")));
 *       SThread.start();//it slices, it dices, it chops!
 */

public class SliceThread extends Thread {
	server.campaign.CampaignMain myCampaign;
	long until;
	int Duration;
	int sliceid = 0;
	
	public SliceThread(server.campaign.CampaignMain main, int Duration) {
		super("slicethread");
		this.Duration = Duration; //set length when thread is spun
		myCampaign = main;
	}
	
	public int getSliceID() {
		return sliceid;
	}
	
	public void extendedWait(int time) {
		until = System.currentTimeMillis() + time;
		try {
			this.wait(time);
		} catch (Exception ex) {
			CampaignData.mwlog.errLog(ex);
		}
	}//end ExtendedWait(time)
	
	public long getRemainingSleepTime() {
		return Math.max(0, until - System.currentTimeMillis());
	}
	
	@Override
	public synchronized void run() {
		try {
			while (true) {
				this.extendedWait(Duration); 
				sliceid++;
				try {
					myCampaign.slice(getSliceID());
				} catch (Exception ex) {
					CampaignData.mwlog.errLog(ex);
					myCampaign.doSendToAllOnlinePlayers("Slice skipped. Errors occured", true);
				}
			}
		}
		catch (Exception ex) {
			CampaignData.mwlog.errLog(ex);
		}
	}
}