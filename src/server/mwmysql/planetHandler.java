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

import java.awt.Dimension;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeMap;

import server.campaign.CampaignMain;
import server.campaign.SHouse;
import server.campaign.SPlanet;

import common.AdvancedTerrain;
import common.CampaignData;
import common.Continent;
import common.House;
import common.Influences;
import common.Terrain;
import common.util.Position;

public class planetHandler {
    JDBCConnectionHandler ch = new JDBCConnectionHandler();

    public int countPlanets() {
    	Connection con = ch.getConnection();
        int num = 0;
        try {

            ResultSet rs = null;
            Statement stmt = con.createStatement();
            String sql = "SELECT COUNT(*) as numplanets from planets";

            rs = stmt.executeQuery(sql);
            rs.next();
            num = rs.getInt("numplanets");
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            CampaignData.mwlog.dbLog("SQL Error in countPlanets: " + e.getMessage());
            CampaignData.mwlog.dbLog(e);
        }
        ch.returnConnection(con);
        return num;
    }

    public void deletePlanet(int PlanetID) {
    	Connection con = ch.getConnection();
        try {
            Statement stmt = con.createStatement();
            stmt.executeUpdate("DELETE from planets WHERE PlanetID = " + PlanetID);
            stmt.executeUpdate("DELETE from planetenvironments WHERE PlanetID = " + PlanetID);
            stmt.executeUpdate("DELETE from planetflags WHERE PlanetID = " + PlanetID);
            stmt.executeUpdate("DELETE from planetinfluences WHERE PlanetID = " + PlanetID);
            stmt.close();
        } catch (SQLException e) {
            CampaignData.mwlog.dbLog("SQL Error in deletePlanet: " + e.getMessage());
            CampaignData.mwlog.dbLog(e);
        }
        ch.returnConnection(con);
    }

    public void loadPlanets(CampaignData data) {
    	ResultSet rs = null;
    	Statement stmt = null;
    	Connection con = ch.getConnection();
    	
        try {
            stmt = con.createStatement();
            
            String sql = "SELECT * from planets";
            
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
            	SPlanet p = new SPlanet();
            	if (rs.getString("pString") != null && !rs.getString("pString").trim().equalsIgnoreCase("")){
            		p.fromString(rs.getString("pString"), CampaignMain.cm.getR(), data);
            		int id = rs.getInt("pMWID");
            		if (id == -1) {
            			id = CampaignData.cd.getUnusedPlanetID();
            		}
            		p.setId(id);
            		p.setDBID(rs.getInt("PlanetID"));
            	} else {
            		p.setCompProduction(rs.getInt("pCompProd"));
            		p.setPosition(new Position(rs.getFloat("pXpos"), rs.getFloat("pYpos")));
            		p.setDescription(rs.getString("pDesc"));
            		p.setBaysProvided(rs.getInt("pBays"));
            		p.setConquerable(rs.getBoolean("pIsConquerable"));
            		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            		try {
            			p.setTimestamp(sdf.parse(rs.getString("pLastChanged")));
            		} catch (Exception ex) {
            			CampaignData.mwlog.errLog("The following exception is not critical, but will cause useless bandwidth usage: please fix!");
            			CampaignData.mwlog.errLog(ex);
            			p.setTimestamp(new Date(0));
            		}

            		//p.setId(-1);
            		int id = rs.getInt("pMWID");
            		if (id == -1) {
            			id = CampaignData.cd.getUnusedPlanetID();
            		}
            		p.setId(id);
            		p.setMinPlanetOwnerShip(rs.getInt("pMinPlanetOwnership"));
            		p.setHomeWorld(rs.getBoolean("pIsHomeworld"));
            		p.setOriginalOwner(rs.getString("pOriginalOwner"));
            		p.setConquestPoints(rs.getInt("pMaxConquestPoints"));
            		p.setName(rs.getString("pName"));
            		p.setDBID(rs.getInt("PlanetID"));

            		// Add the vectors now

            		// Influences
            		loadInfluences(p, data);

            		// Environments
            		loadEnvironments(p, data);

            		// Flags
            		loadPlanetFlags(p, data);

            		// Factories
            		CampaignMain.cm.MySQL.loadFactories(p);
            	}
                CampaignMain.cm.addPlanet(p);
                p.setOwner(null, p.checkOwner(), false);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            CampaignData.mwlog.dbLog("SQL Error in loadPlanets: " + e.getMessage());
            CampaignData.mwlog.dbLog(e);
        } finally {
        	if (rs != null){
        		try {
        			rs.close();
        		} catch (SQLException e) {}
        	}
        	if( stmt != null) {
        		try {
        			stmt.close();
        		} catch (SQLException e) {}
        	}
        }
        ch.returnConnection(con);
    }

