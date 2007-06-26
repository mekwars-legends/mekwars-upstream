package server.mwmysql;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Vector;

import common.Unit;



import server.MMServ;
import server.campaign.CampaignMain;
import server.campaign.SHouse;
import server.campaign.SUnit;
import server.campaign.pilot.SPilot;

public class FactionHandler {
	Connection con;
	
	public void saveFaction (SHouse h) {
		PreparedStatement ps;
		StringBuffer sql = new StringBuffer();
		ResultSet rs = null;
		Statement stmt;
		
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery("SELECT COUNT(*) as numfactions from factions WHERE fID = " + h.getId());

			rs.next();

			if(rs.getInt("numfactions")== 0) {
				// Not in the database - INSERT it
				sql.setLength(0);
				sql.append("INSERT into factions set ");
				sql.append("fName = ?, ");
				sql.append("fMoney = ?, ");
				sql.append("fColor = ?, ");
				sql.append("fAbbreviation = ?, ");
				sql.append("fLogo = ?, ");
				sql.append("fInitialHouseRanking = ?, ");
				sql.append("fConquerable = ?, ");
				sql.append("fPlayerColors = ?, ");
				sql.append("fInHouseAttacks = ?, ");
				sql.append("fAllowDefectionsFrom = ?, ");
				sql.append("fFluFile = ?, ");
				sql.append("fMOTD = ?, ");
				sql.append("fAllowDefectionsTo = ?, ");
				sql.append("fTechLevel = ?, ");
				sql.append("fBaseGunner = ?, ");
				sql.append("fBasePilot = ?, ");
				sql.append("fID = ?");

				ps = con.prepareStatement(sql.toString());
				ps.setString(1, h.getName());
				ps.setInt(2, h.getMoney());
				ps.setString(3, h.getHouseColor());
				ps.setString(4, h.getAbbreviation());
				ps.setString(5, h.getLogo());
				ps.setInt(6, h.getInitialHouseRanking());
				ps.setString(7, Boolean.toString(h.isConquerable()));
				ps.setString(8, h.getHousePlayerColor());
				ps.setString(9, Boolean.toString(h.isInHouseAttacks()));
				ps.setString(10, Boolean.toString(h.getHouseDefectionFrom()));
				ps.setString(11, h.getHouseFluFile());
				ps.setString(12, h.getMotd());
				ps.setString(13, Boolean.toString(h.getHouseDefectionTo()));
				ps.setInt(14, h.getTechLevel());
				ps.setInt(15, h.getBaseGunner());
				ps.setInt(16, h.getBasePilot());
				ps.setInt(17, h.getId());
				MMServ.mmlog.dbLog(ps.toString());
				ps.executeUpdate();
				MMServ.mmlog.dbLog("Got here");
			} else {
				// Already in the database - UPDATE it
				sql.setLength(0);
				sql.append("UPDATE factions set ");
				sql.append("fName = ?, ");
				sql.append("fMoney = ?, ");
				sql.append("fColor = ?, ");
				sql.append("fAbbreviation = ?, ");
				sql.append("fLogo = ?, ");
				sql.append("fInitialHouseRanking = ?, ");
				sql.append("fConquerable = ?, ");
				sql.append("fPlayerColors = ?, ");
				sql.append("fInHouseAttacks = ?, ");
				sql.append("fAllowDefectionsFrom = ?, ");
				sql.append("fFluFile = ?, ");
				sql.append("fMOTD = ?, ");
				sql.append("fAllowDefectionsTo = ?, ");
				sql.append("fTechLevel = ?, ");
				sql.append("fBaseGunner = ?, ");
				sql.append("fBasePilot = ? ");
				sql.append("WHERE fID = ?");
				ps = con.prepareStatement(sql.toString());
				ps.setString(1, h.getName());
				ps.setInt(2, h.getMoney());
				ps.setString(3, h.getHouseColor());
				ps.setString(4, h.getAbbreviation());
				ps.setString(5, h.getLogo());
				ps.setInt(6, h.getInitialHouseRanking());
				ps.setString(7, Boolean.toString(h.isConquerable()));
				ps.setString(8, h.getHousePlayerColor());
				ps.setString(9, Boolean.toString(h.isInHouseAttacks()));
				ps.setString(10, Boolean.toString(h.getHouseDefectionFrom()));
				ps.setString(11, h.getHouseFluFile());
				ps.setString(12, h.getMotd());
				ps.setString(13, Boolean.toString(h.getHouseDefectionTo()));
				ps.setInt(14, h.getTechLevel());
				ps.setInt(15, h.getBaseGunner());
				ps.setInt(16, h.getBasePilot());
				ps.setInt(17, h.getId());
				ps.executeUpdate();				
			}
			

		// Now we do the vectors
			
			// Mechs
			for (int i = 0; i < 4; i++) {
				Vector<SUnit> tmpVec = h.getHangar(Unit.MEK).elementAt(i);
				tmpVec.trimToSize();
				for (SUnit currU : tmpVec) {
					CampaignMain.cm.MySQL.saveUnit(currU);
				}
			}
			//Vehicles
			for (int i = 0; i < 4; i++) {
				Vector<SUnit> tmpVec = h.getHangar(Unit.VEHICLE).elementAt(i);
				tmpVec.trimToSize();
				for (SUnit currU : tmpVec) {
					CampaignMain.cm.MySQL.saveUnit(currU);
				}
			}
			//Infantry
			if(Boolean.parseBoolean(h.getConfig("UseInfantry"))) {
				for (int i = 0; i < 4; i++) {
					Vector<SUnit> tmpVec = h.getHangar(Unit.INFANTRY).elementAt(i);
					tmpVec.trimToSize();
					for (SUnit currU : tmpVec) {
						CampaignMain.cm.MySQL.saveUnit(currU);
					}
				}				
			}
			//BattleArmor
				for (int i = 0; i < 4; i++) {
					Vector<SUnit> tmpVec = h.getHangar(Unit.BATTLEARMOR).elementAt(i);
					tmpVec.trimToSize();
					for (SUnit currU : tmpVec) {
						CampaignMain.cm.MySQL.saveUnit(currU);
					}
				}
			// Protomechs
				for (int i = 0; i < 4; i++) {
					Vector<SUnit> tmpVec = h.getHangar(Unit.PROTOMEK).elementAt(i);
					tmpVec.trimToSize();
					for (SUnit currU : tmpVec) {
						CampaignMain.cm.MySQL.saveUnit(currU);
					}
				}
			// Pilot Queues
				// Mechs
				LinkedList<SPilot> PilotList = h.getPilotQueues().getPilotQueue(Unit.MEK);
				for (SPilot currP: PilotList) {
					CampaignMain.cm.MySQL.savePilot(currP);
					CampaignMain.cm.MySQL.unlinkPilot(currP.getPilotId());
					CampaignMain.cm.MySQL.linkPilotToFaction(currP.getPilotId(), h.getId());
				}
				// Vehicles
				PilotList = h.getPilotQueues().getPilotQueue(Unit.MEK);
				for (SPilot currP: PilotList) {
					CampaignMain.cm.MySQL.savePilot(currP);
					CampaignMain.cm.MySQL.unlinkPilot(currP.getPilotId());
					CampaignMain.cm.MySQL.linkPilotToFaction(currP.getPilotId(), h.getId());
				}
				
				// Infantry
				PilotList = h.getPilotQueues().getPilotQueue(Unit.MEK);
				for (SPilot currP: PilotList) {
					CampaignMain.cm.MySQL.savePilot(currP);
					CampaignMain.cm.MySQL.unlinkPilot(currP.getPilotId());
					CampaignMain.cm.MySQL.linkPilotToFaction(currP.getPilotId(), h.getId());
				}
				
				// BattleArmor
				PilotList = h.getPilotQueues().getPilotQueue(Unit.MEK);
				for (SPilot currP: PilotList) {
					CampaignMain.cm.MySQL.savePilot(currP);
					CampaignMain.cm.MySQL.unlinkPilot(currP.getPilotId());
					CampaignMain.cm.MySQL.linkPilotToFaction(currP.getPilotId(), h.getId());
				}
				
				// ProtoMechs
				PilotList = h.getPilotQueues().getPilotQueue(Unit.MEK);
				for (SPilot currP: PilotList) {
					CampaignMain.cm.MySQL.savePilot(currP);
					CampaignMain.cm.MySQL.unlinkPilot(currP.getPilotId());
					CampaignMain.cm.MySQL.linkPilotToFaction(currP.getPilotId(), h.getId());
				}
				

				// Components
				ps.executeUpdate("DELETE from factionComponents WHERE factionID = " + h.getId());
				Enumeration en = h.getComponents().keys();
				while (en.hasMoreElements()) {
					Integer id = (Integer) en.nextElement();
					Vector<Integer> v = h.getComponents().get(id);
					for (int i = 0; i < v.size(); i ++){
//						ps.executeUpdate("INSERT into factionComponents set factionID = " + h.getId() + ", unitType = " + id.intValue() + ", unitWeight = " + v + ", components = " + v.elementAt(i).intValue());
						MMServ.mmlog.dbLog("----------------");
					MMServ.mmlog.dbLog("Faction ID: " + h.getId());
					MMServ.mmlog.dbLog("Unit Type: " + id.intValue());
					MMServ.mmlog.dbLog("Unit Weight: " + v);
					MMServ.mmlog.dbLog("Components: " + v.elementAt(i).intValue());
					MMServ.mmlog.dbLog("----------------");
					}
				}

				// Pilot Skill
				ps.executeUpdate("DELETE from faction_pilot_skills WHERE factionID = " + h.getId());
				for (int pos = 0; pos < Unit.MAXBUILD; pos ++ ) {
					String skill = h.getBasePilotSkill(pos);
					ps = con.prepareStatement("INSERT into faction_pilot_skills set factionID = ?, skillName = ?, skillID = ?");
					ps.setInt(1, h.getId());
					if(skill.length() < 1)
						ps.setString(2, " ");
					else
					    ps.setString(2, skill);
					ps.setInt(3, pos);
					ps.executeUpdate();			
				}
				// BaseGunner & Pilot
				ps.executeUpdate("DELETE from faction_base_gunnery_piloting where factionId = " + h.getId());
				for (int pos = 0; pos < Unit.MAXBUILD; pos++){
					ps.executeUpdate("INSERT into faction_base_gunnery_piloting set factionID = " + h.getId() + ", unitType = " + pos + ", baseGunnery = " + h.getBaseGunner(pos) + ", basePiloting = " + h.getBasePilot(pos));
				}
				
			} catch (SQLException e) {
			MMServ.mmlog.dbLog("SQL Error in FactionHandler.saveFaction: " + e.getMessage());
		}	
	}
	
	public FactionHandler (Connection c) {
		this.con = c;
	}

}
