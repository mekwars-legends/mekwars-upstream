/*
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

package server.campaign;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;
import java.awt.Dimension;

import common.AdvancedTerrain;
import common.CampaignData;
import common.Continent;
import common.House;
import common.Influences;
import common.PlanetEnvironment;
import common.Unit;
import common.UnitFactory;
import common.util.Position;

import server.MWServ;
import server.campaign.data.TimeUpdatePlanet;
import server.util.TokenReader;

public class SPlanet extends TimeUpdatePlanet implements Serializable, Comparable<Object> {

    /**
     * 
     */
    private static final long serialVersionUID = -2266871107987235842L;
    private SHouse owner = null;

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("PL#");
        result.append(getName());
        result.append("#");
        result.append(getCompProduction());
        if (getUnitFactories() != null) {
            result.append("#" + getUnitFactories().size());
            for (UnitFactory factory : getUnitFactories()) {
                // int i = 0; i < getUnitFactories().size(); i++) {
                // SUnitFactory MF = (SUnitFactory) getUnitFactories().get(i);
                result.append("#" + ((SUnitFactory) factory).toString());
            }
        } else
            result.append("#0");

        result.append("#" + getPosition().getX());
        result.append("#" + getPosition().getY());
        result.append("#");
        for (House house : getInfluence().getHouses()) {
            SHouse next = (SHouse) house;
            if ( next == null )
                continue;
            result.append(next.getName());
            result.append("$"); // change for unusual influence
            result.append(getInfluence().getInfluence(next.getId()));
            result.append("$"); // change for unusual influence
        }
        // No Influences then set influence to NewbieHouse so the planet will
        // load.
        if (getInfluence().getHouses().size() < 1) {
            result.append(CampaignMain.cm.getConfig("NewbieHouseName"));
            result.append("$");
            result.append(this.getConquestPoints());
            result.append("$");
        }

        result.append("#");
        result.append(getEnvironments().size());
        result.append("#");
        for (Continent t : getEnvironments().toArray()) {
            result.append(t.getSize());
            result.append("#");
            result.append(t.getEnvironment().getName());
            result.append("#");
            if (CampaignMain.cm.getBooleanConfig("UseStaticMaps")) {
                AdvancedTerrain aTerrain = this.getAdvancedTerrain().get(new Integer(t.getEnvironment().getId()));
                if (aTerrain == null)
                    aTerrain = new AdvancedTerrain(); // no data start it
                // over. first time
                // starting advanced
                // maps.
                if (aTerrain.getDisplayName().length() <= 1)
                    aTerrain.setDisplayName(t.getEnvironment().getName());
                result.append(aTerrain.toString());
                result.append("#");
            }
        }
        if (getDescription().equals(""))
            result.append(" ");
        else
            result.append(getDescription());
        result.append("#");
        result.append(this.getBaysProvided());
        result.append("#");
        result.append(this.isConquerable());
        result.append("#");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        result.append(sdf.format(this.getLastChanged()));
        result.append("#");
        result.append(-1);
        result.append("#");
        result.append(getMapSize().width);
        result.append("#");
        result.append(getMapSize().height);
        result.append("#");
        result.append(getBoardSize().width);
        result.append("#");
        result.append(getBoardSize().height);
        result.append("#");
        result.append(getTemp().width);
        result.append("#");
        result.append(getTemp().height);
        result.append("#");
        result.append(getGravity());
        result.append("#");
        result.append(isVacuum());
        result.append("#");
        result.append(getNightChance());
        result.append("#");
        result.append(getNightTempMod());
        result.append("#");
        result.append(this.getMinPlanetOwnerShip());
        result.append("#");
        result.append(isHomeWorld());
        result.append("#");
        result.append(getOriginalOwner());
        result.append("#");

        if (this.getPlanetFlags().size() > 0) {
            for (String key : this.getPlanetFlags().keySet()) {
                result.append(key + "^");
            }

            result.append("#");
        } else
            result.append("^^#");

        result.append(this.getConquestPoints());
        result.append("#");

        return result.toString();
    }

    public void toDB() {
        try {
            if (getDBID() == 0) {
                // It's a new planet, INSERT it.
                Statement stmt = CampaignMain.cm.MySQL.getStatement();
                ResultSet rs = null;
                StringBuffer sql = new StringBuffer();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                PreparedStatement ps = null;

                sql.append("INSERT into planets set pCompProd = ?, ");
                sql.append("pXpos = ?, ");
                sql.append("pYpos = ?, ");
                sql.append("pDesc = ?, ");
                sql.append("pBays = ?, ");
                sql.append("pIsConquerable = ?, ");
                sql.append("pLastChanged = ?, ");
                sql.append("pMWID = ?, ");
                sql.append("pMapSizeWidth = ?, ");
                sql.append("pMapSizeHeight = ?, ");
                sql.append("pBoardSizeWidth = ?, ");
                sql.append("pBoardSizeHeight = ?, ");
                sql.append("pTempWidth = ?, ");
                sql.append("pTempHeight = ?, ");
                sql.append("pGravity = ?, ");
                sql.append("pVacuum = ?, ");
                sql.append("pNightChance = ?, ");
                sql.append("pNightTempMod = ?, ");
                sql.append("pMinPlanetOwnership = ?, ");
                sql.append("pIsHomeworld = ?, ");
                sql.append("pOriginalOwner = ?, ");
                sql.append("pMaxConquestPoints = ?, ");
                sql.append("pName = ?");

                ps = CampaignMain.cm.MySQL.getPreparedStatement(sql.toString(), PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setInt(1, getCompProduction());
                ps.setDouble(2, getPosition().getX());
                ps.setDouble(3, getPosition().getY());
                ps.setString(4, getDescription());
                ps.setInt(5, getBaysProvided());
                ps.setBoolean(6, isConquerable());
                ps.setString(7, sdf.format(getLastChanged()));
                ps.setInt(8, getId());
                ps.setInt(9, getMapSize().width);
                ps.setInt(10, getMapSize().height);
                ps.setInt(11, getBoardSize().width);
                ps.setInt(12, getBoardSize().height);
                ps.setInt(13, getTemp().width);
                ps.setInt(14, getTemp().height);
                ps.setDouble(15, getGravity());
                ps.setBoolean(16, isVacuum());
                ps.setInt(17, getNightChance());
                ps.setInt(18, getNightTempMod());
                ps.setInt(19, getMinPlanetOwnerShip());
                ps.setBoolean(20, isHomeWorld());
                ps.setString(21, getOriginalOwner());
                ps.setInt(22, getConquestPoints());
                ps.setString(23, getName());

                ps.executeUpdate();

                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int pid = rs.getInt(1);
                    setDBID(pid);

                    /**
                     * If it didn't get us an ID, there's not much point in
                     * doing the following:
                     * 
                     * Now, we need to save all the vectors: Factories Influence
                     * Environments planet flags
                     */
                    if (getUnitFactories() != null) {
                        for (int i = 0; i < getUnitFactories().size(); i++) {
                            SUnitFactory MF = (SUnitFactory) getUnitFactories().get(i);
                            MF.toDB();
                        }
                    }
                    // Save Influences
                    CampaignMain.cm.MySQL.saveInfluences(this);

                    // Save Environments

                    CampaignMain.cm.MySQL.saveEnvironments(this);

                    // Save Planet Flags
                    if (getPlanetFlags().size() > 0)
                        CampaignMain.cm.MySQL.savePlanetFlags(this);

                }
                rs.close();
                ps.close();
                if (stmt != null)
                    stmt.close();
            } else {
                // It's already in the database, UPDATE it
                Statement stmt = CampaignMain.cm.MySQL.getStatement();
                StringBuffer sql = new StringBuffer();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                PreparedStatement ps = null;

                sql.append("UPDATE planets set pCompProd = ?, ");
                sql.append("pXpos = ?, ");
                sql.append("pYpos = ?, ");
                sql.append("pDesc = ?, ");
                sql.append("pBays = ?, ");
                sql.append("pIsConquerable = ?, ");
                sql.append("pLastChanged = ?, ");
                sql.append("pMWID = ?, ");
                sql.append("pMapSizeWidth = ?, ");
                sql.append("pMapSizeHeight = ?, ");
                sql.append("pBoardSizeWidth = ?, ");
                sql.append("pBoardSizeHeight = ?, ");
                sql.append("pTempWidth = ?, ");
                sql.append("pTempHeight = ?, ");
                sql.append("pGravity = ?, ");
                sql.append("pVacuum = ?, ");
                sql.append("pNightChance = ?, ");
                sql.append("pNightTempMod = ?, ");
                sql.append("pMinPlanetOwnership = ?, ");
                sql.append("pIsHomeworld = ?, ");
                sql.append("pOriginalOwner = ?, ");
                sql.append("pMaxConquestPoints = ?, ");
                sql.append("pName = ? ");
                sql.append("WHERE PlanetID = ?");

                ps = CampaignMain.cm.MySQL.getPreparedStatement(sql.toString());

                ps.setInt(1, getCompProduction());
                ps.setDouble(2, getPosition().getX());
                ps.setDouble(3, getPosition().getY());
                ps.setString(4, getDescription());
                ps.setInt(5, getBaysProvided());
                ps.setBoolean(6, isConquerable());
                ps.setString(7, sdf.format(getLastChanged()));
                ps.setInt(8, getId());
                ps.setInt(9, getMapSize().width);
                ps.setInt(10, getMapSize().height);
                ps.setInt(11, getBoardSize().width);
                ps.setInt(12, getBoardSize().height);
                ps.setInt(13, getTemp().width);
                ps.setInt(14, getTemp().height);
                ps.setDouble(15, getGravity());
                ps.setBoolean(16, isVacuum());
                ps.setInt(17, getNightChance());
                ps.setInt(18, getNightTempMod());
                ps.setInt(19, getMinPlanetOwnerShip());
                ps.setBoolean(20, isHomeWorld());
                ps.setString(21, getOriginalOwner());
                ps.setInt(22, getConquestPoints());
                ps.setString(23, getName());
                ps.setInt(24, getDBID());

                ps.executeUpdate();

                /**
                 * Now, we need to save all the vectors: Factories Influence
                 * Environments planet flags
                 */
                if (getUnitFactories() != null) {
                    for (int i = 0; i < getUnitFactories().size(); i++) {
                        SUnitFactory MF = (SUnitFactory) getUnitFactories().get(i);
                        MF.toDB();
                    }
                }
                // Save Influences
                CampaignMain.cm.MySQL.saveInfluences(this);

                // Save Environments

                CampaignMain.cm.MySQL.saveEnvironments(this);

                // Save Planet Flags
                if (getPlanetFlags().size() > 0)
                    CampaignMain.cm.MySQL.savePlanetFlags(this);
                ps.close();
                if (stmt != null)
                    stmt.close();
            }
        } catch (SQLException e) {
            MWServ.mwlog.dbLog(e.getMessage());
        }

    }

    /**
     * 
     */
    public String fromString(String s, Random r, CampaignData data) {
        // debug
        MWServ.mwlog.mainLog(s);
        s = s.substring(3);
        StringTokenizer ST = new StringTokenizer(s, "#");
        setName(TokenReader.readString(ST));
        setCompProduction(TokenReader.readInt(ST));
        // Read Factories
        if (!CampaignMain.cm.isUsingMySQL()) {
            int hasMF = TokenReader.readInt(ST);
            for (int i = 0; i < hasMF; i++) {
                SUnitFactory mft = new SUnitFactory();
                mft.fromString(TokenReader.readString(ST), this, r);
                getUnitFactories().add(mft);
            }
        } else {
            // Load from the database
            CampaignMain.cm.MySQL.loadFactories(this);
            int hasMF = TokenReader.readInt(ST);
            for (int i = 0; i < hasMF; i++)
                TokenReader.readString(ST);
        }
        setPosition(new Position(TokenReader.readDouble(ST), TokenReader.readDouble(ST)));

        int Infcount = 0;
        try {
            HashMap<Integer, Integer> influence = new HashMap<Integer, Integer>();
            {
                StringTokenizer influences = new StringTokenizer(TokenReader.readString(ST), "$");
                
                while (influences.hasMoreElements()) {
                    String HouseName = influences.nextToken();
                    SHouse h = (SHouse) data.getHouseByName(HouseName);
                    Integer HouseInf = new Integer(influences.nextToken());
                    Infcount += HouseInf;
                    if (h != null)
                        influence.put(h.getId(), HouseInf);
                    else
                        MWServ.mwlog.errLog("House not found: " + HouseName);
                }
            }
            // getInfluence().setInfluence(influence);
            setInfluence(new Influences(influence));
        } catch (RuntimeException ex) {
            MWServ.mwlog.errLog("Problem on Planet: " + this.getName());
            MWServ.mwlog.errLog(ex);
        }
        int Envs = TokenReader.readInt(ST);
        for (int i = 0; i < Envs; i++) {
            int size = TokenReader.readInt(ST);
            String terrain = TokenReader.readString(ST);
            int terrainNumber = 0;
            PlanetEnvironment planetEnvironment = null;

            /*
             * Bug reported if you screw with the positions of the terrains in
             * terrain.xml you'll screw up the planet terrains this will now
             * allow you to load via int and then save via name so the terrain
             * will always be correct no matter the position of the terrain in
             * the terrain.xml.
             */
            try {
                terrainNumber = Integer.parseInt(terrain);
                planetEnvironment = data.getTerrain(terrainNumber);
            } catch (Exception ex) {
                planetEnvironment = data.getTerrainByName(terrain);
            }

            Continent PE = new Continent(size, planetEnvironment);
            if (CampaignMain.cm.getBooleanConfig("UseStaticMaps")) {
                AdvancedTerrain aTerrain = new AdvancedTerrain();

                String tempHolder = TokenReader.readString(ST);
                if (tempHolder.indexOf("$") < 0) {
                    aTerrain.setDisplayName(tempHolder);
                    aTerrain.setXSize(TokenReader.readInt(ST));
                    aTerrain.setYSize(TokenReader.readInt(ST));
                    aTerrain.setStaticMap(TokenReader.readBoolean(ST));
                    aTerrain.setXBoardSize(TokenReader.readInt(ST));
                    aTerrain.setYBoardSize(TokenReader.readInt(ST));
                    aTerrain.setLowTemp(TokenReader.readInt(ST));
                    aTerrain.setHighTemp(TokenReader.readInt(ST));
                    aTerrain.setGravity(TokenReader.readDouble(ST));
                    aTerrain.setVacuum(TokenReader.readBoolean(ST));
                    aTerrain.setNightChance(TokenReader.readInt(ST));
                    aTerrain.setNightTempMod(TokenReader.readInt(ST));
                    aTerrain.setStaticMapName(TokenReader.readString(ST));
                } else {
                    aTerrain = new AdvancedTerrain(tempHolder);
                }
                this.getAdvancedTerrain().put(new Integer(PE.getEnvironment().getId()), aTerrain);
            }
            getEnvironments().add(PE);
        }

        setDescription(TokenReader.readString(ST));

        this.setBaysProvided(TokenReader.readInt(ST));

        setConquerable(TokenReader.readBoolean(ST));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            setTimestamp(sdf.parse(TokenReader.readString(ST)));
        } catch (Exception ex) {
            // No biggy, but will cause senseless Data transfer, so:
            MWServ.mwlog.errLog("The following excepion is not critical, but will cause useless bandwith usage: please fix!");
            MWServ.mwlog.errLog(ex);
            setTimestamp(new Date(0));
        }

        TokenReader.readString(ST);
        setId(-1);
        int x = (TokenReader.readInt(ST));
        int y = (TokenReader.readInt(ST));
        setMapSize(new Dimension(x, y));

        x = (TokenReader.readInt(ST));
        y = (TokenReader.readInt(ST));
        setBoardSize(new Dimension(x, y));

        x = (TokenReader.readInt(ST));
        y = (TokenReader.readInt(ST));
        setTemp(new Dimension(x, y));

        setGravity(TokenReader.readDouble(ST));

        setVacuum(TokenReader.readBoolean(ST));
        int chance = (TokenReader.readInt(ST));
        int mod = (TokenReader.readInt(ST));
        setNightChance(chance);
        setNightTempMod(mod);

        setMinPlanetOwnerShip(TokenReader.readInt(ST));

        setHomeWorld(TokenReader.readBoolean(ST));

        setOriginalOwner(TokenReader.readString(ST));

        StringTokenizer str = new StringTokenizer(TokenReader.readString(ST), "^");
        TreeMap<String, String> map = new TreeMap<String, String>();
        while (str.hasMoreTokens()) {
            String key = str.nextToken();
            if (CampaignMain.cm.getData().getPlanetOpFlags().containsKey(key))
                map.put(key, CampaignMain.cm.getData().getPlanetOpFlags().get(key));
        }
        this.setPlanetFlags(map);

        this.setConquestPoints(TokenReader.readInt(ST));

        updateInfluences();

        setOwner(null, checkOwner(), false);

        return s;
    }

    /**
     * Use the other constructor as soon as you do not need the manual
     * serialization support through fromString() anymore.
     * 
     */
    public SPlanet() {
        // super(CampaignMain.cm.getData().getUnusedPlanetID(),"", new
        // Position(0,0), null);
        super();
        setTimestamp(new Date(0));
        setOriginalOwner(CampaignMain.cm.getConfig("NewbieHouseName"));
    }

    public SPlanet(int id, String name, Influences flu, int income, int CompProd, double xcood, double ycood) {
        super(id, name, new Position(xcood, ycood), flu);
        setCompProduction(CompProd);
        setTimestamp(new Date(0));
        setOriginalOwner(CampaignMain.cm.getConfig("NewbieHouseName"));
    }

    public SUnitFactory getRandomUnitFactory() {
        if (getUnitFactories().size() == 0)
            return null;
        // else
        return (SUnitFactory) getUnitFactories().get(CampaignMain.cm.getRandomNumber(getUnitFactories().size()));
    }

    public SUnitFactory getBestUnitFactory() {
        if (getUnitFactories().size() == 0)
            return null;
        SUnitFactory result = null;
        for (int i = 0; i < getUnitFactories().size(); i++) {
            SUnitFactory MF = (SUnitFactory) getUnitFactories().get(i);
            if (result == null)
                result = MF;
            else {
                if (MF.getWeightclass() > result.getWeightclass()) {
                    result = MF;
                } else if (MF.getWeightclass() == result.getWeightclass()) {
                    if (MF.getBestTypeProducable() < result.getBestTypeProducable())
                        result = MF;
                }
            }
        }
        return result;
    }

    public Vector<SUnitFactory> getFactoriesByName(String s) {
        Vector<SUnitFactory> result = new Vector<SUnitFactory>(getUnitFactories().size(), 1);
        for (int i = 0; i < getUnitFactories().size(); i++) {
            SUnitFactory MF = (SUnitFactory) getUnitFactories().get(i);
            if (MF.getName().equals(s))
                result.add(MF);
        }
        return result;
    }

    public Vector<SUnitFactory> getFactoriesOfWeighclass(int weightclass) {
        Vector<SUnitFactory> result = new Vector<SUnitFactory>(getUnitFactories().size(), 1);
        for (int i = 0; i < getUnitFactories().size(); i++) {
            SUnitFactory MF = (SUnitFactory) getUnitFactories().get(i);
            if (MF.getWeightclass() == weightclass)
                result.add(MF);
        }
        return result;
    }

    /**
     * @param Attacker -
     *            attacking faction
     * @return potential defending houses (ie - those with territory on the
     *         world)
     */
    public Vector<House> getDefenders(SHouse Attacker) {
        Vector<House> result = new Vector<House>(getInfluence().getHouses());
        result.trimToSize();
        /*
         * Iterator it = getInfluence().getHouses().iterator(); while
         * (it.hasNext()) { SHouse h = (SHouse) it.next(); //if
         * (!h.equals(Attacker) || Attacker.isInHouseAttacks()) result.add(h); }
         */
        return result;
    }

    @Override
    public boolean equals(Object o) {

        SPlanet p = null;
        try {
            p = (SPlanet) o;
        } catch (ClassCastException e) {
            return false;
        }

        if (o == null)
            return false;

        return p.getId() == this.getId();
    }

    /**
     * Do a tick - call tick on he planets MF, if it has one, and return the
     * amount of income generated by the planet Income = base income * the
     * number of miniticks registered at a tick
     */
    public String tick(int refreshminiticks) {
        // Tick all Factories
        StringBuilder hsUpdates = new StringBuilder();
        for (int i = 0; i < getUnitFactories().size(); i++) {
            SUnitFactory MF = (SUnitFactory) getUnitFactories().get(i);
            int total = 0;
            if (MF.canProduce(Unit.MEK))
                total += refreshminiticks;
            if (MF.canProduce(Unit.VEHICLE))
                total += refreshminiticks;
            if (MF.canProduce(Unit.INFANTRY))
                total += refreshminiticks;
            if (MF.canProduce(Unit.PROTOMEK))
                total += refreshminiticks;
            if (MF.canProduce(Unit.BATTLEARMOR))
                total += refreshminiticks;
            hsUpdates.append(MF.addRefresh(-total, false));
        }
        return hsUpdates.toString();
    }

    public String getSmallStatus(boolean useHTML) {

        StringBuilder result = new StringBuilder();
        if (useHTML)
            result.append(this.getNameAsColoredLink());
        else
            result.append(getName());

        for (int i = 0; i < getUnitFactories().size(); i++) {
            SUnitFactory MF = (SUnitFactory) getUnitFactories().get(i);
            result.append(" [" + MF.getSize() + "," + MF.getFounder() + "," + MF.getTypeString() + "]");
        }

        result.append(":");
        for (House h : getInfluence().getHouses()) {
            result.append(h.getName() + "(" + getInfluence().getInfluence(h.getId()) + "cp)");
            result.append(", ");

        }
        if (useHTML)
            result.replace(result.length() - 2, result.length(), "<br>");
        return result.toString();
    }

    public SHouse checkOwner() {

        if (getInfluence() == null) {
            MWServ.mwlog.errLog("getINF == null Planet: " + getName());
            return null;
        }

        SHouse h = null;
        Integer houseID = this.getInfluence().getOwner();

        if (houseID == null)
            return null;

        h = (SHouse) CampaignMain.cm.getData().getHouse(houseID);

        if (this.getInfluence().getInfluence(houseID) < this.getMinPlanetOwnerShip())
            return null;

        return h;
    }

    public SHouse getOwner() {
        /*
         * Null owner is possible, but should be uncommon. Check the owner again
         * to make sure the this is true before returning.
         */
        if (owner == null)
            checkOwner();
        return owner;
    }

    public void setOwner(SHouse oldOwner, SHouse newOwner, boolean sendHouseUpdates) {

        if (owner != null)// this is the same as oldowner in most cases
            owner.removePlanet(this);

        if (newOwner != null) {
            owner = newOwner;
            owner.addPlanet(this);
        }

        if (sendHouseUpdates)
            this.sendHouseStatusUpdate(oldOwner, newOwner);
    }

    public int doGainInfluence(SHouse winner, SHouse loser, int amount, boolean adminExchange) {

        if (!winner.isConquerable() && !adminExchange)
            return 0;

        int infgain = getInfluence().moveInfluence(winner, loser, amount, this.getConquestPoints());
        // dont bother with updates if land has not changed hands.
        if (infgain > 0) {

            // winner.updated();
            // loser.updated();
            this.updated();

            SHouse oldOwner = owner;
            SHouse newOwner = checkOwner();
            setOwner(oldOwner, newOwner, true);
        }
        return infgain;
    }

    /*
     * Helper method that sends updates to online players when a world changes
     * hands.
     */
    private void sendHouseStatusUpdate(SHouse oldOwner, SHouse newOwner) {

        // don't do anything if there's no change is ownership
        if (oldOwner != null && oldOwner.equals(newOwner))
            return;
        else if (oldOwner == null && newOwner == null)
            return;

        // if the world has factories, build strings to send
        StringBuilder oldOwnerHSUpdates = new StringBuilder();
        StringBuilder newOwnerHSUpdates = new StringBuilder();
        for (UnitFactory currUF : getUnitFactories()) {

            oldOwnerHSUpdates.append("RF|" + currUF.getWeightclass() + "$" + currUF.getType() + "$" + this.getName() + "$" + currUF.getName() + "|");

            newOwnerHSUpdates.append("AF|" + currUF.getWeightclass() + "$" + currUF.getType() + "$" + currUF.getFounder() + "$" + this.getName() + "$" + currUF.getName() + "$" + currUF.getTicksUntilRefresh() + "|");
            newOwnerHSUpdates.append(currUF.getAccessLevel());
            newOwnerHSUpdates.append("|");
        }

        // send updates to non-null houses, so long as update strings have
        // length > 0 (real updates)
        if (oldOwner != null && oldOwnerHSUpdates.length() > 0)
            CampaignMain.cm.doSendToAllOnlinePlayers(oldOwner, "HS|" + oldOwnerHSUpdates.toString(), false);
        if (newOwner != null && newOwnerHSUpdates.length() > 0)
            CampaignMain.cm.doSendToAllOnlinePlayers(newOwner, "HS|" + newOwnerHSUpdates.toString(), false);

    }

    public String getShortDescription(boolean withTerrain) {
        StringBuilder result = new StringBuilder(getName());
        if (withTerrain) {
            Continent p = getEnvironments().getBiggestEnvironment();
            PlanetEnvironment pe = p.getEnvironment();
            if (pe != null)
                result.append(" " + pe.toImageDescription());

            if (this.getUnitFactories().size() > 0) {
                for (int i = 0; i < this.getUnitFactories().size(); i++) {
                    SUnitFactory MF = ((SUnitFactory) this.getUnitFactories().get(i));
                    result.append(MF.getIcons());
                }
            }
            if (pe != null && getEnvironments().getTotalEnivronmentPropabilities() > 0)
                result.append(" (" + Math.round((double) p.getSize() * 100 / getEnvironments().getTotalEnivronmentPropabilities()) + "% correct)");
            else
                result.append(" (100% correct)");
        }
        return result.toString();
    }

    /**
     * Method which returns a coloured link name for a planet.
     */
    public String getNameAsColoredLink() {

        String colorString = "";
        if (owner == null) {
            colorString = CampaignMain.cm.getConfig("DisputedPlanetColor");// malformed
            // gets
            // you
            // black?
        } else
            colorString = owner.getHouseColor();

        String toReturn = "<font color=\"" + colorString + "\">" + getNameAsLink() + "</font>";
        return toReturn;
    }

    @Override
    public int getMinPlanetOwnerShip() {

        int ownership = super.getMinPlanetOwnerShip();
        if (ownership < 0)
            ownership = CampaignMain.cm.getIntegerConfig("MinPlanetOwnerShip");

        return ownership;
    }

}