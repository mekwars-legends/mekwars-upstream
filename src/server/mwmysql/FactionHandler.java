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
import server.campaign.NewbieHouse;
import server.campaign.mercenaries.MercHouse;

public class FactionHandler {
	Connection con;
	
	public void saveFaction (SHouse h) {
		//TODO: modify this to save the Merc stuff if it's a Merc house
		
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
				sql.append("fIsNewbieHouse = ?, ");
				sql.append("fIsMercHouse = ?");
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
				ps.setString(17, Boolean.toString(h.isNewbieHouse()));
				ps.setString(18, Boolean.toString(h.isMercHouse()));
				ps.setInt(19, h.getId());
				ps.executeUpdate();
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
					CampaignMain.cm.MySQL.savePilot(currP, Unit.MEK, -1);
				}
				// Vehicles
				PilotList = h.getPilotQueues().getPilotQueue(Unit.VEHICLE);
				for (SPilot currP: PilotList) {
					CampaignMain.cm.MySQL.savePilot(currP, Unit.VEHICLE, -1);
				}
				
				// Infantry
				PilotList = h.getPilotQueues().getPilotQueue(Unit.INFANTRY);
				for (SPilot currP: PilotList) {
					CampaignMain.cm.MySQL.savePilot(currP, Unit.INFANTRY, -1);
				}
				
				// BattleArmor
				PilotList = h.getPilotQueues().getPilotQueue(Unit.BATTLEARMOR);
				for (SPilot currP: PilotList) {
					CampaignMain.cm.MySQL.savePilot(currP, Unit.BATTLEARMOR, -1);
				}
				
				// ProtoMechs
				PilotList = h.getPilotQueues().getPilotQueue(Unit.PROTOMEK);
				for (SPilot currP: PilotList) {
					CampaignMain.cm.MySQL.savePilot(currP, Unit.PROTOMEK, -1);
				}
				

				// Components
				ps.executeUpdate("DELETE from factionComponents WHERE factionID = " + h.getId());
				Enumeration en = h.getComponents().keys();
				while (en.hasMoreElements()) {
					Integer id = (Integer) en.nextElement();
					Vector<Integer> v = h.getComponents().get(id);
					for (int i = 0; i < v.size(); i ++){
						ps.executeUpdate("INSERT into factionComponents set factionID = " + h.getId() + ", unitType = " + id.intValue() + ", unitWeight = " + i + ", components = " + v.elementAt(i).intValue());
					}
				}

