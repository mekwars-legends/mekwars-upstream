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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;

import server.campaign.operations.OperationReportEntry;
import server.campaign.SUnit;
import common.CampaignData;

public class HistoryHandler {
	JDBCConnectionHandler ch = new JDBCConnectionHandler();
  
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
  
  public static final int MECHSTAT_TYPE_GAMEPLAYED = 0;
  public static final int MECHSTAT_TYPE_GAMEWON = 1;
  public static final int MECHSTAT_TYPE_UNITSCRAPPED = 2;
  public static final int MECHSTAT_TYPE_UNITDESTROYED =3;
  
  
  private Hashtable<String, Integer> mechStatIDs = new Hashtable<String, Integer>();
  
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
	  Connection con = ch.getConnection();
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
	  ch.returnConnection(con);
  }
  
  public void commitBattleReport(OperationReportEntry opData) {
	  PreparedStatement ps = null;
	  Connection con = ch.getConnection();
	  try {
		  CampaignData.mwlog.dbLog("New Operation!!!");
		  CampaignData.mwlog.dbLog("Attacker(s): " + opData.getAttackers() + " (" + opData.getAttackerStartBV() + " / " + opData.getAttackerEndBV() + ")");
		  CampaignData.mwlog.dbLog("Defender(s): " + opData.getDefenders() + " (" + opData.getDefenderStartBV() + " / " + opData.getDefenderEndBV() + ")");
		  CampaignData.mwlog.dbLog("Planet Info: " + opData.getPlanet() + " / " + opData.getTerrain() + " / " + opData.getTheme());
		  
		  StringBuilder sql = new StringBuilder();
		  
		  sql.append("INSERT into task_history SET ");
		  sql.append("type = ?, ");
		  sql.append("planet = ?, ");
		  sql.append("terrain = ?, ");
		  sql.append("theme = ?, ");
		  sql.append("attackers = ?, ");
		  sql.append("defenders = ?, ");
		  sql.append("attackerStartBV = ?, ");
		  sql.append("attackerEndBV = ?, ");
		  sql.append("attackerNumUnits = ?, ");
		  sql.append("defenderStartBV = ?, ");
		  sql.append("defenderEndBV = ?, ");
		  sql.append("defenderNumUnits = ?, ");
		  sql.append("attackerWon = ?, ");
		  sql.append("drawGame = ?, ");
		  sql.append("winner = ?, ");
		  sql.append("loser = ?, ");
		  sql.append("gameLength = ?");
		  
		  ps = con.prepareStatement(sql.toString());
		  
		  ps.setString(1, opData.getOpType());
		  ps.setString(2, opData.getPlanet());
		  ps.setString(3, opData.getTerrain());
		  ps.setString(4, opData.getTheme());
		  ps.setString(5, opData.getAttackers());
		  ps.setString(6, opData.getDefenders());
		  ps.setInt(7, opData.getAttackerStartBV());
		  ps.setInt(8, opData.getAttackerEndBV());
		  ps.setInt(9, opData.getAttackerSize());
		  ps.setInt(10, opData.getDefenderStartBV());
		  ps.setInt(11, opData.getDefenderEndBV());
		  ps.setInt(12, opData.getDefenderSize());
		  ps.setBoolean(13, opData.attackerIsWinner());
		  ps.setBoolean(14, opData.gameIsDraw());
		  ps.setString(15, opData.getWinners());
		  ps.setString(16, opData.getLosers());
		  ps.setString(17, opData.getHumanReadableGameLength());
		  
		  ps.executeUpdate();
		  ps.close();
	  } catch (SQLException e) {
		  CampaignData.mwlog.dbLog("SQLException in HistoryHandler.commitBattleReport: " + e.getMessage());
		  CampaignData.mwlog.dbLog(e);
	  } finally {
		  try {
			  if(ps != null)
				  ps.close();  
		  } catch (SQLException ex) {}
	  }
	  ch.returnConnection(con);
  }
  
  private int getIDByName(String n) {
	  if(mechStatIDs.containsKey(n))
		  return mechStatIDs.get(n);
	  return createNewMechstatRecord(n);
  }
  
  private int createNewMechstatRecord(String n) {
	  Statement stmt = null;
	  ResultSet rs = null;
	  int uID = -1;
	  int BV = -1;
	  int mechSize = -1;
	  
	  mechSize = SUnit.loadMech(n).getWeightClass();
	  BV = SUnit.loadMech(n).calculateBattleValue();
	  
	  if(BV == -1 || mechSize == -1)
		  return -1;  // Invalid mechfile
	  Connection con = ch.getConnection();
	  try {
		  stmt = con.createStatement();
		  stmt.executeUpdate("INSERT into mechstats SET mechFileName = '" + n + "', mechSize = " + mechSize + ", originalBV = " + BV, Statement.RETURN_GENERATED_KEYS);
		  rs = stmt.getGeneratedKeys();
		  if (rs.next()) {
			uID = rs.getInt(1);  
		  }
	  } catch (SQLException e) {
		  CampaignData.mwlog.dbLog("SQLException in HistoryHandler.createNewMechstatRecord (" + n + "): " + e.getMessage());
		  CampaignData.mwlog.dbLog(e);
	  } finally {
		  if (rs != null) {
			  try {
				  rs.close();
			  } catch(SQLException e) {}
		  }
		  if (stmt != null) {
			  try {
				  stmt.close();
			  } catch (SQLException e) {}
		  }
	  }
	  ch.returnConnection(con);
	  if (uID != -1)
		  mechStatIDs.put(n, uID);
	  return uID;
  }
  
  public void addMechstat (String fileName, int mechsize, int gameplayed, int gamewon, int scrapped, int destroyed) {
	  StringBuilder sql = new StringBuilder();
	  Statement stmt = null;
	  ResultSet rs = null;
	  Connection con = ch.getConnection();
	  
	  int unitID = getIDByName(fileName);
	  if (unitID == -1) {
		  		  
	  } else {
		  sql.append("UPDATE mechstats SET gamesPlayed = gamesPlayed + " + gameplayed + ", gamesWon = gamesWon + " + gamewon + ", timesScrapped = timesScrapped + " + scrapped + ", timesDestroyed = timesDestroyed + " + destroyed + ", mechSize = " + mechsize + " WHERE mechFileName = '" + fileName + "'");
		  try {
			  stmt = con.createStatement();
			  stmt.execute(sql.toString());
		  } catch (SQLException e) {
			  CampaignData.mwlog.errLog(e);			  
		  } finally {
			  if (rs != null) {
				  try {
					rs.close();
				} catch (SQLException e) {
					
				}
			  }
			  if (stmt != null) {
				  try {
					  stmt.close();
				  } catch (SQLException e) {
					  
				  }
			  }
		  }
	  }
	  ch.returnConnection(con);
  }
  
  private void addMechstat(String unitType, int statType) {
	  StringBuilder sql = new StringBuilder();
	  Statement stmt = null;
	  String transType = "";
	  	  
	  int unitID = getIDByName(unitType);
	  if (unitID == -1) {
		  
	  }
		    // No entry in database, and we were unable to update the DB
	  
	  switch(statType) {
	  case MECHSTAT_TYPE_GAMEPLAYED: {
		  transType = "gamesPlayed";
		  break;
	  }
	  case MECHSTAT_TYPE_GAMEWON: {
		  transType = "gamesWon";
		  break;
	  }
	  case MECHSTAT_TYPE_UNITSCRAPPED: {
		  transType = "timesScrapped";
		  break;
	  }
	  case MECHSTAT_TYPE_UNITDESTROYED: {
		  transType = "timesDestroyed";
		  break;
	  }
	  default: {
		  return;  // Invalid stat type
	  }
	  }
	  
	  sql.append("UPDATE mechstats set " + transType + " = " + transType + " + 1 WHERE ID = " + unitID);
	  Connection con = ch.getConnection();
	  try {
		  stmt = con.createStatement();
		  stmt.executeUpdate(sql.toString());
	  } catch (SQLException e ) {
		  CampaignData.mwlog.dbLog("SQLException in HistoryHandler.addMechstat: " + e.getMessage());
		  CampaignData.mwlog.dbLog(sql.toString());
		  CampaignData.mwlog.dbLog(e);
	  } finally {
		  try {
			  if (stmt != null)
				  stmt.close();
		  } catch (SQLException e) {}
	  }
	  ch.returnConnection(con);
  }  
  
  private void loadMechStats() {
	  Statement stmt = null;
	  ResultSet rs = null;
	  Connection con = ch.getConnection();
	  try {
		  stmt = con.createStatement();
		  rs = stmt.executeQuery("SELECT ID, mechFileName from mechstats ORDER BY ID");
		  while (rs.next()) {
			  this.mechStatIDs.put(rs.getString("mechFileName"), rs.getInt("ID"));
		  }
	  } catch (SQLException e) {
		  CampaignData.mwlog.dbLog("SQLException in HistoryHandler.loadMechStats: " + e.getMessage());
		  CampaignData.mwlog.dbLog(e);
	  } finally {
		  if (rs != null) {
			  try {
				  rs.close();
			  } catch (SQLException e) {}
		  }
		  if (stmt != null) {
			  try {
				  stmt.close();
			  } catch (SQLException e) {}
		  }
	  }
	  ch.returnConnection(con);
  }
  
  // CONSTRUCTOR
  public HistoryHandler(Connection c)
    {
    this.loadMechStats();
    }
}
