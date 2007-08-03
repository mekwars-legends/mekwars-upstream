package server.mwmysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import server.MMServ;

public class UnitHandler {
	
	Connection con;
	
	public void linkUnitToFaction(int unitID, int factionID) {
		try {
			MMServ.mmlog.dbLog("Linking Unit to Faction: ");
			MMServ.mmlog.dbLog(" --> Unit ID: " + unitID);
			MMServ.mmlog.dbLog(" --> Faction ID: " + factionID);
			Statement stmt = con.createStatement();

			stmt.executeUpdate("UPDATE units set uPlayerID = NULL, uFactionID = " + factionID + " WHERE MWID = " + unitID);
			stmt.close();
		} catch (SQLException e) {
			MMServ.mmlog.dbLog("SQL Error in UnitHandler.linkUnitToFaction: " + e.getMessage());
		}
	}
	
	public void unlinkUnit(int unitID){
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate("UPDATE units set uFactionID = NULL, uPlayerID = NULL WHERE ID = " + unitID);
			stmt.close();
		} catch(SQLException e) {
			MMServ.mmlog.dbLog("SQL Error in UnitHandler.unlinkUnit: " + e.getMessage());
		}
	}
	
	public void linkUnitToPlayer(int unitID, int playerID) {
		try {
			PreparedStatement ps;
			ps = con.prepareStatement("UPDATE units set uFactionID = NULL, uPlayerID = ? WHERE MWID = " + unitID);
			ps.setInt(1, playerID);
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			MMServ.mmlog.dbLog("SQL Error in UnitHandler.linkUnitToPlayer: " + e.getMessage());
		}
	}
	
	public void deleteUnit(int unitID) {
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate("DELETE from unit_mgs WHERE unitID = " + unitID);
			stmt.executeUpdate("DELETE from unit_ammo WHERE unitID = " + unitID);
			stmt.executeUpdate("DELETE from units WHERE MWID = " + unitID);
			stmt.close();
		} catch (SQLException e) {
			MMServ.mmlog.dbLog("SQL Error in UnitHandler.deleteUnit: " + e.getMessage());
		}
	}
	
	public UnitHandler(Connection c) {
		this.con = c;
	}

}
