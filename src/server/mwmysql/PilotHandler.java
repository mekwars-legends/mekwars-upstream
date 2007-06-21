package server.mwmysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import server.MMServ;
import server.campaign.pilot.SPilot;
import server.campaign.pilot.skills.EdgeSkill;
import server.campaign.pilot.skills.SPilotSkill;
import server.campaign.pilot.skills.TraitSkill;
import server.campaign.pilot.skills.WeaponSpecialistSkill;

public class PilotHandler {

	Connection con;
	
	public void linkPilotToUnit(int pilotID, int unitID) {
		try {
		unlinkPilotFromUnit(pilotID);
			
		Statement stmt = con.createStatement();
		stmt.executeUpdate("INSERT into pilots_to_units SET pilotID = " + pilotID + ", unitID = " + unitID);
		stmt.close();
		} catch (SQLException e) {
			MMServ.mmlog.dbLog("SQL Error in PilotHandler.linkPilotToUnit: " + e.getMessage());
		}
	}
	
	public void unlinkPilotFromUnit(int pilotID) {
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate("DELETE from pilots_to_units WHERE pilotID = " + pilotID);
			stmt.close();
		} catch (SQLException e) {
			MMServ.mmlog.dbLog("SQL Error in PilotHandler.unlinkPilot: " + e.getMessage());
		}

	}
	
	public void savePilot(SPilot p) {
		try {
			ResultSet rs = null;
			Statement stmt = con.createStatement();	
			
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT COUNT(*) as num from pilots WHERE pilotID=" + p.getPilotId());
			rs = stmt.executeQuery(sql.toString());
			rs.next();
			if(rs.getInt("num") == 0){
				// No pilot with this id, so INSERT
				sql.setLength(0);
				/*
				sql.append("INSERT into pilots set ");
				sql.append("pilotID = " + p.getPilotId() + ", ");
				sql.append("pilotName = '" + p.getName() + "', ");
				sql.append("pilotExp = " + p.getExperience() + ", ");
				sql.append("pilotGunnery = " + p.getGunnery() + ", ");
				sql.append("pilotPiloting = " + p.getPiloting() + ", ");
				sql.append("pilotKills = " + p.getKills() + ", ");
				sql.append("pilotCurrentFaction = '" + p.getCurrentFaction() + "', ");
				sql.append("pilotHits = " + p.getHits());
				*/
				PreparedStatement ps;
				ps = con.prepareStatement("INSERT into pilots set pilotID=?, pilotName=?, pilotExp=?, pilotGunnery=?, pilotPiloting=?, pilotKills=?, pilotCurrentFaction=?, pilotHits=?");
				ps.setInt(1, p.getPilotId());
				ps.setString(2, p.getName());
				ps.setInt(3, p.getExperience());
				ps.setInt(4, p.getGunnery());
				ps.setInt(5, p.getPiloting());
				ps.setInt(6, p.getKills());
				ps.setString(7, p.getCurrentFaction());
				ps.setInt(8, p.getHits());
				ps.executeUpdate();
			} else {
				// Pilot already saved, so UPDATE
				sql.setLength(0);
				/*
				 
				sql.append("UPDATE pilots set ");
				sql.append("pilotName = '" + p.getName() + "', ");
				sql.append("pilotExp = " + p.getExperience() + ", ");
				sql.append("pilotGunnery = " + p.getGunnery() + ", ");
				sql.append("pilotPiloting = " + p.getPiloting() + ", ");
				sql.append("pilotKills = " + p.getKills() + ", ");
				sql.append("pilotCurrentFaction = '" + p.getCurrentFaction() + "', ");
				sql.append("pilotHits = " + p.getHits());
				sql.append(" WHERE pilotID = " + p.getPilotId());
				*/
				PreparedStatement ps;
				ps = con.prepareStatement("UPDATE pilots set pilotName=?, pilotExp=?, pilotGunnery=?, pilotPiloting=?, pilotKills=?, pilotCurrentFaction=?, pilotHits=? WHERE pilotID=?");
				ps.setString(1, p.getName());
				ps.setInt(2, p.getExperience());
				ps.setInt(3, p.getGunnery());
				ps.setInt(4, p.getPiloting());
				ps.setInt(5, p.getKills());
				ps.setString(6, p.getCurrentFaction());
				ps.setInt(7, p.getHits());
				ps.setInt(8, p.getPilotId());
				ps.executeUpdate();
			}
			//stmt.executeUpdate(sql.toString());

			// Update pilot skills
			sql.setLength(0);
			sql.append("DELETE from pilotskills WHERE pilotID = " + p.getPilotId());
			stmt.executeUpdate(sql.toString());
			
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
					stmt.executeUpdate(sql.toString());
					}
			}
			stmt.close();
		} catch (SQLException e) {
			MMServ.mmlog.dbLog("SQL Error in PilotHandler: " + e.getMessage());
		}

		
	}
	
	public PilotHandler(Connection c) {
		this.con = c;
	}
}