				// Pilot Skill
				// Change this so it doesn't save if it's blank.
				ps.executeUpdate("DELETE from faction_pilot_skills WHERE factionID = " + h.getId());
				for (int pos = 0; pos < Unit.MAXBUILD; pos ++ ) {
					String skill = h.getBasePilotSkill(pos);

 					ps = con.prepareStatement("INSERT into faction_pilot_skills set factionID = ?, skillID = ?, pilotSkills = ?");
					ps.setInt(1, h.getId());
					ps.setString(3, skill);
					ps.setInt(2, pos);
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
	
	public void loadFactions() {
		try {
			ResultSet rs, rs1;
			Statement stmt = con.createStatement();
			rs = stmt.executeQuery("SELECT * from factions ORDER BY fID");
			while(rs.next()) {
				SHouse h;
				boolean newbieHouse = Boolean.parseBoolean(rs.getString("fIsNewbieHouse"));
				boolean mercHouse = Boolean.parseBoolean(rs.getString("fIsMercHouse"));
				if (newbieHouse)
					h = new NewbieHouse(rs.getInt("fID"));
				else if (mercHouse)
					h = new MercHouse(rs.getInt("fID"));
				else
					h = new SHouse(rs.getInt("fID"));
				MMServ.mmlog.createFactionLogger(h.getName());
				h.setMoney(rs.getInt("fMoney"));
				h.setHouseColor(rs.getString("fHouseColor"));
				h.setBaseGunner(rs.getInt("fBaseGunner"));
				h.setBasePilot(rs.getInt("fBasePilot"));
				h.setAbbreviation(rs.getString("fAbbreviation"));
				h.getHangar().put(new Integer(Unit.MEK), new Vector<Vector<SUnit>>(5, 1));
				h.getHangar().put(new Integer(Unit.VEHICLE), new Vector<Vector<SUnit>>(5, 1));
				h.getHangar().put(new Integer(Unit.INFANTRY), new Vector<Vector<SUnit>>(5, 1));
				h.getHangar().put(new Integer(Unit.PROTOMEK), new Vector<Vector<SUnit>>(5, 1));
				h.getHangar().put(new Integer(Unit.BATTLEARMOR), new Vector<Vector<SUnit>>(5, 1));
				// Init all of the hangars
				for (int i = 0; i < 4; i++) {

					h.getHangar(Unit.MEK).add(new Vector<SUnit>());
					h.getHangar(Unit.VEHICLE).add(new Vector<SUnit>());
					h.getHangar(Unit.INFANTRY).add(new Vector<SUnit>());
					h.getHangar(Unit.BATTLEARMOR).add(new Vector<SUnit>());
					h.getHangar(Unit.PROTOMEK).add(new Vector<SUnit>());
				}
				h.getComponents().put(Unit.MEK, new Vector<Integer>(4, 1));
				h.getComponents().put(Unit.VEHICLE, new Vector<Integer>(4, 1));
				h.getComponents().put(Unit.INFANTRY, new Vector<Integer>(4, 1));
				h.getComponents().put(Unit.PROTOMEK, new Vector<Integer>(4, 1));
				h.getComponents().put(Unit.BATTLEARMOR, new Vector<Integer>(4, 1));
				
				for (int i = 0; i < 4; i++) {
					h.getComponents().get(Unit.MEK).add(0);
					h.getComponents().get(Unit.VEHICLE).add(0);
					h.getComponents().get(Unit.INFANTRY).add(0);
					h.getComponents().get(Unit.PROTOMEK).add(0);
					h.getComponents().get(Unit.BATTLEARMOR).add(0);
				}
				h.setInitialHouseRanking(rs.getInt("fInitialHouseRanking"));
				h.setConquerable(Boolean.parseBoolean(rs.getString("fConquerable")));
				h.setInHouseAttacks(Boolean.parseBoolean(rs.getString("fInHouseAttacks")));
				h.setId(rs.getInt("fID"));
				h.setHousePlayerColors(rs.getString("fPlayerColors"));
				h.setHouseDefectionFrom(Boolean.parseBoolean(rs.getString("fAllowDefectionsFrom")));
				h.setHouseDefectionTo(Boolean.parseBoolean(rs.getString("fAllowDefectionsTo")));
				h.setHouseFluFile(rs.getString("fFluFile"));
				h.setMotd(rs.getString("fMOTD"));
				h.setTechLevel(rs.getInt("fTechLevel"));
				
				// Now the vectors
				
				 //TODO: Load the Meks
				
				
				//TODO: Load the Vees
				
				
				//TODO: Load the Infantry
				
				
				//TODO: Load the Protomeks
				
				
				//TODO: Load the BattleArmor
				
				
				//TODO: Load the components
				
				
				//TODO: Load the pilotqueues
				
				
				//TODO: Load the baseGunner / BasePilot vectors
				rs1 = stmt.executeQuery("SELECT * from faction_base_gunnery_piloting WHERE factionID = " + h.getId());
				while(rs1.next()) {
					
				}
				
				//TODO: Load the house Piloting skills
				
				
				//TODO: Load the Merc stuff.  Don't forget to change saveFaction to save the Merc stuff
				
				
				
				
				CampaignMain.cm.addHouse(h);
			}
			
		} catch (SQLException e) {
			MMServ.mmlog.dbLog("SQL Error in FactionHandler.saveFactions: " + e.getMessage());
		}
	}
	
	public FactionHandler (Connection c) {
		this.con = c;
	}

}
