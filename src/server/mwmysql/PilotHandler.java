package server.mwmysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.StringTokenizer;

import common.campaign.pilot.skills.PilotSkill;

import server.MMServ;
import server.campaign.CampaignMain;
import server.campaign.pilot.SPilot;
import server.campaign.pilot.skills.AstechSkill;
import server.campaign.pilot.skills.EdgeSkill;
import server.campaign.pilot.skills.SPilotSkill;
import server.campaign.pilot.skills.TraitSkill;
import server.campaign.pilot.skills.WeaponSpecialistSkill;

public class PilotHandler {

	Connection con;
	
	public SPilot loadPilot(int pID) {
		SPilot p = new SPilot("Vacant", 99, 99);
		ResultSet rs;
		TraitSkill traitSkill = null;
		try {
			try {
				Statement stmt = con.createStatement();
				rs = stmt.executeQuery("SELECT * from pilots WHERE pilotID = " + pID);
				if(rs.next()) {
					p.setName(rs.getString("pilotName"));
					p.setExperience(rs.getInt("pilotExp"));
					p.setGunnery(rs.getInt("pilotGunnery"));
					p.setPiloting(rs.getInt("pilotPiloting"));
					p.setKills(rs.getInt("pilotKills"));
					p.setCurrentFaction(rs.getString("pilotCurrentFaction"));
					p.setPilotId(rs.getInt("MWID"));
					p.setDBId(pID);
					p.setHits(rs.getInt("pilotHits"));
			
					// Load the skills
					MMServ.mmlog.dbLog("Loading skills for pilot ID " + p.getPilotId());
					rs = stmt.executeQuery("SELECT * from pilotskills WHERE pilotID = " + pID);
					while(rs.next()) {
						SPilotSkill skill = CampaignMain.cm.getPilotSkill(rs.getInt("skillNum"));
						int level = rs.getInt("skillLevel");
						if (skill instanceof AstechSkill)
							skill = new AstechSkill(PilotSkill.AstechSkillID);
						if(skill instanceof WeaponSpecialistSkill )
							p.setWeapon(rs.getString("skillData"));
						if (skill instanceof TraitSkill ) {
							String traitName = rs.getString("skillData");
							if (traitName.equalsIgnoreCase("none"))
								traitSkill = (TraitSkill)skill;
							else
								p.setTraitName(traitName);
						}
						if (skill instanceof EdgeSkill) {
							String skillString = rs.getString("skillData");
							StringTokenizer ST = new StringTokenizer("$", skillString);
							skill = new EdgeSkill(PilotSkill.EdgeSkillID);
							((EdgeSkill)skill).setTac(Boolean.parseBoolean(ST.nextToken()));
							((EdgeSkill)skill).setKO(Boolean.parseBoolean(ST.nextToken()));
							((EdgeSkill)skill).setHeadHit(Boolean.parseBoolean(ST.nextToken()));
							((EdgeSkill)skill).setExplosion(Boolean.parseBoolean(ST.nextToken()));
						}	
						skill.setLevel(level);
						skill.addToPilot(p);
						skill.modifyPilot(p);
					}
					if (traitSkill != null)
						traitSkill.assignTrait(p);
					if (p.getPilotId() == -1)
						p.setPilotId(CampaignMain.cm.getAndUpdateCurrentPilotID());
				}
				else {
					return p;
				}
		} catch (SQLException e) {
			MMServ.mmlog.dbLog("SQL Error in PilotHandler.loadPilot: " + e.getMessage());
			p = new SPilot("Vacant", 99, 99);
			return p;
		} } catch (Exception ex) {
			MMServ.mmlog.errLog("Error loading Pilot " + p.getPilotId());
			MMServ.mmlog.errLog(ex);
			p = new SPilot("Vacant", 99, 99);
			return p;
		}
    return p;
	}
	
	public SPilot loadUnitPilot(int unitID) {
		SPilot p = new SPilot();
		try {
			ResultSet rs;
			Statement stmt = con.createStatement();
			rs = stmt.executeQuery("Select pilotID from pilots WHERE unitID = " + unitID);
			while(rs.next())
				p = loadPilot(rs.getInt("pilotID"));
		} catch (SQLException e) {
			MMServ.mmlog.dbLog("SQL Error in PilotHandler.loadUnitPilot: " + e.getMessage());
		}
		return p;
	}
	
