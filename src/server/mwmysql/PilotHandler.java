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
import java.util.StringTokenizer;

import common.campaign.pilot.skills.PilotSkill;

import common.CampaignData;
import server.campaign.CampaignMain;
import server.campaign.SUnit;
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
					rs.close();
					stmt.close();
					return p;
				}
				rs.close();
				stmt.close();
		} catch (SQLException e) {
			CampaignData.mwlog.dbLog("SQL Error in PilotHandler.loadPilot: " + e.getMessage());
			p = new SPilot("Vacant", 99, 99);
			return p;
		} } catch (Exception ex) {
			CampaignData.mwlog.errLog("Error loading Pilot " + p.getPilotId());
			CampaignData.mwlog.errLog(ex);
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
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			CampaignData.mwlog.dbLog("SQL Error in PilotHandler.loadUnitPilot: " + e.getMessage());
            CampaignData.mwlog.dbLog(e);
		}
		return p;
	}
	
	public void deletePilot(int pilotID) {
		int DBId = getPilotDBId(pilotID);
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate("DELETE from pilotskills WHERE pilotID = " + DBId);
			stmt.executeUpdate("DELETE from pilots WHERE pilotID = " + DBId);
			stmt.close();
		} catch (SQLException e) {
			CampaignData.mwlog.dbLog("SQL Error in PilotHandler.deletePilot: " + e.getMessage());
            CampaignData.mwlog.dbLog(e);
		}
	}
	
	public void deleteFactionPilots(int factionID) {
		Statement stmt;
		ResultSet rs;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery("SELECT pilotID from pilots WHERE factionID = " + factionID);
			while(rs.next()) {
				stmt.executeUpdate("DELETE from pilotskills WHERE pilotID = " + rs.getInt("pilotID"));
				stmt.executeUpdate("DELETE from pilots WHERE pilotID = " + rs.getInt("pilotID"));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			CampaignData.mwlog.dbLog("SQL Error in PilotHandler.deleteFactionPilots: " + e.getMessage());
            CampaignData.mwlog.dbLog(e);
		}
	}
		
	public void deletePlayerPilots(int playerID) {
		Statement stmt, stmt1;
		ResultSet rs;
		try {
			stmt = con.createStatement();
			stmt1 = con.createStatement();
			rs = stmt.executeQuery("SELECT pilotID from pilots WHERE playerID = " + playerID);
			while(rs.next()) {
				stmt1.executeUpdate("DELETE from pilotskills WHERE pilotID = " + rs.getInt("pilotID"));
				stmt1.executeUpdate("DELETE from pilots WHERE pilotID = " + rs.getInt("pilotID"));
			}
			stmt.close();
			stmt1.close();
			rs.close();
		} catch (SQLException e) {
			CampaignData.mwlog.dbLog("SQL Error in PilotHandler.deleteFactionPilots: " + e.getMessage());
            CampaignData.mwlog.dbLog(e);
		}
	}
	
	public void deletePlayerPilots(int playerID, int unitType, int unitWeight) {
		Statement stmt, stmt1;
		ResultSet rs;
		try {
			stmt = con.createStatement();
			stmt1 = con.createStatement();
			rs = stmt.executeQuery("SELECT pilotID from pilots WHERE playerID = " + playerID + " AND pilotType = " + unitType + " AND pilotSize = " + unitWeight);
			while(rs.next()) {
				stmt1.executeUpdate("DELETE from pilotskills WHERE pilotID = " + rs.getInt("pilotID"));
				stmt1.executeUpdate("DELETE from pilots WHERE pilotID = " + rs.getInt("pilotID"));
			}
			rs.close();
			stmt.close();
			stmt1.close();
		} catch (SQLException e) {
			CampaignData.mwlog.dbLog("SQL Error in PilotHandler.deleteFactionPilots: " + e.getMessage());
            CampaignData.mwlog.dbLog(e);
		}
	}
	
	public void deleteFactionPilots(int factionID, int type) {
		Statement stmt, stmt1;
		ResultSet rs;
		try {
			stmt = con.createStatement();
			stmt1 = con.createStatement();
			rs = stmt.executeQuery("SELECT pilotID from pilots WHERE factionID = " + factionID + " AND pilotType = " + type);
			while(rs.next()) {
				stmt1.executeUpdate("DELETE from pilotskills WHERE pilotID = " + rs.getInt("pilotID"));
				stmt1.executeUpdate("DELETE from pilots WHERE pilotID = " + rs.getInt("pilotID"));
			}
			rs.close();
			stmt.close();
			stmt1.close();
		} catch (SQLException e) {
			CampaignData.mwlog.dbLog("SQL Error in PilotHandler.deleteFactionPilots: " + e.getMessage());
            CampaignData.mwlog.dbLog(e);
		}
	}
	
	public int getPilotDBId(int pilotID) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		int DBId = -1;
		try {
			ps = con.prepareStatement("SELECT pilotID from pilots WHERE MWID = ?");
			ps.setInt(1, pilotID);
			rs = ps.executeQuery();
			if(rs.next())
				DBId = rs.getInt("pilotID");
		} catch (SQLException e) {
			CampaignData.mwlog.dbLog("SQLException in PilotHandler.getPilotDBID: " + e.getMessage());
			CampaignData.mwlog.dbLog(e);
		} finally {
			try {
				if(rs != null)
					rs.close();
				if(ps != null)
					ps.close();
			} catch (SQLException ex) {
				
			}
			if(DBId == -1)
				CampaignData.mwlog.dbLog("GetPilotDBId returned -1 for pilotID: " + pilotID);
		}
	return DBId;
	}
	
	public PilotHandler(Connection c) {
		this.con = c;
	}
}