    public void loadInfluences(SPlanet p, CampaignData data) {
    	Connection con = ch.getConnection();
        try {
            ResultSet rs1 = null;
            Statement stmt = con.createStatement();

            HashMap<Integer, Integer> influence = new HashMap<Integer, Integer>();

            rs1 = stmt.executeQuery("SELECT * from planetinfluences WHERE planetID = " + p.getDBID());

            while (rs1.next()) {
                Integer HouseInf = new Integer(rs1.getInt("influence"));
                String HouseName = rs1.getString("FactionName");
                SHouse h = (SHouse) data.getHouseByName(HouseName);
                if (h != null) {
                    influence.put(h.getId(), HouseInf);

                } else if ( HouseName.equalsIgnoreCase("None") )
                    influence.put(-1,HouseInf);
                else
                    CampaignData.mwlog.errLog("House not found: " + HouseName);
            }
            p.setInfluence(new Influences(influence));
            rs1.close();
            stmt.close();
        } catch (SQLException e) {
            CampaignData.mwlog.dbLog("SQL Error in loadInfluences: " + e.getMessage());
            CampaignData.mwlog.dbLog(e);
        }
        ch.returnConnection(con);
    }

    public void loadPlanetFlags(SPlanet p, CampaignData data) {
    	Connection con = ch.getConnection();
        try {
            ResultSet rs2 = null;
            Statement stmt = con.createStatement();

            TreeMap<String, String> map = new TreeMap<String, String>();

            rs2 = stmt.executeQuery("SELECT * from planetflags WHERE planetID = " + p.getDBID());
            while (rs2.next()) {
                String key = rs2.getString("PlanetFlag");
                if (CampaignMain.cm.getData().getPlanetOpFlags().containsKey(key))
                    map.put(key, CampaignMain.cm.getData().getPlanetOpFlags().get(key));
            }
            p.setPlanetFlags(map);
            rs2.close();
            stmt.close();
        } catch (SQLException e) {
            CampaignData.mwlog.dbLog("SQL Error in loadPlanetFlags: " + e.getMessage());
            CampaignData.mwlog.dbLog(e);
        }
        ch.returnConnection(con);
    }

    public void loadEnvironments(SPlanet p, CampaignData data) {
        ResultSet rs3 = null;
        Statement stmt = null;
        Connection con = ch.getConnection();
        
        try {
        	stmt = con.createStatement();
            rs3 = stmt.executeQuery("SELECT * from planetenvironments WHERE PlanetID = " + p.getDBID());

            while (rs3.next()) {
                int size = rs3.getInt("ContinentSize");
                Terrain planetEnvironment = null;
                AdvancedTerrain planetAdvancedTerrain = null;
                int terrainNumber = 0;
                int advTerrainNumber = 0;

                try {
                    terrainNumber = rs3.getInt("TerrainData");
                    planetEnvironment = data.getTerrain(terrainNumber);
                } catch (Exception ex) {
                    CampaignData.mwlog.dbLog(ex);
                    CampaignData.mwlog.dbLog("Unable to load Terrain #"+terrainNumber);
                    planetEnvironment = data.getTerrain(0);
                }
                
                if ( planetEnvironment == null )
                    planetEnvironment = data.getTerrain(0);

                try {
                    advTerrainNumber = rs3.getInt("AdvancedTerrainData");
                    planetAdvancedTerrain = data.getAdvancedTerrain(terrainNumber);
                } catch (Exception ex) {
                    CampaignData.mwlog.dbLog(ex);
                    CampaignData.mwlog.dbLog("Unable to load AdvancedTerrain #"+terrainNumber);
                    planetAdvancedTerrain = data.getAdvancedTerrain(0);
                }
                
                if ( planetAdvancedTerrain == null )
                	planetAdvancedTerrain = data.getAdvancedTerrain(0);

                Continent PE = new Continent(size, planetEnvironment,planetAdvancedTerrain);
                p.getEnvironments().add(PE);

            }
            rs3.close();
            stmt.close();
        } catch (SQLException e) {
            CampaignData.mwlog.dbLog("SQL Error in loadEnvironments: " + e.getMessage());
            CampaignData.mwlog.dbLog(e);
            try {
                if(rs3 != null)
                	rs3.close();
                if (stmt != null)
                	stmt.close();
            } catch (SQLException ex) {}
        }
        ch.returnConnection(con);
    }

