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

import server.MWServ;

public class UnitHandler {
	
	Connection con;
	
	public void linkUnitToFaction(int unitID, int factionID) {
		try {
			Statement stmt = con.createStatement();

			stmt.executeUpdate("UPDATE units set uPlayerID = NULL, uFactionID = " + factionID + " WHERE ID = " + unitID);
			stmt.close();
		} catch (SQLException e) {
			MWServ.mwlog.dbLog("SQL Error in UnitHandler.linkUnitToFaction: " + e.getMessage());
            MWServ.mwlog.dbLog(e);
		}
	}
	
	public int getUnitDBIdFromMWId(int MWId) {
		int uID = 0;
		try {
			PreparedStatement ps = con.prepareStatement("SELECT ID from units WHERE MWID = ?");
			ps.setInt(1, MWId);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				uID = rs.getInt("ID");
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			MWServ.mwlog.dbLog("SQL Error in UnitHandler.getDBIdFromMWId: " + e.getMessage());
            MWServ.mwlog.dbLog(e);
		} 
		return uID;
	}
	
	public void unlinkUnit(int unitID){
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate("UPDATE units set uFactionID = NULL, uPlayerID = NULL WHERE ID = " + unitID);
			stmt.close();
		} catch(SQLException e) {
			MWServ.mwlog.dbLog("SQL Error in UnitHandler.unlinkUnit: " + e.getMessage());
            MWServ.mwlog.dbLog(e);
		}
	}
	
	public void linkUnitToPlayer(int unitID, int playerID) {
		try {
			PreparedStatement ps = null;
			ps = con.prepareStatement("UPDATE units set uFactionID = NULL, uPlayerID = ? WHERE ID = " + unitID);
			ps.setInt(1, playerID);
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			MWServ.mwlog.dbLog("SQL Error in UnitHandler.linkUnitToPlayer: " + e.getMessage());
            MWServ.mwlog.dbLog(e);
		}
	}
	
	public void deleteUnit(int unitID) {
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate("DELETE from unit_mgs WHERE unitID = " + unitID);
			stmt.executeUpdate("DELETE from unit_ammo WHERE unitID = " + unitID);
			stmt.executeUpdate("DELETE from units WHERE ID = " + unitID);
			stmt.close();
		} catch (SQLException e) {
			MWServ.mwlog.dbLog("SQL Error in UnitHandler.deleteUnit: " + e.getMessage());
            MWServ.mwlog.dbLog(e);
		}
	}
	
	public UnitHandler(Connection c) {
		this.con = c;
	}

}
