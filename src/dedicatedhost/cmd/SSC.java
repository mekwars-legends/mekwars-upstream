/*
 * MekWars - Copyright (C) 2008
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

package dedicatedhost.cmd;

import java.util.StringTokenizer;

import megamek.MegaMek;

import common.CampaignData;
import common.util.TokenReader;

import dedicatedhost.MWDedHost;

public class SSC extends Command {
	
	public SSC(MWDedHost mwclient) {
		super(mwclient);
	}
	
	/**
	 * @see client.cmd.Command#execute(java.lang.String)
	 */
	@Override
	public void execute(String input) {

	    try {
	        StringTokenizer st = decode(input);
	        
	        while ( st.hasMoreTokens() ) {
	            mwclient.getserverConfigs().put(TokenReader.readString(st), TokenReader.readString(st));
	        }
	    }catch( Exception ex) {
	        CampaignData.mwlog.errLog(ex);
	    }
	    
        String MMVersion = mwclient.getserverConfigs("AllowedMegaMekVersion");
        if (!MMVersion.equals("-1") && !MMVersion.equalsIgnoreCase(MegaMek.VERSION)) {
            CampaignData.mwlog.errLog("You are using an invalid version of MegaMek. Please use version " + MMVersion);
            mwclient.stopHost();
            mwclient.updateDed();
            return;
        }

		//mwclient.setWaiting(false);
	}//end execute
}//end SC.java
