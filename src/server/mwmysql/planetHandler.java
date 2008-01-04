package server.mwmysql;

import java.awt.Dimension;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeMap;

import common.AdvancedTerrain;
import common.CampaignData;
import common.Continent;
import common.House;
import common.Influences;
import common.PlanetEnvironment;
import common.util.Position;
import server.MWServ;
import server.campaign.CampaignMain;
import server.campaign.SHouse;
import server.campaign.SPlanet;

public class planetHandler {
  Connection con = null;

  public int countPlanets() {
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
		  MWServ.mwlog.dbLog("SQL Error in countPlanets: " + e.getMessage());
	  }
	  return num;
  }
  
  public void deletePlanet(int PlanetID) {
	  try {
		  Statement stmt = con.createStatement();
		  stmt.executeUpdate("DELETE from planets WHERE PlanetID = " + PlanetID);
		  stmt.executeUpdate("DELETE from planetenvironments WHERE PlanetID = " + PlanetID);
		  stmt.executeUpdate("DELETE from planetfactories WHERE PlanetID = " + PlanetID);
		  stmt.executeUpdate("DELETE from planetflags WHERE PlanetID = " + PlanetID);
		  stmt.executeUpdate("DELETE from planetinfluences WHERE PlanetID = " + PlanetID);
		  stmt.close();
	  } catch (SQLException e) {
		  MWServ.mwlog.dbLog("SQL Error in deletePlanet: " + e.getMessage());
	  }
	  
  }

  @SuppressWarnings("unchecked")
public void loadPlanets(CampaignData data) {
	
	  try {
		  ResultSet rs = null;

		  Statement stmt = con.createStatement();
		  String sql = "SELECT * from planets";
		  
		  rs = stmt.executeQuery(sql);
		  while(rs.next()) {
			  SPlanet p = new SPlanet();

			  p.setCompProduction(rs.getInt("pCompProd"));
 		      p.setPosition(new Position(rs.getFloat("pXpos"), rs.getFloat("pYpos")));
			  p.setDescription(rs.getString("pDesc"));
			  p.setBaysProvided(rs.getInt("pBays"));
			  p.setConquerable(rs.getBoolean("pIsConquerable"));
			  SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			  try {
				  p.setTimestamp(sdf.parse(rs.getString("pLastChanged")));
			    } catch (Exception ex) {
			    	MWServ.mwlog.errLog("The following exception is not critical, but will cause useless bandwidth usage: please fix!");
			    	MWServ.mwlog.errLog(ex);
			    	p.setTimestamp(new Date(0));
			    }

			  p.setId(-1);

			  p.setMapSize(new Dimension(rs.getInt("pMapSizeWidth"), rs.getInt("pMapSizeHeight")));
			  p.setBoardSize(new Dimension(rs.getInt("pBoardSizeWidth"), rs.getInt("pBoardSizeHeight")));
			  p.setTemp(new Dimension(rs.getInt("pTempWidth"), rs.getInt("pTempHeight")));
			  p.setGravity(rs.getFloat("pGravity"));
			  p.setVacuum(rs.getBoolean("pVacuum"));
			  p.setNightChance(rs.getInt("pNightChance"));
			  p.setNightTempMod(rs.getInt("pNightTempMod"));
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
					  			  
			  CampaignMain.cm.addPlanet(p);
			  p.setOwner(null, p.checkOwner(), false);
		  }
		  rs.close();
		  stmt.close();
	  } catch (SQLException e) {
		  MWServ.mwlog.dbLog("SQL Error in loadPlanets: " + e.getMessage());
		}
  }
  
  @SuppressWarnings("unchecked")
