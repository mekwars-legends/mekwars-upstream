/*
 * MekWars - Copyright (C) 2004 
 * 
 * Derived from MegaMekNET (http://www.sourceforge.net/projects/megameknet)
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

/* 
 * original author - @McWizard
 * rewritten extensively on 2/04/03. @urgru. 
 * 
 * factories now produce on demand, and only decrement
 * their reset counters during ticks.
 */

package server.campaign;

import java.io.File;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;

import server.campaign.pilot.SPilot;
import server.campaign.util.SerializedMessage;
import server.mwmysql.JDBCConnectionHandler;

import common.CampaignData;
import common.Planet;
import common.Unit;
import common.UnitFactory;
import common.util.TokenReader;


public class SUnitFactory extends UnitFactory implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1735176578439214960L;
    // VARIABLES
    private SPlanet planet;
    
    private JDBCConnectionHandler ch = new JDBCConnectionHandler();

    // CONSTRUCTORS
    public SUnitFactory() {
        // empty
    }

    public SUnitFactory(String Name, SPlanet P, String Size, String Faction, int ticksuntilrefresh, int refreshSpeed, int type, String buildTableFolder, int accessLevel) {
        setName(Name);
        setPlanet(P);
        setSize(Size);
        setFounder(Faction);
        setTicksUntilRefresh(ticksuntilrefresh);
        setRefreshSpeed(refreshSpeed);
        setType(type);
        setBuildTableFolder(buildTableFolder);
        setAccessLevel(accessLevel);
    }

    // STRING SAVE METHODS
    /**
     * Used for Serialisation
     * 
     * @return A Serialised form of the UnitFactory
     */
    @Override
    public String toString() {
        SerializedMessage result = new SerializedMessage("*");
        result.append("MF");
        result.append(getName());
        result.append(getSize());
        result.append(getFounder());
        result.append(getTicksUntilRefresh());
        result.append(getRefreshSpeed());

        String buildtablefolder = getBuildTableFolder().replaceAll(BuildTable.STANDARD + "\\" + File.separatorChar, "");

        if (buildtablefolder.trim().length() < 1 || buildtablefolder.equals(BuildTable.STANDARD))
            result.append("0");
        else
            result.append(buildtablefolder);

        result.append(getType());
        result.append(isLocked());
        result.append(getAccessLevel());
        return result.toString();
    }

    public void toDB() {
        Statement stmt = null;
        ResultSet rs = null;
        StringBuffer sql = new StringBuffer();
        Planet pl = getPlanet();
        PreparedStatement ps = null;
        int fid = 0;
        Connection c = ch.getConnection();
        try {
            stmt = c.createStatement();
            sql.setLength(0);
            sql.append("SELECT FactoryID from factories WHERE FactoryID = '");
            sql.append(getID());
            sql.append("'");
            rs = stmt.executeQuery(sql.toString());
            if (!rs.next()) {
                // This doesn't exist, so INSERT it
                sql.setLength(0);
                sql.append("INSERT into factories set ");
                sql.append("FactoryName = ?, ");
                sql.append("FactorySize = ?, ");
                sql.append("FactoryFounder = ?, ");
                sql.append("FactoryTicks = ?, ");
                sql.append("FactoryRefreshSpeed = ?, ");
                sql.append("FactoryType = ?, ");
                sql.append("FactoryPlanet = ?, ");
                sql.append("FactoryisLocked = ?, ");
                sql.append("FactoryBuildTableFolder = ?, ");
                sql.append("FactoryAccessLevel = ?");

                ps = c.prepareStatement(sql.toString(), PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setString(1, getName());
                ps.setString(2, getSize());
                ps.setString(3, getFounder());
                ps.setInt(4, getTicksUntilRefresh());
                ps.setInt(5, getRefreshSpeed());
                ps.setInt(6, getType());
                ps.setString(7, pl.getName());
                ps.setString(8, Boolean.toString(isLocked()));
                ps.setString(9, getBuildTableFolder());
                ps.setInt(10, getAccessLevel());

                ps.executeUpdate();
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    fid = rs.getInt(1);
                    setID(fid);
                }
            } else {
                // It already exists, so UPDATE it
                fid = rs.getInt("FactoryID");
                sql.setLength(0);
                sql.append("UPDATE factories set ");
                sql.append("FactoryName = ?, ");
                sql.append("FactorySize = ?, ");
                sql.append("FactoryPlanet = ?, ");
                sql.append("FactoryFounder = ?, ");
                sql.append("FactoryTicks = ?, ");
                sql.append("FactoryRefreshSpeed = ?, ");
                sql.append("FactoryType = ?, ");
                sql.append("FactoryisLocked = ?, ");
                sql.append("FactoryBuildTableFolder = ?, ");
                sql.append("FactoryAccessLevel = ? ");
                sql.append("WHERE FactoryID = ?");

                ps = c.prepareStatement(sql.toString());
                ps.setString(1, getName());
                ps.setString(2, getSize());
                ps.setString(3, pl.getName());
                ps.setString(4, getFounder());
                ps.setInt(5, getTicksUntilRefresh());
                ps.setInt(6, getRefreshSpeed());
                ps.setInt(7, getType());
                ps.setString(8, Boolean.toString(isLocked()));
                ps.setString(9, getBuildTableFolder());
                ps.setInt(10, getAccessLevel());
                ps.setInt(11, getID());

                ps.executeUpdate();
            }
            rs.close();
            ps.close();
            stmt.close();
        } catch (SQLException e) {
            CampaignData.mwlog.dbLog("SQL ERROR in SUnitFactory.toDB: " + e.getMessage());
            CampaignData.mwlog.dbLog(e);
        } finally {
        	if (rs != null) {
        		try {
        			rs.close();
        		} catch (SQLException e) {}
        	}
        	if (ps != null) {
        		try {
        			ps.close();
        		} catch (SQLException e) {}
        	}
        	if (stmt != null) {
        		try {
        			stmt.close();
        		} catch (SQLException e) {}
        	}
        	ch.returnConnection(c);
        }
    }

    /**
     * Used to DE-Serialise a MF
     * 
     * @param s
     *            The Serialised Version
     * @param p
     *            A SPlanet where this MF is placed upon
     * @param r
     *            The Random Object
     */
    public void fromString(String s, SPlanet p, Random r) {
        s = s.substring(3);
        StringTokenizer ST = new StringTokenizer(s, "*");
        setName(TokenReader.readString(ST));
        setSize(TokenReader.readString(ST));
        setFounder(TokenReader.readString(ST));
        setTicksUntilRefresh(TokenReader.readInt(ST));
        setRefreshSpeed(TokenReader.readInt(ST));

        setBuildTableFolder(TokenReader.readString(ST));

        setType(TokenReader.readInt(ST));
        setLock(TokenReader.readBoolean(ST));
        setAccessLevel(TokenReader.readInt(ST));

        setPlanet(p);
    }

    // METHODS
    public String getIcons() {
        // TODO: Add more icons to make this unambiguous
        String sizeid = "";
        String result = "";
        int size = getWeightclass();
        if (size == Unit.LIGHT)
            sizeid += "l";
        else if (size == Unit.MEDIUM)
            sizeid += "m";
        else if (size == Unit.HEAVY)
            sizeid += "h";
        else if (size == Unit.ASSAULT)
            sizeid += "a";
        if (canProduce(Unit.MEK))
            sizeid += "m";
        else if (canProduce(Unit.VEHICLE))
            sizeid += "v";
        else if (canProduce(Unit.INFANTRY))
            sizeid += "li";// override size w/ light
        else if (canProduce(Unit.BATTLEARMOR))
            sizeid += "b";
        else if (canProduce(Unit.PROTOMEK))
            sizeid += "p";
        else if (canProduce(Unit.AERO))
            sizeid += "ae";

        result += "<img src=\"data/images/" + sizeid + ".gif\">";
        return result;
    }

    /**
     * Have the factory build a unit. This should be called only as the result
     * of a tick (overflow production) or RequestCommand. Any other use should
     * be avoided.
     * 
     * @return the Mek Produced
     */
    public Vector<SUnit> getMechProduced(int type_id, SPilot pilot) {

        // Build the fluff text for the mek
        String Filename = "";
        String producer = "Built by ";
        Vector<SUnit> units = new Vector<SUnit>(1, 1);

        if (this.getPlanet().getOwner() != null)
            producer += this.getPlanet().getOwner().getName();
        else
            producer += this.getFounder();

        /*
         * add a production location to the fluff, if from a normal planet. null
         * planet will normally be reward point production.
         */
        if (this.getPlanet().getName() != null)
            producer += " on " + this.getPlanet().getName();

        String unitSize = getSize();
        if (CampaignMain.cm.getBooleanConfig("UseOnlyOneVehicleSize") && type_id == Unit.VEHICLE)
            unitSize = Unit.getWeightClassDesc(CampaignMain.cm.getRandomNumber(4));

        Filename = BuildTable.getUnitFilename(this.getFounder(), unitSize, type_id, getBuildTableFolder());
        // log the creation
        String buildtableName = this.getFounder() + "_" + this.getSize();
        if (type_id != Unit.MEK)
            buildtableName += Unit.getTypeClassDesc(type_id);

		if(this.getPlanet().getOwner() != null)
			CampaignData.mwlog.infoLog("New unit for " + this.getPlanet().getOwner().getName() + " on " + this.getPlanet().getName() + ": " + Filename + "(Table: " + buildtableName + ")");
		else 
			CampaignData.mwlog.infoLog("New unit for " + this.getFounder() + " on " + this.getPlanet().getName() + ": " + Filename + "(Table: " + buildtableName + ")");
		
        if (Filename.toLowerCase().trim().endsWith(".mul")) {
            units.addAll(SUnit.createMULUnits(Filename, producer));
        } else {
            // Build the unit & create history entry
            SUnit cm = new SUnit(producer, Filename, this.getWeightclass());
            cm.setPilot(pilot);
            units.add(cm);
        }
        return units;
    }

    /**
     * Add or remove refresh time to a factory. This should ALWAYS be used in
     * lieu of super.setTicksUntilRefresh(), as it properly (albeit hackishly)
     * updates players' clients with accurate refresh times.
     */
    public String addRefresh(int i, boolean sendHSUpdate) {

        int startRefresh = getTicksUntilRefresh();

        setTicksUntilRefresh(getTicksUntilRefresh() + i);
        if (getTicksUntilRefresh() < 0)
            setTicksUntilRefresh(0);

        if (getTicksUntilRefresh() == startRefresh)
            return "";

        /*
         * Change the factory's information (refresh time) Format:
         * HS|CF|weight$metatype$planet$name$timetorefresh$accessLevel|
         */
        String hsUpdate = "CF|" + getWeightclass() + "$" + getType() + "$" + getPlanet().getName() + "$" + getName() + "$" + getTicksUntilRefresh() + "$" + getAccessLevel() + "|";

        if (sendHSUpdate) {
            SHouse owner = getPlanet().getOwner();
            if (owner != null)
                CampaignMain.cm.doSendToAllOnlinePlayers(owner, "HS|" + hsUpdate, false);
        }

        return hsUpdate;
    }

    /**
     * @return Returns the planet.
     */
    public SPlanet getPlanet() {
        return planet;
    }

    /**
     * @param planet
     *            The planet to set.
     */
    public void setPlanet(SPlanet pl) {
        this.planet = pl;
    }

    /**
     * The cost (money) of a unit from this factory. Back referenced to the
     * faction that originally owned the world. Hacky. Ugly.
     */
    public int getPriceForUnit(int weightclass, int typeid) {
        SHouse originalHouse = (SHouse) CampaignMain.cm.getData().getHouseByName(this.getFounder());
        return originalHouse.getPriceForUnit(weightclass, typeid);
    }

    /**
     * The cost (flu) of a unit from this factory. Back referenced to the
     * faction that originally owned the world. Hacky. Ugly.
     */
    public int getInfluenceForUnit(int weightclass, int typeid) {
        SHouse originalHouse = (SHouse) CampaignMain.cm.getData().getHouseByName(this.getFounder());
        return originalHouse.getInfluenceForUnit(weightclass, typeid);
    }

    /**
     * The cost (PP) of a unit from this factory. Back referenced to the faction
     * that originally owned the world. Hacky. Ugly.
     */
    public int getPPCost(int weightclass, int typeid) {
        SHouse originalHouse = (SHouse) CampaignMain.cm.getData().getHouseByName(this.getFounder());
        return originalHouse.getPPCost(weightclass, typeid);
    }

    /**
     * This is used for tech raids. to increase other players build tables.
     * 
     * @param type_id
     * @return
     */
    public String getTechProduced(int type_id) {

        // Build the fluff text for the mek
        String Filename = "";
        String producer = "Built by ";
        if (this.getPlanet().getOwner() != null)
            producer += this.getPlanet().getOwner().getName();
        else
            producer += this.getFounder();

        /*
         * add a production location to the fluff, if from a normal planet. null
         * planet will normally be reward point production.
         */
        if (this.getPlanet().getName() != null)
            producer += " on " + this.getPlanet().getName();

        String unitSize = getSize();
        if (CampaignMain.cm.getBooleanConfig("UseOnlyOneVehicleSize") && type_id == Unit.VEHICLE)
            unitSize = Unit.getWeightClassDesc(CampaignMain.cm.getRandomNumber(4));

        Filename = BuildTable.getUnitFilename(this.getFounder(), unitSize, type_id, getBuildTableFolder());

        return Filename;
    }
}
