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

	
package server.mwmysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import common.CampaignData;

public class UnitHandler {
	
	private JDBCConnectionHandler ch = new JDBCConnectionHandler();
	
	public void linkUnitToFaction(int unitID, int factionID) {
		Statement stmt = null;
		Connection con = ch.getConnection();
		try {
			stmt = con.createStatement();

			stmt.executeUpdate("UPDATE units set uPlayerID = NULL, uFactionID = " + factionID + " WHERE ID = " + unitID);
			
		} catch (SQLException e) {
			CampaignData.mwlog.dbLog("SQL Error in UnitHandler.linkUnitToFaction: " + e.getMessage());
            CampaignData.mwlog.dbLog(e);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException ex) {}
		}
		ch.returnConnection(con);
	}
	
	public int getUnitDBIdFromMWId(int MWId) {
		int uID = 0;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection con = ch.getConnection();
		try {
			ps = con.prepareStatement("SELECT ID from units WHERE MWID = ?");
			ps.setInt(1, MWId);
			rs = ps.executeQuery();
			if(rs.next()) {
				uID = rs.getInt("ID");
			}

		} catch (SQLException e) {
			CampaignData.mwlog.dbLog("SQL Error in UnitHandler.getDBIdFromMWId: " + e.getMessage());
            CampaignData.mwlog.dbLog(e);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
			} catch (SQLException ex) {}
		}
		ch.returnConnection(con);
		return uID;
	}
	
	public void unlinkUnit(int unitID){
		Statement stmt = null;
		Connection c = ch.getConnection();
		try {
			stmt = c.createStatement();
			stmt.executeUpdate("UPDATE units set uFactionID = NULL, uPlayerID = NULL WHERE ID = " + unitID);
		} catch(SQLException e) {
			CampaignData.mwlog.dbLog("SQL Error in UnitHandler.unlinkUnit: " + e.getMessage());
            CampaignData.mwlog.dbLog(e);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException ex) {}
		}
		ch.returnConnection(c);
	}
	
	public void linkUnitToPlayer(int unitID, int playerID) {
		PreparedStatement ps = null;
		Connection con = ch.getConnection();
		try {
			ps = con.prepareStatement("UPDATE units set uFactionID = NULL, uPlayerID = ? WHERE ID = " + unitID);
			ps.setInt(1, playerID);
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			CampaignData.mwlog.dbLog("SQL Error in UnitHandler.linkUnitToPlayer: " + e.getMessage());
            CampaignData.mwlog.dbLog(e);
		} finally {
			try {
				if (ps != null) 
					ps.close();
			} catch (SQLException ex) {}
		} 
		ch.returnConnection(con);
	}
	
	public void deleteUnit(int unitID) {
		Statement stmt = null;
		Connection con = ch.getConnection();
		try {
			stmt = con.createStatement();
			stmt.executeUpdate("DELETE from unit_mgs WHERE unitID = " + unitID);
			stmt.executeUpdate("DELETE from unit_ammo WHERE unitID = " + unitID);
			stmt.executeUpdate("DELETE from units WHERE ID = " + unitID);
		} catch (SQLException e) {
			CampaignData.mwlog.dbLog("SQL Error in UnitHandler.deleteUnit: " + e.getMessage());
            CampaignData.mwlog.dbLog(e);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException ex) {}
		}
		ch.returnConnection(con);
	}
	
	public UnitHandler() {
		
	}

}