public void loadInfluences(SPlanet p, CampaignData data) {
	  try {
		  ResultSet rs1 = null;
		  Statement stmt = con.createStatement();
		  
		  HashMap influence = new HashMap();
		  
		  rs1 = stmt.executeQuery("SELECT * from planetinfluences WHERE planetID = " + p.getDBID());

		  
		  while(rs1.next()) {
			  Integer HouseInf = new Integer(rs1.getInt("influence"));
			  String HouseName = rs1.getString("FactionName");
			  SHouse h = (SHouse)data.getHouseByName(HouseName);
			  if(h!= null){
			  influence.put(new Integer(h.getId()), HouseInf);

			  } else
				  MWServ.mwlog.errLog("House not found: " + HouseName);
		  }
		  p.setInfluence(new Influences(influence));
		  rs1.close();
		  stmt.close();
	  } catch (SQLException e) {
		  MWServ.mwlog.dbLog("SQL Error in loadInfluences: " + e.getMessage());
	  }
  }

  public void loadPlanetFlags(SPlanet p, CampaignData data) {
	  try {
		  ResultSet rs2 = null;
		  Statement stmt = con.createStatement();
		  
		  TreeMap<String, String> map = new TreeMap<String, String>();
			 
		  rs2 = stmt.executeQuery("SELECT * from planetflags WHERE planetID = " + p.getDBID());
		  while(rs2.next()) {
			  String key = rs2.getString("PlanetFlag");
			  if ( CampaignMain.cm.getData().getPlanetOpFlags().containsKey(key))
				  map.put(key, CampaignMain.cm.getData().getPlanetOpFlags().get(key));
		  }
		  p.setPlanetFlags(map);
		  rs2.close();
		  stmt.close();
	  } catch (SQLException e) {
		  MWServ.mwlog.dbLog("SQL Error in loadPlanetFlags: " + e.getMessage());
	  }
  }
  
  public void loadEnvironments(SPlanet p, CampaignData data) {
	  try {
	  
	  ResultSet rs3 = null;
	  Statement stmt = con.createStatement();
	  
	  rs3 = stmt.executeQuery("SELECT * from planetenvironments WHERE PlanetID = " + p.getDBID());
	  
	  while(rs3.next()) {
		  int size = rs3.getInt("ContinentSize");
		  PlanetEnvironment planetEnvironment = null;
		  int terrainNumber = 0;
		  
		  try {
			  terrainNumber = rs3.getInt("TerrainData");
			  planetEnvironment = data.getTerrain(terrainNumber);
		  } catch (Exception ex) {
			  planetEnvironment = data.getTerrain(terrainNumber);
		  }
		  Continent PE = new Continent(size, planetEnvironment);
		  if(CampaignMain.cm.getBooleanConfig("UseStaticMaps")) {
			  AdvancedTerrain aTerrain = new AdvancedTerrain();
			  
			  String tempHolder  = rs3.getString("AdvancedTerrainData");
			  if (tempHolder.length() > 0 ) {
				 StringTokenizer ST = new StringTokenizer(tempHolder, "$");
				 aTerrain.setDisplayName(ST.nextToken());
				 aTerrain.setXSize(Integer.parseInt(ST.nextToken()));
				 aTerrain.setYSize(Integer.parseInt(ST.nextToken()));
				 aTerrain.setStaticMap(Boolean.parseBoolean(ST.nextToken()));
				 aTerrain.setXBoardSize(Integer.parseInt(ST.nextToken()));
				 aTerrain.setYBoardSize(Integer.parseInt(ST.nextToken()));
				 aTerrain.setLowTemp(Integer.parseInt(ST.nextToken()));
				 aTerrain.setHighTemp(Integer.parseInt(ST.nextToken()));
				 aTerrain.setGravity(Double.parseDouble(ST.nextToken()));
				 aTerrain.setVacuum(Boolean.parseBoolean(ST.nextToken()));
				 aTerrain.setNightChance(Integer.parseInt(ST.nextToken()));
				 aTerrain.setNightTempMod(Integer.parseInt(ST.nextToken()));
				 aTerrain.setStaticMapName(ST.nextToken());
			  } else {
				  aTerrain = new AdvancedTerrain(tempHolder);
			  }
			  p.getAdvancedTerrain().put(new Integer(PE.getEnvironment().getId()), aTerrain);
		  }
		  p.getEnvironments().add(PE);

	  } 
	  rs3.close();
	  stmt.close();
	  } catch (SQLException e) {
		  MWServ.mwlog.dbLog("SQL Error in loadEnvironments: " + e.getMessage());
	  }
  }
  
  public void saveEnvironments(SPlanet p) {
	  Statement stmt = null;
	  StringBuffer sql = new StringBuffer();
	  
	  try {
		stmt = con.createStatement();
		sql.append("DELETE from planetenvironments WHERE PlanetID = " + p.getDBID());
		stmt.executeUpdate(sql.toString());
		Iterator<Continent> it = p.getEnvironments().iterator();
		while(it.hasNext()){
			Continent t = it.next();
			int size = t.getSize();
			StringBuffer atData = new StringBuffer();
			StringBuffer tName = new StringBuffer();
			
			tName.append(t.getEnvironment().getName());
			int tId = t.getEnvironment().getId();
			
			if(CampaignMain.cm.getBooleanConfig("UseStaticMaps")){
				/**
				 *  This is a hack.  Right now, to get this working,
				 *  I'm going to just store the terrain.fromString()
				 *  string.  I'll come back and break this out later.
				 */
				AdvancedTerrain aTerrain = p.getAdvancedTerrain().get(new Integer(t.getEnvironment().getId()));
				if( aTerrain == null )
					aTerrain = new AdvancedTerrain();
				if ( aTerrain.getDisplayName().length() <= 1)
					aTerrain.setDisplayName(t.getEnvironment().getName());
				atData.append(aTerrain.toString());
			}
			sql.setLength(0);
			sql.append("INSERT into planetenvironments set ");
			sql.append("PlanetID = " + p.getDBID() + ", ");
			sql.append("ContinentSize = " + size + ", ");
			sql.append("TerrainData = '" + tId + "'");
			if(CampaignMain.cm.getBooleanConfig("UseStaticMaps"))
			  sql.append(", AdvancedTerrainData = '" + atData.toString() + "'");
			stmt.executeUpdate(sql.toString());
		}
		stmt.close();
	  } catch (SQLException e) {
		  MWServ.mwlog.dbLog("SQL Error in saveEnvironments: " + e.getMessage());
	  }
  }
  
  public void savePlanetFlags(SPlanet p) {
	  Statement stmt = null;

	  StringBuffer sql = new StringBuffer();
	  
	  try {
		  stmt = con.createStatement();
		  
		  sql.append("DELETE from planetflags WHERE PlanetID = " + p.getDBID());
		  stmt.executeUpdate(sql.toString());
		  
		  for (String key : p.getPlanetFlags().keySet() ) {
			  sql.setLength(0);
			  sql.append("INSERT into planetflags set PlanetID = " + p.getDBID() + "PlanetFlag = '" + key + "'");
			  stmt.executeUpdate(sql.toString());
		  	}
		  stmt.close();
		  
		  }
	  catch (SQLException e) {
		  MWServ.mwlog.dbLog("SQL Error in savePlanetFlags: " + e.getMessage());
	  }
  }


  public void saveInfluences(SPlanet p) {
	  Iterator<House> it = p.getInfluence().getHouses().iterator();
	  Statement stmt;	  
	  StringBuffer sql = new StringBuffer();
	  int pid = p.getDBID();
	  
	  try {
		  stmt = con.createStatement();
		  sql.append("DELETE from planetinfluences WHERE PlanetID = " + pid);
		  stmt.executeUpdate(sql.toString());
		  while (it.hasNext()) {
			  SHouse next = (SHouse) it.next();
			  String iName = next.getName().replace("'", "\'");
		  
			  int iInf = p.getInfluence().getInfluence(next.getId());
			  sql.setLength(0);
			  sql.append("INSERT into planetinfluences set PlanetID = " + pid + ", ");
			  sql.append("FactionName = '" + iName + "', ");
			  sql.append("Influence = " + iInf);
			  stmt.executeUpdate(sql.toString());
	  		}
			stmt.close();
		} catch (SQLException e) {
			MWServ.mwlog.dbLog("Error closing resources in saveInfluences: " + e.getMessage());
		}
  }
 
   
  // CONSTRUCTOR
  public planetHandler(Connection c)
    {
    this.con = c;
    }
}
