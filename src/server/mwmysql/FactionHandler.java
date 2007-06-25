package server.mwmysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
		ResultSet rs;
		
		try {
			ps = con.prepareStatement("SELECT COUNT(*) as num from factions WHERE factionID = ?");
			ps.setInt(1, h.getId());
			rs = ps.executeQuery();
			rs.next();
			if(rs.getInt("num")== 0) {
				// Not in the database - INSERT it
				sql.setLength(0);
				sql.append("INSERT into factions set ");
				sql.append("factionName = ?, ");
				sql.append("factionMoney = ?, ");
				sql.append("factionColor = ?, ");
				sql.append("factionAbbreviation = ?, ");
				sql.append("factionLogo = ?, ");
				sql.append("factionInitialHouseRanking = ?, ");
				sql.append("factionConquerable = ?, ");
				sql.append("factionPlayerColors = ?, ");
				sql.append("factionInHouseAttacks = ?, ");
				sql.append("factionAllowDefectionsFrom = ?, ");
				sql.append("factionFluFile = ?, ");
				sql.append("factionMOTD = ?, ");
				sql.append("factionAllowDefectionsTo = ?, ");
				sql.append("factionTechLevel = ?, ");
				sql.append("factionBaseGunner = ?, ");
				sql.append("factionBasePilot = ?, ");
				sql.append("factionID = ?");
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
			} else {
				// Already in the database - UPDATE it
				sql.setLength(0);
				sql.setLength(0);
				sql.append("UPDATE factions set ");
				sql.append("factionName = ?, ");
				sql.append("factionMoney = ?, ");
				sql.append("factionColor = ?, ");
				sql.append("factionAbbreviation = ?, ");
				sql.append("factionLogo = ?, ");
				sql.append("factionInitialHouseRanking = ?, ");
				sql.append("factionConquerable = ?, ");
				sql.append("factionPlayerColors = ?, ");
				sql.append("factionInHouseAttacks = ?, ");
				sql.append("factionAllowDefectionsFrom = ?, ");
				sql.append("factionFluFile = ?, ");
				sql.append("factionMOTD = ?, ");
				sql.append("factionAllowDefectionsTo = ?, ");
				sql.append("factionTechLevel = ?, ");
				sql.append("factionBaseGunner = ?, ");
				sql.append("factionBasePilot = ? ");
				sql.append("WHERE factionID = ?");
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
				
				// Pilot Skill
				
				// BaseGunner
				
				// BasePilot
				
				/**
				 * 		// Write the Components / BuildingPP's
		result.append("Components" + "|");
		Enumeration e = getComponents().keys();
		while (e.hasMoreElements()) {
			Integer id = (Integer) e.nextElement();
			Vector<Integer> v = getComponents().get(id);
			result.append(id.intValue() + "|" + v.size() + "|");
			for (int i = 0; i < v.size(); i++)
				result.append(v.elementAt(i).intValue() + "|");
		}
	

		

        for ( int pos = 0; pos < Unit.MAXBUILD; pos++ ){
            result.append(getBaseGunner(pos));
            result.append("|");
            result.append(getBasePilot(pos));
            result.append("|");
        }

        for ( int pos = 0; pos < Unit.MAXBUILD; pos++ ){
        	String skill = getBasePilotSkill(pos);
        	if ( skill.length() < 1)
        		result.append(" ");
        	else
        		result.append(skill);
            result.append("|");
        }
				 */
				
		} catch (SQLException e) {
			MMServ.mmlog.dbLog("SQL Error in FactionHandler.saveFaction: " + e.getMessage());
		}

		
	}
	
	public FactionHandler (Connection c) {
		this.con = c;
	}

}
