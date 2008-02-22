	/*
	 * MekWars - Copyright (C) 2007 
	 * 
	 * Original author - Bob Eldred (billypinhead@users.sourceforge.net)
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

	/*
	 * My personal thanks go out to McWizard for the inspiration for this.
	 * Unit histories were positively one of the *GREAT* additions to the
	 * MegaMekNET campaigns, and I can only hope that my efforts here can
	 * result in a similar outcome.  Much of this has been drawn directly
	 * or semi-directly from the MegaMekNET sourcecode on Sourceforge.
	 */
	
package server.mwmysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import common.CampaignData;

public class HistoryHandler {
  Connection con = null;
  
  public static final int UNIT_CREATED = 0;
  public static final int UNIT_AUTOPRODUCED = 1;
  public static final int UNIT_RAIDED = 2;
  public static final int UNIT_SALVAGED = 3;
  public static final int UNIT_DONATED = 4;
  public static final int UNIT_TRADED_AWAY = 5;
  public static final int UNIT_RECEIVED_IN_TRADE = 6;
  public static final int UNIT_SCRAPPED = 7;
  public static final int UNIT_BOUGHT_FROM_BAY = 8;
  public static final int UNIT_BOUGHT_FROM_FACTORY = 9;
  public static final int UNIT_BOUGHT_WITH_RP = 10;
  public static final int UNIT_DESTROYED = 11;
  public static final int UNIT_DESTROYED_BY_ACCIDENT = 12;
  public static final int UNIT_SOLD_ON_BM = 13;
  public static final int UNIT_PARTICIPATED_IN_TASK = 14;
  public static final int UNIT_KILLED_UNIT = 15;
  public static final int UNIT_PILOT_ASSIGNED = 16;
  public static final int UNIT_PILOT_KILLED = 17;
  public static final int PILOT_ASSIGNED = 18;
  public static final int PILOT_LEVEL_UP = 19;
  public static final int PILOT_SKILL_GAIN = 20;
  public static final int PILOT_ASSIGNED_TO_QUEUE = 21;
  public static final int PILOT_CHANGED_NAME = 22;
  public static final int PILOT_KILLED_UNIT = 23;
  public static final int PILOT_DISPOSSESSED = 24;
  public static final int PILOT_DIED = 25;
  public static final int PILOT_IMPRISONED = 26;
  public static final int PILOT_DEFECTED = 27;
  public static final int UNIT_PRODUCED_BY_WELFARE = 28;
  
  public static final int HISTORY_TYPE_UNIT = 1;
  public static final int HISTORY_TYPE_PILOT = 2;
  
  public void addHistoryEntry(int historyType, int unitID, int eventType, String fluff) {
	  PreparedStatement ps = null;
	  String hTypeName = "";
	  String unitIDType = "";
	  if(historyType == HISTORY_TYPE_UNIT) {
		  hTypeName = "unit_history";
		  unitIDType = "unit_id";
	  }
	  else if (historyType == HISTORY_TYPE_PILOT) {
		  hTypeName = "pilot_history";
		  unitIDType = "pilot_id";
	  }
	  try {
		  ps = con.prepareStatement("INSERT INTO " + hTypeName + " set event_fluff = ?, " + unitIDType + " = ?, event_type = ?");
		  ps.setString(1, fluff);
		  ps.setInt(2, unitID);
		  ps.setInt(3, eventType);
		  ps.executeUpdate();
		  ps.close();
	  } catch(SQLException e) {
		  CampaignData.mwlog.dbLog("SQLException in HistoryHandler.addHistoryEntry: " + e.getMessage());
          CampaignData.mwlog.dbLog(e);
		  try {
			  if(ps != null)
				  ps.close();
		  } catch (Exception ex) {}
	  }
  }
  
  // CONSTRUCTOR
  public HistoryHandler(Connection c)
    {
    this.con = c;
    }
}
