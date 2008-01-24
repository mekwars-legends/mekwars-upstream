package server.mwmysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringTokenizer;

import server.MWServ;
import server.campaign.CampaignMain;
import server.campaign.SPlayer;
import server.campaign.commands.Command;

public class PlayerHandler {

	Connection con;
	
	public int countPlayers() {
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as num from players");
			rs.next();
			int numplayers = rs.getInt("num");
			rs.close();
			stmt.close();
			return numplayers;
		} catch (SQLException e) {
			MWServ.mwlog.dbLog("SQL Error in PlayerHandler.countPlayers: " + e.getMessage());
			return 0;
		}
	}
	
	public void purgeStalePlayers(long days) {
		Statement stmt;
		ResultSet rs;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery("SELECT playerName from players WHERE playerLastModified < (CURRENT_TIMESTAMP() - INTERVAL " + days + " DAY)");
			while(rs.next()) {
				SPlayer p = CampaignMain.cm.getPlayer(rs.getString("playerName"), false, false);
				p.addExperience(100, true);
				Command c = CampaignMain.cm.getServerCommands().get("UNENROLL");
				c.process(new StringTokenizer("CONFIRMED", "#"), rs.getString("playerName"));
				MWServ.mwlog.infoLog(rs.getString("playerName") + " purged.");
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			MWServ.mwlog.dbLog("SQL Error in PlayerHandler.purgeStalePlayers: " + e.getMessage());
		}
	}
	
	public int getPlayerIDByName(String name) {
		try {
			PreparedStatement ps = null;
			ps = con.prepareStatement("SELECT playerID from players WHERE playerName = ?");
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				int pid = rs.getInt("playerID");
				rs.close();
				ps.close();
				return pid;
			}
			rs.close();
			ps.close();
			return -1;
		} catch (SQLException e) {
			MWServ.mwlog.dbLog("SQL Error in PlayerHandler.getPlayerIDByName: " + e.getMessage());
			return -1;
		}
	}
	public void setPassword(int DBId, String pass) {
		try {
			PreparedStatement ps = con.prepareStatement("UPDATE players set playerPassword = MD5(?) WHERE playerID = " + DBId);
			ps.setString(1, pass);
			ps.executeUpdate();
			ps.close();
		} catch(SQLException e) {
			MWServ.mwlog.dbLog("SQL Error in PlayerHandler.setPassword: " + e.getMessage());
		}
	}
	
	public void setPlayerAccess(int DBId, int level) {
		try {
			PreparedStatement ps = con.prepareStatement("UPDATE players set playerAccess = ? WHERE playerID = " + DBId);
			ps.setInt(1, level);
			ps.executeUpdate();
			ps.close();
		} catch(SQLException e) {
			MWServ.mwlog.dbLog("SQL Error in PlayerHandler.setPlayerAccess: " + e.getMessage());
		}
	}
	
	public boolean matchPassword(String playerName, String pass) {
		PreparedStatement ps = null;
		ResultSet rs;
		try {
			ps = con.prepareStatement("SELECT playerPassword, MD5(?) as cryptedpass, playerAccess from players WHERE playerName = ?");
			ps.setString(1, pass);
			ps.setString(2, playerName);
			rs = ps.executeQuery();
			if(!rs.next()){
				rs.close();
				ps.close();
				return false;
			}
			else
				if(rs.getString("playerPassword").equalsIgnoreCase(rs.getString("cryptedpass"))) {
					rs.close();
					ps.close();
					return true;
				}
				else {
					rs.close();
					ps.close();
					return false;
				}
		} catch (SQLException e) {
			MWServ.mwlog.dbLog("SQL Error in PlayerHandler.matchPassword: " + e.getMessage());
			return false;
		}
	}
	
	public boolean playerExists(String name) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = con.prepareStatement("SELECT COUNT(*) as num from players where playerName = ?");
			ps.setString(1, name);
			rs=ps.executeQuery();
			if(!rs.next()) {
				rs.close();
				ps.close();
				return false;
			}
			else
				if(rs.getInt("num") == 1) {
					rs.close();
					ps.close();
					return true;
				}
			rs.close();
			ps.close();
			return false;
		} catch(SQLException e) {
			MWServ.mwlog.dbLog("SQL Error in playerHandler.playerExists: " + e.getMessage());
			return false;
		}
	}
	
	public void deletePlayer(SPlayer p) {
		deletePlayer(p, true);
	}
	
	public void deletePlayer(SPlayer p, boolean deleteForumAccount) {
		Statement stmt;
		try {
			// Remove armies
			stmt = con.createStatement();
			ResultSet rs = null;
			
			stmt.executeUpdate("DELETE from playerarmies WHERE playerID = " + p.getDBId());
			// Remove units - if they should have been donated, SHouse.removePlayer will have done that
			rs = stmt.executeQuery("SELECT ID from units WHERE uPlayerID = " + p.getDBId());
			while(rs.next()) {
				CampaignMain.cm.MySQL.deleteUnit(rs.getInt("ID"));
			}
			rs.close();
			
			// Remove pilots
			rs = stmt.executeQuery("SELECT pilotID from pilots WHERE playerID = " + p.getDBId());
			while (rs.next()) {
				CampaignMain.cm.MySQL.deletePilot(rs.getInt("pilotID"));
			}
			rs.close();
			
			// Remove from phpBB database
			if(CampaignMain.cm.isSynchingBB() && deleteForumAccount) 
				CampaignMain.cm.MySQL.deleteForumAccount(p.getForumID());
			// This has its own set of issues right now - I need to talk to Orca before I put this in.
			
			// Remove player
			stmt.executeUpdate("DELETE from players WHERE playerID = " + p.getDBId());
			stmt.close();
			MWServ.mwlog.dbLog("Deleted account " + p.getName() + " from database.");
		} catch (SQLException e) {
			MWServ.mwlog.dbLog("SQL Error in PlayerHandler.deletePlayer: " + e.getMessage());
		}
	}
	
	public PlayerHandler(Connection c) {
		this.con = c;
	}
}