    public void saveEnvironments(SPlanet p) {
        Statement stmt = null;
        StringBuffer sql = new StringBuffer();
        Connection con = ch.getConnection();
        
        try {
            stmt = con.createStatement();
            sql.append("DELETE from planetenvironments WHERE PlanetID = " + p.getDBID());
            stmt.executeUpdate(sql.toString());
            Iterator<Continent> it = p.getEnvironments().iterator();
            while (it.hasNext()) {
                Continent t = it.next();
                int size = t.getSize();
                StringBuffer atData = new StringBuffer();
                StringBuffer tName = new StringBuffer();

                tName.append(t.getEnvironment().getName());
                int tId = t.getEnvironment().getId();

                AdvancedTerrain aTerrain = t.getAdvancedTerrain();
                if (aTerrain == null)
                    aTerrain = new AdvancedTerrain();
                if (aTerrain.getName().length() <= 1)
                    aTerrain.setName(t.getEnvironment().getName());
                sql.setLength(0);
                sql.append("INSERT into planetenvironments set ");
                sql.append("PlanetID = " + p.getDBID() + ", ");
                sql.append("ContinentSize = " + size + ", ");
                sql.append("TerrainData = '" + tId + ",");
                sql.append("AdvancedTerrainData = " + aTerrain.getId());
                stmt.executeUpdate(sql.toString());
            }
            stmt.close();
        } catch (SQLException e) {
            CampaignData.mwlog.dbLog("SQL Error in saveEnvironments: " + e.getMessage());
            CampaignData.mwlog.dbLog(e);
        }
        ch.returnConnection(con);
    }

    public void savePlanetFlags(SPlanet p) {
        Statement stmt = null;
        Connection con = ch.getConnection();
        StringBuffer sql = new StringBuffer();

        try {
            stmt = con.createStatement();

            sql.append("DELETE from planetflags WHERE PlanetID = " + p.getDBID());
            stmt.executeUpdate(sql.toString());

            for (String key : p.getPlanetFlags().keySet()) {
                sql.setLength(0);
                sql.append("INSERT into planetflags set PlanetID = " + p.getDBID() + "PlanetFlag = '" + key + "'");
                stmt.executeUpdate(sql.toString());
            }
            stmt.close();

        } catch (SQLException e) {
            CampaignData.mwlog.dbLog("SQL Error in savePlanetFlags: " + e.getMessage());
            CampaignData.mwlog.dbLog(e);
        }
        ch.returnConnection(con);
    }

    public void saveInfluences(SPlanet p) {
        Iterator<House> it = p.getInfluence().getHouses().iterator();
        PreparedStatement ps = null;
        StringBuffer sql = new StringBuffer();
        int pid = p.getDBID();
        Connection con = ch.getConnection();

        try {
            ps = con.prepareStatement("DELETE from planetinfluences WHERE PlanetID = " + pid);
            ps.executeUpdate();
            while (it.hasNext()) {
            	ps.close();
                SHouse next = (SHouse) it.next();
                String iName = next.getName().replace("'", "\'");

                int iInf = p.getInfluence().getInfluence(next.getId());
                sql.setLength(0);
                sql.append("INSERT into planetinfluences set PlanetID = ?, ");
                sql.append("FactionName = ?, ");
                sql.append("Influence = ?");
                ps = con.prepareStatement(sql.toString());
                ps.setInt(1, pid);
                ps.setString(2, iName);
                ps.setInt(3, iInf);
                ps.execute();
            }
            
        } catch (SQLException e) {
            CampaignData.mwlog.dbLog("SQLException in planetHandler.saveInfluences: " + e.getMessage());
            CampaignData.mwlog.dbLog(e);
        } finally {
        	try {
        		if(ps != null)
        			ps.close();
        	} catch (SQLException ex) {}
        }
        ch.returnConnection(con);
    }

    // CONSTRUCTOR
    public planetHandler() {
        
    }
}
