package server.mwmysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;



import common.Unit;
import common.campaign.pilot.Pilot;

import server.MMServ;
import server.campaign.CampaignMain;
import server.campaign.SPlayer;
import server.campaign.SUnit;
import server.campaign.pilot.SPilot;

public class PlayerHandler {

	Connection con;
	
	public void savePlayer(SPlayer p) {
		
		
		PreparedStatement ps;
		StringBuffer sql = new StringBuffer();
		try {
			MMServ.mmlog.dbLog("Saving player " + p.getName() + " (DBID: " + p.getDBId() + ")");		
			if(p.getDBId()==0){
				// Not in the database - INSERT it
				sql.setLength(0);
				sql.append("INSERT into players set ");
				sql.append("playerName = ?, ");
				sql.append("playerMoney = ?, ");
				sql.append("playerExperience = ?, ");
				sql.append("playerHouseName = ?, ");
				sql.append("playerLastOnline = ?, ");
				sql.append("playerTotalBays = ?, ");
				sql.append("playerFreeBays = ?, ");
				sql.append("playerRating = ?, ");
				sql.append("playerInfluence = ?, ");
				sql.append("playerFluff = ?, ");
				if (CampaignMain.cm.isUsingAdvanceRepair())
					sql.append("playerBaysOwned = ?, playerTechnicians = NULL, ");
				else
					sql.append("playerBaysOwned = NULL, playerTechnicians = ?, ");
				sql.append("playerRP = ?, ");
				sql.append("playerXPToReward = ?, ");
				sql.append("playerAdminExcludeList = ?, ");
				sql.append("playerExcludeList = ?, ");
				sql.append("playerTotalTechsString = ?, ");
				sql.append("playerAvailableTechsString = ?, ");
				sql.append("playerLogo = ?, ");
				sql.append("playerLastAFR = ?, ");
				sql.append("playerGroupAllowance = ?, ");
				sql.append("playerLastISP = ?, ");
				sql.append("playerIsInvisible = ?, ");

				sql.append("playerUnitParts = ?, ");
				sql.append("playerAccess = ?, ");
				sql.append("playerAutoReorder = ?");

				ps = con.prepareStatement(sql.toString(), PreparedStatement.RETURN_GENERATED_KEYS);
				ps.setString(1, p.getName());
				ps.setInt(2, p.getMoney());
				ps.setInt(3, p.getExperience());
				ps.setString(4, p.getMyHouse().getName());
				ps.setLong(5, p.getLastOnline());
				ps.setInt(6, p.getTotalMekBays());
				ps.setInt(7, p.getFreeBays());
				ps.setDouble(8, p.getRating());
				ps.setInt(9, p.getInfluence());
				ps.setString(10, p.getFluffText());
				if (CampaignMain.cm.isUsingAdvanceRepair())
					ps.setInt(11, p.getBaysOwned());
				else
					ps.setInt(11, p.getTechnicians());
				ps.setInt(12, p.getReward());
				ps.setInt(13, p.getXPToReward());
				ps.setString(14, p.getExclusionList().adminExcludeToString("$"));
				ps.setString(15, p.getExclusionList().playerExcludeToString("$"));
				if(CampaignMain.cm.isUsingAdvanceRepair()) {
					ps.setString(16, p.totalTechsToString());
					ps.setString(17, p.availableTechsToString());
				}
				else {
					ps.setString(16, "");
					ps.setString(17, "");
				}
				if(p.getMyLogo().length() > 0)
					ps.setString(18, p.getMyLogo());
				else
					ps.setString(18, "");
				ps.setDouble(19, p.getLastAttackFromReserve());
				ps.setInt(20, p.getGroupAllowance());
				ps.setString(21, p.getLastISP());
				ps.setString(22, Boolean.toString(p.isInvisible()));
				ps.setString(23, p.getUnitParts().toString());
				if(p.getPassword()!=null)
					ps.setInt(24, p.getPassword().getAccess());
				else
					ps.setInt(24, 0);
				ps.setString(25, Boolean.toString(p.getAutoReorder()));

				ps.executeUpdate();
				ResultSet rs = ps.getGeneratedKeys();
				rs.next();
				p.setDBId(rs.getInt(1));
				rs.close();

			
			} else {
				// Already in the database - UPDATE it
				sql.setLength(0);
				sql.append("UPDATE players set ");
				sql.append("playerName = ?, ");
				sql.append("playerMoney = ?, ");
				sql.append("playerExperience = ?, ");
				sql.append("playerHouseName = ?, ");
				sql.append("playerLastOnline = ?, ");
				sql.append("playerTotalBays = ?, ");
				sql.append("playerFreeBays = ?, ");
				sql.append("playerRating = ?, ");
				sql.append("playerInfluence = ?, ");
				sql.append("playerFluff = ?, ");
				if(CampaignMain.cm.isUsingAdvanceRepair())
					sql.append("playerBaysOwned = ?, playerTechnicians = NULL, ");
				else
					sql.append("playerTechnicians = ?, playerBaysOwned = NULL, ");
				sql.append("playerRP = ?, ");
				sql.append("playerXPToReward = ?, ");
				sql.append("playerAdminExcludeList = ?, ");
				sql.append("playerExcludeList = ?, ");
				sql.append("playerTotalTechsString = ?, ");
				sql.append("playerAvailableTechsString = ?, ");
				sql.append("playerLogo = ?, ");
				sql.append("playerLastAFR = ?, ");
				sql.append("playerGroupAllowance = ?, ");
				sql.append("playerLastISP = ?, ");
				sql.append("playerIsInvisible = ?, ");
				sql.append("playerAccess = ?, ");
				sql.append("playerUnitParts = ?, ");
				sql.append("playerAutoReorder = ? ");
				sql.append("WHERE playerID = ?");
				ps = con.prepareStatement(sql.toString());
				ps.setString(1, p.getName());
				ps.setInt(2, p.getMoney());
				ps.setInt(3, p.getExperience());
				ps.setString(4, p.getMyHouse().getName());
				ps.setLong(5, p.getLastOnline());
				ps.setInt(6, p.getTotalMekBays());
				ps.setInt(7, p.getFreeBays());
				ps.setDouble(8, p.getRating());
				ps.setInt(9, p.getInfluence());
				ps.setString(10, p.getFluffText());
				if (CampaignMain.cm.isUsingAdvanceRepair())
					ps.setInt(11, p.getBaysOwned());
				else
					ps.setInt(11, p.getTechnicians());
				ps.setInt(12, p.getReward());
				ps.setInt(13, p.getXPToReward());
				ps.setString(14, p.getExclusionList().adminExcludeToString("$"));
				ps.setString(15, p.getExclusionList().playerExcludeToString("$"));
				if(CampaignMain.cm.isUsingAdvanceRepair()) {
					ps.setString(16, p.totalTechsToString());
					ps.setString(17, p.availableTechsToString());
			
				}
				else {
					ps.setString(16, "");
					ps.setString(17, "");

				}
				if(p.getMyLogo().length() > 0)
					ps.setString(18, p.getMyLogo());
				else
					ps.setString(18, "");
				ps.setDouble(19, p.getLastAttackFromReserve());
				ps.setInt(20, p.getGroupAllowance());
				ps.setString(21, p.getLastISP());
				ps.setString(22, Boolean.toString(p.isInvisible()));
				ps.setInt(23, p.getPassword().getAccess());
				ps.setString(24, p.getUnitParts().toString());
				ps.setString(25, Boolean.toString(p.getAutoReorder()));
				ps.setInt(26, p.getDBId());
				MMServ.mmlog.dbLog(ps.toString());	
				ps.executeUpdate();

			}
				
		// Save Units
		if (p.getUnits().size() > 0) {
			for (SUnit currU : p.getUnits()) {
				SPilot pilot = (SPilot)currU.getPilot();
				pilot.setCurrentFaction(p.getMyHouse().getName());
				CampaignMain.cm.MySQL.savePilot(pilot, currU.getType(), currU.getWeightclass());
				CampaignMain.cm.MySQL.saveUnit(currU);
				CampaignMain.cm.MySQL.linkUnitToPlayer(currU.getDBId(), p.getDBId());
			}
		}
		if(p.getArmies().size() > 0) {
			ps = con.prepareStatement("DELETE from playerarmies WHERE playerID = " + p.getDBId());
			ps.executeUpdate();
			for (int i = 0; i < p.getArmies().size(); i++) {

					sql.setLength(0);
					sql.append("INSERT into playerarmies set playerID = " + p.getDBId() + ", armyID = " + p.getArmies().elementAt(i).getID() + ", armyString = ?");
					ps=con.prepareStatement(sql.toString());
					ps.setString(1, p.getArmies().elementAt(i).toString(false,"%"));
					ps.executeUpdate();			
				}
			}
		// Save Personal Pilots Queues
		for(int weightClass = Unit.LIGHT; weightClass < Unit.ASSAULT; weightClass++) {
			LinkedList<Pilot> currList = p.getPersonalPilotQueue().getPilotQueue(Unit.MEK, weightClass);
			int numPilots = currList.size();
			for(int position = 0; position < numPilots; position++) {
				CampaignMain.cm.MySQL.savePilot((SPilot)currList.get(position), Unit.MEK, weightClass);
				CampaignMain.cm.MySQL.linkPilotToPlayer(((SPilot)currList.get(position)).getDBId(), p.getDBId());
			}
		}
		for(int weightClass = Unit.LIGHT; weightClass < Unit.ASSAULT; weightClass++) {
			LinkedList<Pilot> currList = p.getPersonalPilotQueue().getPilotQueue(Unit.PROTOMEK, weightClass);
			int numPilots = currList.size();
			for(int position = 0; position < numPilots; position++) {
				CampaignMain.cm.MySQL.savePilot((SPilot)currList.get(position), Unit.PROTOMEK, weightClass);
				CampaignMain.cm.MySQL.linkPilotToPlayer(((SPilot)currList.get(position)).getDBId(), p.getDBId());
			}
		}
		
		} catch (SQLException e) {
			MMServ.mmlog.dbLog("SQL error in PlayerHandler.savePlayer: " + e.getMessage());
		}
	}
		
	
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
	
	public int getPlayerIDByName(String name) {
		try {
			PreparedStatement ps;
			ps = con.prepareStatement("SELECT playerID from players WHERE playerName = ?");
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			if(rs.next())
				return rs.getInt("playerID");
			else
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
	
	public int matchPassword(String playerName, String pass) {
		PreparedStatement ps;
		ResultSet rs;
		try {
			ps = con.prepareStatement("SELECT playerPassword, MD5(?) as cryptedpass, playerAccess from players WHERE playerName = ?");
			ps.setString(1, pass);
			ps.setString(2, playerName);
			rs = ps.executeQuery();
			if(!rs.next())
				return 0;
			else
				if(rs.getString("playerPassword").equalsIgnoreCase(rs.getString("cryptedpass")))
					return rs.getInt("playerAccess");
				else
					return 0;
		} catch (SQLException e) {
			MMServ.mmlog.dbLog("SQL Error in PlayerHandler.matchPassword: " + e.getMessage());
			return 0;
		}
	}
	
	public PlayerHandler(Connection c) {
		this.con = c;
	}
}
