package server.mwmysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

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
	
	public void linkUnitToFaction(int unitID, String factionName) {
		try {
			PreparedStatement ps;
			ps = con.prepareStatement("DELETE from units_to_factions WHERE unitID = ?");
			ps.setInt(1, unitID);
			ps.executeUpdate();
			
			ps = con.prepareStatement("INSERT into units_to_factions set unitID = ?, factionName = ?");
			ps.setInt(1, unitID);
			ps.setString(2, factionName);
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			MMServ.mmlog.dbLog("SQL Error in UnitHandler.linkUnitToFaction: " + e.getMessage());
		}
	}
	
	public void unlinkUnit(int unitID){
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate("DELETE from units_to_factions WHERE unitID = " + unitID);
			stmt.executeUpdate("DELETE from units_to_players WHERE unitID = " + unitID);
			stmt.close();
		} catch(SQLException e) {
			MMServ.mmlog.dbLog("SQL Error in UnitHandler.unlinkUnit: " + e.getMessage());
		}
	}
	
	public void linkUnitToPlayer(int unitID, String playerName) {
		try {
			PreparedStatement ps;
			ps = con.prepareStatement("DELETE from units_to_players WHERE unitID = ?");
			ps.setInt(1, unitID);
			ps.executeUpdate();
			
			ps = con.prepareStatement("INSERT into units_to_players set unitID = ?, playerName = ?");
			ps.setInt(1, unitID);
			ps.setString(2, playerName);
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			MMServ.mmlog.dbLog("SQL Error in UnitHandler.linkUnitToPlayer: " + e.getMessage());
		}
	}
	
	public void saveUnit(SUnit u) {
	PreparedStatement ps;
	ResultSet rs = null;
	StringBuffer sql = new StringBuffer();
	Entity ent = u.getEntity();
	
	try {
		ps = con.prepareStatement("SELECT COUNT(*) as num from units where uid=?");
		ps.setInt(1, u.getId());
		rs = ps.executeQuery();
		rs.next();
		if(rs.getInt("num")==0) {
			// Unit's not in there - insert it
			sql.setLength(0);
			sql.append("INSERT into units set uID=?, uFileName=?, uPosID=?, uStatus=?, uProducer=?, uWeightClass=?, uAutoEject=?, uHasSpotlight=?, uIsUsingSpotlight=?, uTargetSystem=?, uScrappableFor=?, uBattleDamage=?, uLastCombatPilot=?, uCurrentRepairCost=?, uLifetimeRepairCost=?");
			ps=con.prepareStatement(sql.toString());
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
		} else {
			// Unit's already there - update it
			sql.setLength(0);
			sql.append("UPDATE units set uFileName=?, uPosID=?, uStatus=?, uProducer=?, uWeightClass=?, uAutoEject=?, uHasSpotlight=?, uIsUsingSpotlight=?, uTargetSystem=?, uScrappableFor=?, uBattleDamage=?, uLastCombatPilot=?, uCurrentRepairCost=?, uLifetimeRepairCost=? where uID=?");
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
		// Save the pilot
		if (u.getPilot().getGunnery()!=99){
			CampaignMain.cm.MySQL.savePilot((SPilot)u.getPilot());
			CampaignMain.cm.MySQL.linkPilotToUnit(u.getPilot().getPilotId(), u.getId());
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
			stmt.executeUpdate("DELETE from units WHERE unitID = " + unitID);
			stmt.close();
		} catch (SQLException e) {
			MMServ.mmlog.dbLog("SQL Error in UnitHandler.deleteUnit: " + e.getMessage());
		}
	}
	
	public UnitHandler(Connection c) {
		this.con = c;
	}

}
