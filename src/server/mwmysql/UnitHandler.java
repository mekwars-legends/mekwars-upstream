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
			Statement stmt = con.createStatement();

			stmt.executeUpdate("UPDATE units set uPlayerName = NULL, uFactionID = " + factionID + " WHERE ID = " + unitID);
			stmt.close();
		} catch (SQLException e) {
			MMServ.mmlog.dbLog("SQL Error in UnitHandler.linkUnitToFaction: " + e.getMessage());
		}
	}
	
	public void unlinkUnit(int unitID){
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate("UPDATE units set uFactionID = NULL, uPlayerName = NULL WHERE ID = " + unitID);
			stmt.close();
		} catch(SQLException e) {
			MMServ.mmlog.dbLog("SQL Error in UnitHandler.unlinkUnit: " + e.getMessage());
		}
	}
	
	public void linkUnitToPlayer(int unitID, String playerName) {
		try {
			PreparedStatement ps;
			ps = con.prepareStatement("UPDATE units set uFactionID = NULL, uPlayerName = ? WHERE ID = " + unitID);
			ps.setString(1, playerName);
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
	ResultSet rs;
	
	try {
			if(u.getDBId()==0) {
			// Unit's not in there - insert it
			sql.setLength(0);
			sql.append("INSERT into units set MWID=?, uFileName=?, uPosID=?, uStatus=?, uProducer=?, uWeightClass=?, uAutoEject=?, uHasSpotlight=?, uIsUsingSpotlight=?, uTargetSystem=?, uScrappableFor=?, uBattleDamage=?, uLastCombatPilot=?, uCurrentRepairCost=?, uLifetimeRepairCost=?");
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
			ps.executeUpdate();
			rs = ps.getGeneratedKeys();
			rs.next();
			int uid = -1;
			uid = rs.getInt(1);
			if (uid != -1)
				u.setDBId(uid);
		} else {
			// Unit's already there - update it
			sql.setLength(0);
			sql.append("UPDATE units set uFileName=?, uPosID=?, uStatus=?, uProducer=?, uWeightClass=?, uAutoEject=?, uHasSpotlight=?, uIsUsingSpotlight=?, uTargetSystem=?, uScrappableFor=?, uBattleDamage=?, uLastCombatPilot=?, uCurrentRepairCost=?, uLifetimeRepairCost=?, MWID=? where ID=?");
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
			ps.setInt(15, u.getId());
			ps.setInt(16, u.getDBId());
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
		int AmmoLoc = 1;
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
			stmt.executeUpdate("DELETE from units WHERE ID = " + unitID);
			stmt.close();
		} catch (SQLException e) {
			MMServ.mmlog.dbLog("SQL Error in UnitHandler.deleteUnit: " + e.getMessage());
		}
	}
	
	public SUnit loadUnit(int unitID) {
		SUnit u = new SUnit();
		try {
			ResultSet rs;
			Statement stmt = con.createStatement();
			rs = stmt.executeQuery("SELECT * from units WHERE ID = " + unitID);
			while(rs.next()) {
			u.setUnitFilename(rs.getString("uFileName"));
			u.setPosId(rs.getInt("uPosID"));
			int newstate = rs.getInt("uStatus");
			u.setProducer(rs.getString("uProducer"));
			u.setWeightclass(rs.getInt("uWeightClass"));
			u.setId(unitID);
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

			Entity unitEntity = u.loadMech(u.getUnitFilename());
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
			// Load ammo
			rs = stmt.executeQuery("SELECT * from unit_ammo WHERE unitID = " + unitID + " ORDER BY ammoLocation");
			ArrayList<Mounted> e = unitEntity.getAmmo();
			while(rs.next()) {
				int weaponType = rs.getInt("ammoType");
				String ammoName = rs.getString("ammoInternalName");
				int shots = rs.getInt("ammoShotsLeft");
				boolean hotloaded = Boolean.parseBoolean(rs.getString("ammoHotLoaded"));
				if(!CampaignMain.cm.getMegaMekClient().game.getOptions().booleanOption("maxtech_hotload"))
					hotloaded = false;
				Mounted mWeapon = e.get(rs.getInt("ammoLocation"));
				AmmoType at = u.getEntityAmmo(weaponType, ammoName);
				if (at == null)
					continue;
				String munition = Long.toString(at.getMunitionType());
				if (CampaignMain.cm.getData().getServerBannedAmmo().get(munition) != null)
					continue;
				mWeapon.changeAmmoType(at);
				mWeapon.setShotsLeft(shots);
				mWeapon.setHotLoad(hotloaded);
			}
			
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
			SPilot p = new SPilot();
			p = CampaignMain.cm.MySQL.loadUnitPilot(unitID);
			u.setPilot(p);
			u.setLastCombatPilot(p.getPilotId());
			u.init();
			}
		} catch (SQLException e) {
			MMServ.mmlog.dbLog("SQL Error in UnitHandler.loadUnit: " + e.getMessage());
		}
		return u;
		}
	
	public UnitHandler(Connection c) {
		this.con = c;
	}

}
