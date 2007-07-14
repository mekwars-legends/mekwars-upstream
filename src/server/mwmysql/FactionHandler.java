package server.mwmysql;


import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Vector;

import common.CampaignData;
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
		MMServ.mmlog.dbLog("Saving Faction " + h.getName());
		PreparedStatement ps;
		StringBuffer sql = new StringBuffer();
		ResultSet rs;
		try {
			if(h.getDBId()== 0) {
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

				ps = con.prepareStatement(sql.toString(), PreparedStatement.RETURN_GENERATED_KEYS);
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
				ps.executeUpdate();
				rs = ps.getGeneratedKeys();
				rs.next();
				h.setDBId(rs.getInt(1));
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
				sql.append("WHERE ID = ?");
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
				ps.setInt(17, h.getDBId());
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
				ps.executeUpdate("DELETE from factionComponents WHERE factionID = " + h.getDBId());
				Enumeration en = h.getComponents().keys();
				while (en.hasMoreElements()) {
					Integer id = (Integer) en.nextElement();
					Vector<Integer> v = h.getComponents().get(id);
					for (int i = 0; i < v.size(); i ++){
						ps.executeUpdate("INSERT into factionComponents set factionID = " + h.getDBId() + ", unitType = " + id.intValue() + ", unitWeight = " + i + ", components = " + v.elementAt(i).intValue());
					}
				}

				// Pilot Skill
				// Change this so it doesn't save if it's blank.
				ps.executeUpdate("DELETE from faction_pilot_skills WHERE factionID = " + h.getDBId());
				for (int pos = 0; pos < Unit.MAXBUILD; pos ++ ) {
					String skill = h.getBasePilotSkill(pos);

 					ps = con.prepareStatement("INSERT into faction_pilot_skills set factionID = ?, skillID = ?, pilotSkills = ?");
					ps.setInt(1, h.getDBId());
					ps.setString(3, skill);
					ps.setInt(2, pos);
					ps.executeUpdate();
			
				}
				// BaseGunner & Pilot
				ps.executeUpdate("DELETE from faction_base_gunnery_piloting where factionId = " + h.getDBId());
				for (int pos = 0; pos < Unit.MAXBUILD; pos++){
					ps.executeUpdate("INSERT into faction_base_gunnery_piloting set factionID = " + h.getDBId() + ", unitType = " + pos + ", baseGunnery = " + h.getBaseGunner(pos) + ", basePiloting = " + h.getBasePilot(pos));
				}
				} catch (SQLException e) {
			MMServ.mmlog.dbLog("SQL Error in FactionHandler.saveFaction: " + e.getMessage());
		}	
	}
	
	public void loadFactions(CampaignData data) {
		try {
			ResultSet rs, rs1;
			Statement stmt = con.createStatement();
			Statement stmt2 = con.createStatement();
			rs = stmt.executeQuery("SELECT * from factions ORDER BY ID");
			while(rs.next()) {
				SHouse h;
				boolean newbieHouse = Boolean.parseBoolean(rs.getString("fIsNewbieHouse"));
				boolean mercHouse = Boolean.parseBoolean(rs.getString("fIsMercHouse"));
				if (newbieHouse)
					h = new NewbieHouse(data.getUnusedHouseID());
				else if (mercHouse)
					h = new MercHouse(data.getUnusedHouseID());
				else
					h = new SHouse(data.getUnusedHouseID());
				h.setName(rs.getString("fName"));
				MMServ.mmlog.createFactionLogger(h.getName());
				MMServ.mmlog.dbLog("Loading faction " + h.getName());
				h.setDBId(rs.getInt("ID"));
				h.setMoney(rs.getInt("fMoney"));
				h.setHouseColor(rs.getString("fColor"));
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
				h.setId(-1);
				h.setHousePlayerColors(rs.getString("fPlayerColors"));
				h.setHouseDefectionFrom(Boolean.parseBoolean(rs.getString("fAllowDefectionsFrom")));
				h.setHouseDefectionTo(Boolean.parseBoolean(rs.getString("fAllowDefectionsTo")));
				h.setHouseFluFile(rs.getString("fFluFile"));
				h.setMotd(rs.getString("fMOTD"));
				h.setTechLevel(rs.getInt("fTechLevel"));
				// Now the vectors
				
				//Load the Meks
				MMServ.mmlog.dbLog("Loading Meks");
				rs1 = stmt2.executeQuery("SELECT MWID from units WHERE uType = " + Unit.MEK + " AND uFactionID = " + h.getDBId());
				while(rs1.next()) {
					MMServ.mmlog.dbLog("Loading Unit " + rs1.getInt("MWID"));
					SUnit u = CampaignMain.cm.MySQL.loadUnit(rs1.getInt("MWID"));
					if (u == null)
						continue;
					if ( newbieHouse ){
						int priceForUnit = h.getPriceForUnit(u.getWeightclass(), u.getType());
						int rareSalesTime = Integer.parseInt(h.getConfig("RareMinSaleTime"));
						CampaignMain.cm.getMarket().addListing("Faction_" + h.getName(), u,priceForUnit, rareSalesTime);
						u.setStatus(Unit.STATUS_FORSALE);
					}
					MMServ.mmlog.dbLog ("Returned from loading mek");
					try {
						h.addUnit(u, false);
					} catch (Exception ex) {
						MMServ.mmlog.dbLog("Exception at addUnit: ");
						MMServ.mmlog.dbLog(ex.toString());
						MMServ.mmlog.dbLog(ex.getStackTrace().toString());
					}
					MMServ.mmlog.dbLog("Unit " + rs1.getInt("MWID") + " loaded");
				}
				
				//Load the Vees
				MMServ.mmlog.dbLog("Loading Vees");
				rs1 = stmt2.executeQuery("SELECT MWID from units WHERE uType = " + Unit.VEHICLE + " AND uFactionID = " + h.getDBId());
				while(rs1.next()) {
					SUnit u = CampaignMain.cm.MySQL.loadUnit(rs1.getInt("MWID"));
					if (u == null)
						continue;
					if ( newbieHouse ){
						int priceForUnit = h.getPriceForUnit(u.getWeightclass(), u.getType());
						int rareSalesTime = Integer.parseInt(h.getConfig("RareMinSaleTime"));
						CampaignMain.cm.getMarket().addListing("Faction_" + h.getName(), u,priceForUnit, rareSalesTime);
						u.setStatus(Unit.STATUS_FORSALE);
					}
					h.addUnit(u, false);
				}
				
				
				//Load the Infantry
				MMServ.mmlog.dbLog("Loading Infantry");
				if (Boolean.parseBoolean(h.getConfig("UseInfantry"))) {
					rs1 = stmt2.executeQuery("SELECT MWID from units WHERE uType = " + Unit.INFANTRY + " AND uFactionID = " + h.getDBId());
					while(rs1.next()) {
						SUnit u = CampaignMain.cm.MySQL.loadUnit(rs1.getInt("MWID"));
						if (u == null) {
						    MMServ.mmlog.dbLog("Null Unit");
							continue;
						}
						
						if ( newbieHouse ){
							int priceForUnit = h.getPriceForUnit(u.getWeightclass(), u.getType());
							int rareSalesTime = Integer.parseInt(h.getConfig("RareMinSaleTime"));
							CampaignMain.cm.getMarket().addListing("Faction_" + h.getName(), u,priceForUnit, rareSalesTime);
							u.setStatus(Unit.STATUS_FORSALE);
						}
						MMServ.mmlog.dbLog("Adding Unit");
						h.addUnit(u, false);
					}
				}
				
				//Load the Protomeks
				MMServ.mmlog.dbLog("Loading Protos");
				rs1 = stmt2.executeQuery("SELECT MWID from units WHERE uType = " + Unit.PROTOMEK + " AND uFactionID = " + h.getDBId());
				while(rs1.next()) {
					SUnit u = CampaignMain.cm.MySQL.loadUnit(rs1.getInt("MWID"));
					if (u == null)
						continue;
					if ( newbieHouse ){
						int priceForUnit = h.getPriceForUnit(u.getWeightclass(), u.getType());
						int rareSalesTime = Integer.parseInt(h.getConfig("RareMinSaleTime"));
						CampaignMain.cm.getMarket().addListing("Faction_" + h.getName(), u,priceForUnit, rareSalesTime);
						u.setStatus(Unit.STATUS_FORSALE);
					}
					h.addUnit(u, false);
				}
				
				
				//Load the BattleArmor
				MMServ.mmlog.dbLog("Loading BA");
				rs1 = stmt2.executeQuery("SELECT MWID from units WHERE uType = " + Unit.BATTLEARMOR + " AND uFactionID = " + h.getDBId());
				while(rs1.next()) {
					SUnit u = CampaignMain.cm.MySQL.loadUnit(rs1.getInt("MWID"));
					if (u == null)
						continue;
					if ( newbieHouse ){
						int priceForUnit = h.getPriceForUnit(u.getWeightclass(), u.getType());
						int rareSalesTime = Integer.parseInt(h.getConfig("RareMinSaleTime"));
						CampaignMain.cm.getMarket().addListing("Faction_" + h.getName(), u,priceForUnit, rareSalesTime);
						u.setStatus(Unit.STATUS_FORSALE);
					}
					h.addUnit(u, false);
				}
				
			
				
				//Load the components
				MMServ.mmlog.dbLog("Loading Components");
				rs1 = stmt2.executeQuery("SELECT * from factioncomponents where factionID = " + h.getDBId());
				while (rs1.next()) {
					h.getComponents().get(rs1.getInt("unitType")).setElementAt(rs1.getInt("components"), rs1.getInt("unitWeight"));
				}
				
				MMServ.mmlog.dbLog("Loading Base Gunnery & Piloting");
				rs1 = stmt2.executeQuery("SELECT * from faction_base_gunnery_piloting WHERE factionID = " + h.getDBId());
					while(rs1.next()) {
					h.setBaseGunner(rs1.getInt("baseGunnery"), rs1.getInt("unitType"));
					h.setBasePilot(rs1.getInt("basePiloting"), rs1.getInt("unitType"));
					}
				
					MMServ.mmlog.dbLog("Loading Default Pilot Skills");
				rs1 = stmt2.executeQuery("SELECT * from faction_pilot_skills WHERE factionID = " + h.getDBId());
					while(rs1.next()) {
						int skillID = rs1.getInt("skillID");
						h.setBasePilotSkill(rs1.getString("pilotSkills"), skillID);
				}
				//TODO: Load the Merc stuff.  Don't forget to change saveFaction to save the Merc stuff
				
				CampaignMain.cm.addHouse(h);
				MMServ.mmlog.dbLog("Loading Faction Pilots");
				CampaignMain.cm.MySQL.loadFactionPilots(h);

				MMServ.mmlog.dbLog("Faction " + h.getName() + " loaded");
				}
			
		} catch (SQLException e) {
			e.printStackTrace();
			MMServ.mmlog.dbLog("SQL Error in FactionHandler.loadFaction: " + e.getMessage());
		}
	}
	
	public int countFactions() {
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as num from factions");
			rs.next();
			
			return rs.getInt("num");
		} catch (SQLException e) {
			MMServ.mmlog.dbLog("SQL Error in FactionHandler.countFactions: " + e.getMessage());
			return 0;
		}
	}
	
	public FactionHandler (Connection c) {
		this.con = c;
	}

}
