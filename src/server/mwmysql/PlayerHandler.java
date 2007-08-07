package server.mwmysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringTokenizer;

import server.MMServ;
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
			return numplayers;
		} catch (SQLException e) {
			MMServ.mmlog.dbLog("SQL Error in PlayerHandler.countPlayers: " + e.getMessage());
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
				SPlayer p = CampaignMain.cm.getPlayer(rs.getString("playerName"), false);
				p.addExperience(100, true);
				Command c = CampaignMain.cm.getServerCommands().get("UNENROLL");
				c.process(new StringTokenizer("CONFIRMED", "#"), rs.getString("playerName"));
				MMServ.mmlog.infoLog(rs.getString("playerName") + " purged.");
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			MMServ.mmlog.dbLog("SQL Error in PlayerHandler.purgeStalePlayers: " + e.getMessage());
		}
	}
	
	public int getPlayerIDByName(String name) {
		try {
			PreparedStatement ps;
			ps = con.prepareStatement("SELECT playerID from players WHERE playerName = ?");
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			if(rs.next())
				return rs.getInt("playerID");
			return -1;
		} catch (SQLException e) {
			MMServ.mmlog.dbLog("SQL Error in PlayerHandler.getPlayerIDByName: " + e.getMessage());
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
			MMServ.mmlog.dbLog("SQL Error in PlayerHandler.setPassword: " + e.getMessage());
		}
	}
	
	public void setPlayerAccess(int DBId, int level) {
		try {
			PreparedStatement ps = con.prepareStatement("UPDATE players set playerAccess = ? WHERE playerID = " + DBId);
			ps.setInt(1, level);
			ps.executeUpdate();
			ps.close();
		} catch(SQLException e) {
			MMServ.mmlog.dbLog("SQL Error in PlayerHandler.setPlayerAccess: " + e.getMessage());
		}
	}
	
	public boolean matchPassword(String playerName, String pass) {
		PreparedStatement ps;
		ResultSet rs;
		try {
			ps = con.prepareStatement("SELECT playerPassword, MD5(?) as cryptedpass, playerAccess from players WHERE playerName = ?");
			ps.setString(1, pass);
			ps.setString(2, playerName);
			rs = ps.executeQuery();
			if(!rs.next())
				return false;
			else
				if(rs.getString("playerPassword").equalsIgnoreCase(rs.getString("cryptedpass")))
					return true;
				else
					return false;
		} catch (SQLException e) {
			MMServ.mmlog.dbLog("SQL Error in PlayerHandler.matchPassword: " + e.getMessage());
			return false;
		}
	}
	
	public boolean playerExists(String name) {
		PreparedStatement ps;
		ResultSet rs;
		try {
			ps = con.prepareStatement("SELECT COUNT(*) as num from players where playerName = ?");
			ps.setString(1, name);
			rs=ps.executeQuery();
			if(!rs.next())
				return false;
			else
				if(rs.getInt("num") == 1)
					return true;
			return false;
		} catch(SQLException e) {
			MMServ.mmlog.dbLog("SQL Error in playerHandler.playerExists: " + e.getMessage());
			return false;
		}
	}
	
	public void deletePlayer(SPlayer p) {
		Statement stmt;
		try {
			// Remove armies
			stmt = con.createStatement();
			stmt.executeUpdate("DELETE from playerarmies WHERE playerID = " + p.getDBId());
			// Remove player
			stmt.executeUpdate("DELETE from players WHERE playerID = " + p.getDBId());
		} catch (SQLException e) {
			MMServ.mmlog.dbLog("SQL Error in PlayerHandler.deletePlayer: " + e.getMessage());
		}
	}
	
	public PlayerHandler(Connection c) {
		this.con = c;
	}
}
