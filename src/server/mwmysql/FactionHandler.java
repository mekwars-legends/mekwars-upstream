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
import java.util.Hashtable;
import java.util.Vector;

import server.campaign.CampaignMain;
import server.campaign.NewbieHouse;
import server.campaign.SHouse;
import server.campaign.SUnit;
import server.campaign.mercenaries.ContractInfo;
import server.campaign.mercenaries.MercHouse;

import common.CampaignData;
import common.SubFaction;
import common.Unit;

public class FactionHandler {
	JDBCConnectionHandler ch = new JDBCConnectionHandler();
	
	public void loadFactions(CampaignData data) {
		ResultSet rs = null;
		ResultSet rs1 = null;
		Statement stmt = null;
		Statement stmt2 = null;
		Connection con = ch.getConnection();
		
		try {
			stmt = con.createStatement();
			stmt2 = con.createStatement();
			rs = stmt.executeQuery("SELECT * from factions ORDER BY ID");
			while(rs.next()) {
				SHouse h;
				boolean newbieHouse = rs.getBoolean("fIsNewbieHouse");
				boolean mercHouse = rs.getBoolean("fIsMercHouse");
				if (newbieHouse)
					h = new NewbieHouse(data.getUnusedHouseID());
				else if (mercHouse)
					h = new MercHouse(data.getUnusedHouseID());
				else
					h = new SHouse(data.getUnusedHouseID());
				String fString = rs.getString("fString");
				if(fString != null && fString.trim().length() > 0) {
					// new save format
					if(fString.startsWith("[N]"))
						fString = fString.substring(3);
					if(fString.startsWith("[C]"))
						fString = fString.substring(3);
					if(fString.startsWith("[M]"))
						fString = fString.substring(3);
					h.fromString(fString, CampaignMain.cm.getR());
					h.setDBId(rs.getInt("ID"));
					CampaignMain.cm.addHouse(h);
					h.loadConfigFileFromDB();
					h.setUsedMekBayMultiplier(Float.parseFloat(h.getConfig("UsedPurchaseCostMulti")));
					if(CampaignMain.cm.isUsingIncreasedTechs())
						h.addCommonUnitSupport();
					if(CampaignMain.cm.isSynchingBB()) {
						h.setForumName(h.getConfig("ForumGroupName"));
						h.setForumID(CampaignMain.cm.MySQL.getHouseForumID(h.getForumName()));
					}

					CampaignData.mwlog.dbLog("Faction " + h.getName() + " loaded");
					continue;
				}
				h.setName(rs.getString("fName"));
				CampaignData.mwlog.createFactionLogger(h.getName());
				CampaignData.mwlog.dbLog("Loading faction " + h.getName());
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
                h.getHangar().put(new Integer(Unit.AERO), new Vector<Vector<SUnit>>(5, 1));
				// Init all of the hangars
				for (int i = 0; i < 4; i++) {

					h.getHangar(Unit.MEK).add(new Vector<SUnit>(1,1));
					h.getHangar(Unit.VEHICLE).add(new Vector<SUnit>(1,1));
					h.getHangar(Unit.INFANTRY).add(new Vector<SUnit>(1,1));
					h.getHangar(Unit.BATTLEARMOR).add(new Vector<SUnit>(1,1));
					h.getHangar(Unit.PROTOMEK).add(new Vector<SUnit>(1,1));
                    h.getHangar(Unit.AERO).add(new Vector<SUnit>(1,1));
				}
				h.getComponents().put(Unit.MEK, new Vector<Integer>(4, 1));
				h.getComponents().put(Unit.VEHICLE, new Vector<Integer>(4, 1));
				h.getComponents().put(Unit.INFANTRY, new Vector<Integer>(4, 1));
				h.getComponents().put(Unit.PROTOMEK, new Vector<Integer>(4, 1));
				h.getComponents().put(Unit.BATTLEARMOR, new Vector<Integer>(4, 1));
                h.getComponents().put(Unit.AERO, new Vector<Integer>(4, 1));
				
				for (int i = 0; i < 4; i++) {
					h.getComponents().get(Unit.MEK).add(0);
					h.getComponents().get(Unit.VEHICLE).add(0);
					h.getComponents().get(Unit.INFANTRY).add(0);
					h.getComponents().get(Unit.PROTOMEK).add(0);
					h.getComponents().get(Unit.BATTLEARMOR).add(0);
                    h.getComponents().get(Unit.AERO).add(0);
				}
				h.setInitialHouseRanking(rs.getInt("fInitialHouseRanking"));
				h.setConquerable(rs.getBoolean("fConquerable"));
				h.setInHouseAttacks(rs.getBoolean("fInHouseAttacks"));
				h.setId(-1);
				h.setHousePlayerColors(rs.getString("fPlayerColors"));
				h.setHouseDefectionFrom(rs.getBoolean("fAllowDefectionsFrom"));
				h.setHouseDefectionTo(rs.getBoolean("fAllowDefectionsTo"));
				h.setHouseFluFile(rs.getString("fFluFile"));
				h.setMotd(rs.getString("fMOTD"));
				h.setTechLevel(rs.getInt("fTechLevel"));
				// Now the vectors
				
				//Load the Meks
				CampaignData.mwlog.dbLog("Loading Meks");
				rs1 = stmt2.executeQuery("SELECT ID from units WHERE uType = " + Unit.MEK + " AND uFactionID = " + h.getDBId());
				while(rs1.next()) {
					SUnit u = new SUnit();
					u.fromDB(rs1.getInt("ID"));
					if ( newbieHouse ){
						int priceForUnit = h.getPriceForUnit(u.getWeightclass(), u.getType());
						int rareSalesTime = Integer.parseInt(h.getConfig("RareMinSaleTime"));
						CampaignMain.cm.getMarket().addListing("Faction_" + h.getName(), u,priceForUnit, rareSalesTime);
						u.setStatus(Unit.STATUS_FORSALE);
					}
					try {
						h.addUnit(u, false);
					} catch (Exception ex) {
						CampaignData.mwlog.dbLog("Exception at addUnit: ");
		                CampaignData.mwlog.dbLog(ex);					}
				}
				
				//Load the Vees
				CampaignData.mwlog.dbLog("Loading Vees");
				rs1 = stmt2.executeQuery("SELECT ID from units WHERE uType = " + Unit.VEHICLE + " AND uFactionID = " + h.getDBId());
				while(rs1.next()) {
					SUnit u = new SUnit();
					u.fromDB(rs1.getInt("ID"));
					if ( newbieHouse ){
						int priceForUnit = h.getPriceForUnit(u.getWeightclass(), u.getType());
						int rareSalesTime = Integer.parseInt(h.getConfig("RareMinSaleTime"));
						CampaignMain.cm.getMarket().addListing("Faction_" + h.getName(), u,priceForUnit, rareSalesTime);
						u.setStatus(Unit.STATUS_FORSALE);
					}
					h.addUnit(u, false);
				}
				
				
				//Load the Infantry
				CampaignData.mwlog.dbLog("Loading Infantry");
				if (Boolean.parseBoolean(h.getConfig("UseInfantry"))) {
					rs1 = stmt2.executeQuery("SELECT ID from units WHERE uType = " + Unit.INFANTRY + " AND uFactionID = " + h.getDBId());
					while(rs1.next()) {
						SUnit u = new SUnit();
						u.fromDB(rs1.getInt("ID"));

						if ( newbieHouse ){
							int priceForUnit = h.getPriceForUnit(u.getWeightclass(), u.getType());
							int rareSalesTime = Integer.parseInt(h.getConfig("RareMinSaleTime"));
							CampaignMain.cm.getMarket().addListing("Faction_" + h.getName(), u,priceForUnit, rareSalesTime);
							u.setStatus(Unit.STATUS_FORSALE);
						}
						CampaignData.mwlog.dbLog("Adding Unit");
						h.addUnit(u, false);
					}
				}
				
				//Load the Protomeks
				CampaignData.mwlog.dbLog("Loading Protos");
				rs1 = stmt2.executeQuery("SELECT ID from units WHERE uType = " + Unit.PROTOMEK + " AND uFactionID = " + h.getDBId());
				while(rs1.next()) {
					SUnit u = new SUnit();
					u.fromDB(rs1.getInt("ID"));
					if ( newbieHouse ){
						int priceForUnit = h.getPriceForUnit(u.getWeightclass(), u.getType());
						int rareSalesTime = Integer.parseInt(h.getConfig("RareMinSaleTime"));
						CampaignMain.cm.getMarket().addListing("Faction_" + h.getName(), u,priceForUnit, rareSalesTime);
						u.setStatus(Unit.STATUS_FORSALE);
					}
					h.addUnit(u, false);
				}
				
				
				//Load the BattleArmor
				CampaignData.mwlog.dbLog("Loading BA");
				rs1 = stmt2.executeQuery("SELECT ID from units WHERE uType = " + Unit.BATTLEARMOR + " AND uFactionID = " + h.getDBId());
				while(rs1.next()) {
					SUnit u = new SUnit();
					u.fromDB(rs1.getInt("ID"));
					if ( newbieHouse ){
						int priceForUnit = h.getPriceForUnit(u.getWeightclass(), u.getType());
						int rareSalesTime = Integer.parseInt(h.getConfig("RareMinSaleTime"));
						CampaignMain.cm.getMarket().addListing("Faction_" + h.getName(), u,priceForUnit, rareSalesTime);
						u.setStatus(Unit.STATUS_FORSALE);
					}
					h.addUnit(u, false);
				}
				
				//Load the Aeros
				CampaignData.mwlog.dbLog("Loading Aeros");
				rs1 = stmt2.executeQuery("SELECT ID from units WHERE uType = " + Unit.AERO + " AND uFactionID = " + h.getDBId());
				while(rs1.next()) {
					SUnit u = new SUnit();
					u.fromDB(rs1.getInt("ID"));
					if ( newbieHouse ){
						int priceForUnit = h.getPriceForUnit(u.getWeightclass(), u.getType());
						int rareSalesTime = Integer.parseInt(h.getConfig("RareMinSaleTime"));
						CampaignMain.cm.getMarket().addListing("Faction_" + h.getName(), u,priceForUnit, rareSalesTime);
						u.setStatus(Unit.STATUS_FORSALE);
					}
					h.addUnit(u, false);
				}
				
				//Load the components
				CampaignData.mwlog.dbLog("Loading Components");
				rs1 = stmt2.executeQuery("SELECT * from factioncomponents where factionID = " + h.getDBId());
				while (rs1.next()) {
					h.getComponents().get(rs1.getInt("unitType")).setElementAt(rs1.getInt("components"), rs1.getInt("unitWeight"));
				}
				
				CampaignData.mwlog.dbLog("Loading Base Gunnery & Piloting");
				rs1 = stmt2.executeQuery("SELECT * from faction_base_gunnery_piloting WHERE factionID = " + h.getDBId());
					while(rs1.next()) {
					h.setBaseGunner(rs1.getInt("baseGunnery"), rs1.getInt("unitType"));
					h.setBasePilot(rs1.getInt("basePiloting"), rs1.getInt("unitType"));
					}
				
					CampaignData.mwlog.dbLog("Loading Default Pilot Skills");
				rs1 = stmt2.executeQuery("SELECT * from faction_pilot_skills WHERE factionID = " + h.getDBId());
					while(rs1.next()) {
						int skillID = rs1.getInt("skillID");
						h.setBasePilotSkill(rs1.getString("pilotSkills"), skillID);
				}
					
					// Load subFactions
				rs1 = stmt2.executeQuery("SELECT * from subfactions WHERE houseID = " + h.getDBId());
				while (rs1.next()) {
					SubFaction newSubFaction = new SubFaction();
					newSubFaction.fromString(rs1.getString("sf_string"));
					h.getSubFactionList().put(rs1.getString("subfactionName"), newSubFaction);
				}
				rs1.close();
				rs1 = stmt2.executeQuery("SELECT * from faction_leaders WHERE faction_id = " + h.getDBId());
				while(rs1.next())
					h.addLeader(rs1.getString("leader_name"));
				
				
				if(h.isMercHouse()) {
					CampaignData.mwlog.dbLog("Merc House");
					rs1.close();
					Hashtable<String, ContractInfo> merctable = new Hashtable<String, ContractInfo>();
					PreparedStatement ps = con.prepareStatement("SELECT contractID from merc_contract_info WHERE contractHouse = ?");
					ps.setString(1, h.getName());
					rs1 = ps.executeQuery();
					while (rs1.next()) {
						ContractInfo ci = new ContractInfo();
						ci.fromDB(rs1.getInt("contractID"));
						merctable.put(ci.getPlayerName(), ci);
					}
					ps.close();
					((MercHouse)h).setOutstandingContracts(merctable);
				}
				
				CampaignMain.cm.addHouse(h);
				CampaignData.mwlog.dbLog("Loading Faction Pilots");
				CampaignMain.cm.MySQL.loadFactionPilots(h);
				h.loadConfigFileFromDB();
				h.setUsedMekBayMultiplier(Float.parseFloat(h.getConfig("UsedPurchaseCostMulti")));
				if(CampaignMain.cm.isUsingIncreasedTechs())
					h.addCommonUnitSupport();
				if(CampaignMain.cm.isSynchingBB()) {
					h.setForumName(h.getConfig("ForumGroupName"));
					h.setForumID(CampaignMain.cm.MySQL.getHouseForumID(h.getForumName()));
				}
				CampaignData.mwlog.dbLog("Faction " + h.getName() + " loaded");
				}
			rs.close();
			if(rs1 != null)
				rs1.close();
			stmt.close();
			stmt2.close();
		} catch (SQLException e) {
			CampaignData.mwlog.dbLog("SQL Error in FactionHandler.loadFaction: " + e.getMessage());
            CampaignData.mwlog.dbLog(e);
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException ex) {}
			try {
				if (rs1 != null)
					rs1.close();
			} catch (SQLException ex) {}
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException ex) {}
			try {
				if (stmt2 != null)
					stmt2.close();
			} catch (SQLException ex) {}
		}
		ch.returnConnection(con);
	}
	
	public int countFactions() {
		Statement stmt = null;
		ResultSet rs = null;
		int num = 0;
		Connection con = ch.getConnection();
		
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery("SELECT COUNT(*) as num from factions");
			rs.next();
			num = rs.getInt("num");
		} catch (SQLException e) {
			CampaignData.mwlog.dbLog("SQL Error in FactionHandler.countFactions: " + e.getMessage());
            CampaignData.mwlog.dbLog(e);
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException ex) {}
			try {
				if (stmt != null) 
					stmt.close();
			} catch (SQLException ex) {}
		}
		ch.returnConnection(con);
		return num;
	}
	
	public void saveSubFaction(String SubFactionString, int houseID) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection con = ch.getConnection();
		try {
			String sql = "";
			SubFaction sf = new SubFaction();
			sf.fromString(SubFactionString);

			CampaignData.mwlog.dbLog("Saving subfaction: " + sf.getConfig("Name"));
			
			Boolean inDB = false;
			ps = con.prepareStatement("SELECT COUNT(*) as num from subfactions WHERE houseID = " + houseID + " AND subfactionName='" + sf.getConfig("Name") + "'");
			rs = ps.executeQuery();
			rs.next();
			if(rs.getInt("num") > 0)
				inDB = true;
			rs.close();
			ps.close();
			if(!inDB) {
				sql = "INSERT into subfactions set subfactionName = ?, houseID = ?, sf_string = ?";
				ps = con.prepareStatement(sql);
				ps.setString(1, sf.getConfig("Name"));
				ps.setInt(2, houseID);
				ps.setString(3, sf.toString());
				ps.executeUpdate();
			} else {
				sql = "UPDATE subfactions set sf_string = ? WHERE (houseID = ? AND subfactionName = ?)";
				ps = con.prepareStatement(sql);
				ps.setString(1, sf.toString());
				ps.setInt(2, houseID);
				ps.setString(3, sf.getConfig("Name"));
				ps.executeUpdate();
			}
			ps.close();
		} catch (SQLException e) {
			CampaignData.mwlog.dbLog("SQLException in FactionHandler.saveSubFaction: " + e.getMessage());
            CampaignData.mwlog.dbLog(e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException ex) {}
			}
		}
		ch.returnConnection(con);
	}
	
	public void deleteSubFaction(String subFactionName, int houseID) {
		PreparedStatement ps = null;
		Connection con = ch.getConnection();
		
		try {
			ps = con.prepareStatement("DELETE from subfactions WHERE subfactionName = ? AND houseID = ?");
			ps.setString(1, subFactionName);
			ps.setInt(2, houseID);
			ps.executeUpdate();
		} catch (SQLException e) {
			CampaignData.mwlog.dbLog("SQLException in FactionHandler.deleteSubFaction: " + e.getMessage());
            CampaignData.mwlog.dbLog(e);
		} finally {
			try {
				if (ps != null)
					ps.close();
			} catch (SQLException ex) {}
		}
		ch.returnConnection(con);
	}
	
	public FactionHandler () {
		
	}

}
