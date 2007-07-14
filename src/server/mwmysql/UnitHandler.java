package server.mwmysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import common.Unit;
import common.util.UnitUtils;

import megamek.common.AmmoType;
import megamek.common.Entity;
import megamek.common.Mech;
import megamek.common.MiscType;
import megamek.common.Mounted;
import megamek.common.WeaponType;

import server.MMServ;
import server.campaign.CampaignMain;
import server.campaign.SUnit;
import server.campaign.pilot.SPilot;

public class UnitHandler {
	
	Connection con;
	
	public void linkUnitToFaction(int unitID, int factionID) {
		try {
			MMServ.mmlog.dbLog("Linking Unit to Faction: ");
			MMServ.mmlog.dbLog(" --> Unit ID: " + unitID);
			MMServ.mmlog.dbLog(" --> Faction ID: " + factionID);
			Statement stmt = con.createStatement();

			stmt.executeUpdate("UPDATE units set uPlayerID = NULL, uFactionID = " + factionID + " WHERE MWID = " + unitID);
			stmt.close();
		} catch (SQLException e) {
			MMServ.mmlog.dbLog("SQL Error in UnitHandler.linkUnitToFaction: " + e.getMessage());
		}
	}
	
	public void unlinkUnit(int unitID){
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate("UPDATE units set uFactionID = NULL, uPlayerID = NULL WHERE ID = " + unitID);
			stmt.close();
		} catch(SQLException e) {
			MMServ.mmlog.dbLog("SQL Error in UnitHandler.unlinkUnit: " + e.getMessage());
		}
	}
	
	public void linkUnitToPlayer(int unitID, int playerID) {
		try {
			PreparedStatement ps;
			ps = con.prepareStatement("UPDATE units set uFactionID = NULL, uPlayerID = ? WHERE MWID = " + unitID);
			ps.setInt(1, playerID);
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			MMServ.mmlog.dbLog("SQL Error in UnitHandler.linkUnitToPlayer: " + e.getMessage());
		}
	}
	
	public void saveUnit(SUnit u) {
	PreparedStatement ps;
	StringBuffer sql = new StringBuffer();
	Entity ent = u.getEntity();
	
	try {
			if(u.getDBId()==0) {
			// Unit's not in there - insert it
			sql.setLength(0);
			sql.append("INSERT into units set MWID=?, uFileName=?, uPosID=?, uStatus=?, uProducer=?, uWeightClass=?, uAutoEject=?, uHasSpotlight=?, uIsUsingSpotlight=?, uTargetSystem=?, uScrappableFor=?, uBattleDamage=?, uLastCombatPilot=?, uCurrentRepairCost=?, uLifetimeRepairCost=?, uType=?");
			ps=con.prepareStatement(sql.toString(), PreparedStatement.RETURN_GENERATED_KEYS);
			ps.setInt(1, u.getId());
			ps.setString(2, u.getUnitFilename());
			ps.setInt(3, u.getPosId());
			ps.setInt(4, u.getStatus());
			ps.setString(5, u.getProducer());
			ps.setInt(6, u.getWeightclass());
			if(ent instanceof Mech)
			  ps.setString(7, Boolean.toString(((Mech)ent).isAutoEject()));
			else
				ps.setString(7, "false");
			ps.setString(8, Boolean.toString(ent.hasSpotlight()));
			ps.setString(9, Boolean.toString(ent.isUsingSpotlight()));
			if (CampaignMain.cm.getData().getBannedTargetingSystems().containsKey(u.getEntity().getTargSysType())) {
				ps.setInt(10, MiscType.T_TARGSYS_STANDARD);
				u.getEntity().setTargSysType(MiscType.T_TARGSYS_STANDARD);
			} else {
				ps.setInt(10, ent.getTargSysType());
			}
			ps.setInt(11, u.getScrappableFor());
			if(CampaignMain.cm.isUsingAdvanceRepair())
				ps.setString(12, UnitUtils.unitBattleDamage(u.getEntity(), false));
			else
				ps.setString(12, "%%-%%-%%-");
			ps.setInt(13, u.getLastCombatPilot());
			ps.setInt(14, u.getCurrentRepairCost());
			ps.setInt(15, u.getLifeTimeRepairCost());
			ps.setInt(16, u.getType());
			ps.executeUpdate();
			u.setDBId(u.getId());
		} else {
			// Unit's already there - update it
			sql.setLength(0);
			sql.append("UPDATE units set uFileName=?, uPosID=?, uStatus=?, uProducer=?, uWeightClass=?, uAutoEject=?, uHasSpotlight=?, uIsUsingSpotlight=?, uTargetSystem=?, uScrappableFor=?, uBattleDamage=?, uLastCombatPilot=?, uCurrentRepairCost=?, uLifetimeRepairCost=?, uType = ? where MWID=?");
			ps=con.prepareStatement(sql.toString());
			ps.setString(1, u.getUnitFilename());
			ps.setInt(2, u.getPosId());
			ps.setInt(3, u.getStatus());
			ps.setString(4, u.getProducer());
			ps.setInt(5, u.getWeightclass());
			if(ent instanceof Mech)
			  ps.setString(6, Boolean.toString(((Mech)ent).isAutoEject()));
			else
				ps.setString(6, "false");
			ps.setString(7, Boolean.toString(ent.hasSpotlight()));
			ps.setString(8, Boolean.toString(ent.isUsingSpotlight()));
			if (CampaignMain.cm.getData().getBannedTargetingSystems().containsKey(u.getEntity().getTargSysType())) {
				ps.setInt(9, MiscType.T_TARGSYS_STANDARD);
				u.getEntity().setTargSysType(MiscType.T_TARGSYS_STANDARD);
			} else {
				ps.setInt(9, ent.getTargSysType());
			}
			ps.setInt(10, u.getScrappableFor());
			if(CampaignMain.cm.isUsingAdvanceRepair())
				ps.setString(11, UnitUtils.unitBattleDamage(u.getEntity(), false));
			else
				ps.setString(11, "%%-%%-%%-");
			ps.setInt(12, u.getLastCombatPilot());
			ps.setInt(13, u.getCurrentRepairCost());
			ps.setInt(14, u.getLifeTimeRepairCost());
			ps.setInt(15, u.getType());
			ps.setInt(16, u.getId());
			ps.executeUpdate();
		}
		// Now do Machine Guns
		ps.executeUpdate("DELETE from unit_mgs WHERE unitID = " + u.getId());
		ArrayList<Mounted> en_Weapon = ent.getWeaponList();
		int location = 0;
		for (Mounted mWeapon : en_Weapon) {
			WeaponType weapon = (WeaponType)mWeapon.getType();
			if (weapon.hasFlag(WeaponType.F_MG)) {
				sql.setLength(0);
				sql.append("INSERT into unit_mgs set unitID=?, mgLocation=?, mgRapidFire=?");
				ps = con.prepareStatement(sql.toString());
				ps.setInt(1, u.getId());
				ps.setInt(2, location);
				ps.setString(3, Boolean.toString(mWeapon.isRapidfire()));
				ps.executeUpdate();
			}
			location++;
		}
		// Do Ammo
		ps.executeUpdate("DELETE from unit_ammo WHERE unitID = " + u.getId());
		
		ArrayList<Mounted> en_Ammo = ent.getAmmo();
		int AmmoLoc = 0;
		for (Mounted mAmmo : en_Ammo ) {
			boolean hotloaded = mAmmo.isHotLoaded();
			if (!CampaignMain.cm.getMegaMekClient().game.getOptions().booleanOption("maxtech_hotload"))
				hotloaded = false;
			AmmoType at = (AmmoType)mAmmo.getType();
			sql.setLength(0);
			sql.append("INSERT into unit_ammo set unitID = ?, ammoLocation = ?, ammoHotLoaded=?, ammoType=?, ammoInternalName=?, ammoShotsLeft=?");
			ps = con.prepareStatement(sql.toString());
			ps.setInt(1, u.getId());
			ps.setInt(2, AmmoLoc);
			ps.setString(3, Boolean.toString(hotloaded));
			ps.setInt(4, at.getAmmoType());
			ps.setString(5, at.getInternalName());
			ps.setInt(6, mAmmo.getShotsLeft());
			ps.executeUpdate();
			AmmoLoc++;
		}
//		 Save the pilot
		if (u.getPilot().getGunnery()!=99){
			CampaignMain.cm.MySQL.savePilot((SPilot)u.getPilot(), u.getType(), u.getWeightclass());
		}
	} catch (SQLException e){
		MMServ.mmlog.dbLog("SQL Exception in UnitHandler.saveUnit: " + e.getMessage());
	}
	}
	
	public void deleteUnit(int unitID) {
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate("DELETE from unit_mgs WHERE unitID = " + unitID);
			stmt.executeUpdate("DELETE from unit_ammo WHERE unitID = " + unitID);
			stmt.executeUpdate("DELETE from units WHERE MWID = " + unitID);
			stmt.close();
		} catch (SQLException e) {
			MMServ.mmlog.dbLog("SQL Error in UnitHandler.deleteUnit: " + e.getMessage());
		}
	}
	
	public SUnit loadUnit(int unitID) {
		SUnit u = new SUnit();
		try {
			MMServ.mmlog.dbLog("Entering loadUnit, loading unit #: " + unitID);
			ResultSet rs;
			Statement stmt = con.createStatement();
			rs = stmt.executeQuery("SELECT * from units WHERE MWID = " + unitID);
			while(rs.next()) {
			u.setUnitFilename(rs.getString("uFileName"));
			u.setPosId(rs.getInt("uPosID"));
			int newstate = rs.getInt("uStatus");
			u.setProducer(rs.getString("uProducer"));
			u.setWeightclass(rs.getInt("uWeightClass"));
			u.setId(unitID);
			u.setDBId(unitID);
			if(CampaignMain.cm.getCurrentUnitID() <= u.getId())
				CampaignMain.cm.setCurrentUnitID(u.getId() + 1);
			if (u.getId()==0)
				u.setId(CampaignMain.cm.getAndUpdateCurrentUnitID());
			if (newstate == Unit.STATUS_FORSALE && CampaignMain.cm.getMarket().getListingForUnit(u.getId()) == null)
				u.setStatus(Unit.STATUS_OK);
			else if (CampaignMain.cm.isUsingAdvanceRepair())
				u.setStatus(Unit.STATUS_OK);
			else
				u.setStatus(newstate);
			u.setScrappableFor(rs.getInt("uScrappableFor"));
			u.setRepairCosts(rs.getInt("uCurrentRepairCost"), rs.getInt("uLifetimeRepairCost"));
MMServ.mmlog.dbLog("Loading entity...");
			Entity unitEntity = u.loadMech(u.getUnitFilename());
			MMServ.mmlog.dbLog("Entity Loaded.");
			if(unitEntity == null) {
				u = null;
				return u;
			}

MMServ.mmlog.dbLog(rs.getString("uAutoEject"));
			if(unitEntity instanceof Mech)
				((Mech)unitEntity).setAutoEject(Boolean.parseBoolean(rs.getString("uAutoEject")));
			unitEntity.setSpotlight(Boolean.parseBoolean(rs.getString("uHasSpotlight")));
			
			unitEntity.setSpotlightState(Boolean.parseBoolean(rs.getString("uIsUsingSpotlight")));
			
			if(CampaignMain.cm.isUsingAdvanceRepair())
				UnitUtils.applyBattleDamage(unitEntity, rs.getString("uBattleDamage"), (CampaignMain.cm.getRTT() != null & CampaignMain.cm.getRTT().unitRepairTimes(u.getId())!=null));
			
			if (CampaignMain.cm.getMegaMekClient().game.getOptions().booleanOption("allow_level_3_targsys")) {
				int targetingType = rs.getInt("uTargetSystem");
				if (CampaignMain.cm.getData().getBannedTargetingSystems().containsKey(targetingType) || unitEntity.hasC3() || unitEntity.hasC3i() || UnitUtils.hasTargettingComputer(unitEntity))
					unitEntity.setTargSysType(MiscType.T_TARGSYS_STANDARD);
				else
					unitEntity.setTargSysType(targetingType);
			}
			

			u.setEntity(unitEntity);
			SPilot p = new SPilot();
			p = CampaignMain.cm.MySQL.loadUnitPilot(unitID);
			u.setPilot(p);
			u.setLastCombatPilot(p.getPilotId());
			u.init();
			
			
			// Load ammo			
			rs = stmt.executeQuery("SELECT * from unit_ammo WHERE unitID = " + unitID + " ORDER BY ammoLocation");

			while(rs.next()) {
				int weaponType = rs.getInt("ammoType");
				String ammoName = rs.getString("ammoInternalName");
				int shots = rs.getInt("ammoShotsLeft");
				int AmmoLoc = rs.getInt("ammoLocation");
				boolean hotloaded = Boolean.parseBoolean(rs.getString("ammoHotLoaded"));
				if(!CampaignMain.cm.getMegaMekClient().game.getOptions().booleanOption("maxtech_hotload"))
					hotloaded = false;

				AmmoType at = u.getEntityAmmo(weaponType, ammoName);
				String munition = Long.toString(at.getMunitionType());
				
				if (at == null )
					continue;
				if (CampaignMain.cm.getData().getServerBannedAmmo().get(munition) != null)
					continue;				
				try {
					unitEntity.getAmmo().get(AmmoLoc).changeAmmoType(at);
					unitEntity.getAmmo().get(AmmoLoc).setShotsLeft(shots);
					unitEntity.getAmmo().get(AmmoLoc).setHotLoad(hotloaded);
				} catch (Exception ex) {
					MMServ.mmlog.dbLog("Exception: " + ex.toString());
					MMServ.mmlog.dbLog(ex.getStackTrace().toString());
				}
			}

			u.setEntity(unitEntity);
					
			// Load MGs
			ArrayList<Mounted> enWeapons = unitEntity.getWeaponList();
			rs = stmt.executeQuery("SELECT * from unit_mgs WHERE unitID = " + unitID + " ORDER BY mgLocation");
			while(rs.next()) {
				int location = rs.getInt("mgLocation");
				int currentLocation = 0;
				for (Mounted mWeapon : enWeapons) {
					if (currentLocation == location ) {
						mWeapon.setRapidfire(Boolean.parseBoolean(rs.getString("mgRapidFire")));
						currentLocation++;
						break;
					}
					currentLocation++;
				}
			}
			
			u.setEntity(unitEntity);
			MMServ.mmlog.dbLog("Unit " + unitID + " loaded.");
			}
		} catch (SQLException e) {
			MMServ.mmlog.dbLog("SQL Error in UnitHandler.loadUnit: " + e.getMessage());
		}
		if(u == null)
			MMServ.mmlog.dbLog("U is null!!!");
		return u;
		}
	
	public UnitHandler(Connection c) {
		this.con = c;
	}

}