	public void unlinkPilot(int pilotID) {
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate("UPDATE pilots set factionID = NULL, playerName = NULL, unitID = NULL WHERE pilotID = " + pilotID);
			stmt.close();
		} catch (SQLException e) {
			MMServ.mmlog.dbLog("SQL Error in PilotHandler.unlinkPilot: " + e.getMessage());
		}
	}
	
	public void linkPilotToUnit(int pilotID, int unitID) {
		try {
		Statement stmt = con.createStatement();
		stmt.executeUpdate("UPDATE pilots SET factionID = NULL, playerID = NULL, unitID = " + unitID + " WHERE pilotID = " + pilotID);
		stmt.close();
		} catch (SQLException e) {
			MMServ.mmlog.dbLog("SQL Error in PilotHandler.linkPilotToUnit: " + e.getMessage());
		}
	}
	
	public void linkPilotToFaction(int pilotID, int factionId) {
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate("UPDATE pilots SET playerID = NULL, unitID = NULL, factionID = " + factionId + " WHERE pilotID = " + pilotID);
			stmt.close();
		} catch (SQLException e) {
			MMServ.mmlog.dbLog("SQL Error in PilotHandler.linkPilotToFaction: " + e.getMessage());
		}
	}

	public void linkPilotToPlayer(int pilotID, int playerID) {
		try {
			MMServ.mmlog.dbLog("Linking Pilot " + pilotID + " to Player " + playerID);
			PreparedStatement ps = con.prepareStatement("UPDATE pilots SET factionID = NULL, unitID = NULL, playerID = ? WHERE pilotID = " + pilotID);
			ps.setInt(1, playerID);
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			MMServ.mmlog.dbLog("SQL Error in PilotHandler.linkPilotToPlayer: " + e.getMessage());
		}
	}
	
	public void savePilot(SPilot p, int unitType, int unitSize) {
		try {
			if(p == null)
				return;

			if(p.getName().equalsIgnoreCase("Vacant"))
				return;
			StringBuffer sql = new StringBuffer();
			PreparedStatement ps;
			
			if(p.getDBId() == 0){
				// No pilot with this id, so INSERT
				sql.setLength(0);

				ps = con.prepareStatement("INSERT into pilots set MWID=?, pilotName=?, pilotExp=?, pilotGunnery=?, pilotPiloting=?, pilotKills=?, pilotCurrentFaction=?, pilotHits=?, pilotSize = ?, pilotType = ?", PreparedStatement.RETURN_GENERATED_KEYS);
				ps.setInt(1, p.getPilotId());
				ps.setString(2, p.getName());
				ps.setInt(3, p.getExperience());
				ps.setInt(4, p.getGunnery());
				ps.setInt(5, p.getPiloting());
				ps.setInt(6, p.getKills());
				ps.setString(7, p.getCurrentFaction());
				ps.setInt(8, p.getHits());
				ps.setInt(9, unitSize);
				ps.setInt(10, unitType);
				ps.executeUpdate();
				ResultSet rs = ps.getGeneratedKeys();
				rs.next();
				p.setDBId(rs.getInt(1));
			} else {
				// Pilot already saved, so UPDATE
				sql.setLength(0);

				ps = con.prepareStatement("UPDATE pilots set pilotName=?, pilotExp=?, pilotGunnery=?, pilotPiloting=?, pilotKills=?, pilotCurrentFaction=?, pilotHits=?, pilotSize = ?, pilotType = ?, MWID=? WHERE pilotID=?");
				ps.setString(1, p.getName());
				ps.setInt(2, p.getExperience());
				ps.setInt(3, p.getGunnery());
				ps.setInt(4, p.getPiloting());
				ps.setInt(5, p.getKills());
				ps.setString(6, p.getCurrentFaction());
				ps.setInt(7, p.getHits());
				ps.setInt(8, unitSize);
				ps.setInt(9, unitType);
				ps.setInt(10, p.getPilotId());
				ps.setInt(11, p.getDBId());
				ps.executeUpdate();
			}
			//stmt.executeUpdate(sql.toString());

			// Update pilot skills
			sql.setLength(0);
			sql.append("DELETE from pilotskills WHERE pilotID = " + p.getPilotId());
			ps.executeUpdate(sql.toString());
			
			if(p.getSkills().size() > 0) {
				Iterator it = p.getSkills().getSkillIterator();
				while(it.hasNext()) {
					SPilotSkill sk = (SPilotSkill) it.next();
					sql.setLength(0);
					sql.append("INSERT into pilotSkills set ");
					sql.append("pilotID = " + p.getPilotId() + ", ");
					sql.append("SkillNum = " + sk.getId() + ", ");
					sql.append("SkillLevel = " + sk.getLevel());
					if (sk instanceof WeaponSpecialistSkill ) {
						sql.append(", skillData = '" + p.getWeapon() + "'");
					}
					if (sk instanceof TraitSkill) {
						sql.append(", skillData = '" + p.getTraitName() + "'");
					}
					if (sk instanceof EdgeSkill) {
						sql.append(", skillData = '" + ((EdgeSkill)sk).getTac() + "$");
						sql.append(((EdgeSkill)sk).getKO() + "$");
						sql.append(((EdgeSkill)sk).getHeadHit() + "$");
						sql.append(((EdgeSkill)sk).getExplosion() + "'");
					}
					ps.executeUpdate(sql.toString());
					
					}
			}
			ps.close();
		} catch (SQLException e) {
			MMServ.mmlog.dbLog("SQL Error in PilotHandler.savePilot: " + e.getMessage());
		}

		
	}
	
	public void deletePilot(int pilotID) {
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate("DELETE from pilotskills WHERE pilotID = " + pilotID);
			stmt.executeUpdate("DELETE fron pilots WHERE pilotID = " + pilotID);
			stmt.close();
		} catch (SQLException e) {
			MMServ.mmlog.dbLog("SQL Error in PilotHandler.deletePilot: " + e.getMessage());
		}
	}
	

	public PilotHandler(Connection c) {
		this.con = c;
	}
}
