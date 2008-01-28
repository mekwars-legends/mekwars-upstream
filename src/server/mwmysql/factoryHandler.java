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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import server.campaign.SUnitFactory;
import server.MWServ;
import server.campaign.SPlanet;

public class factoryHandler {
  Connection con = null;

  public void loadFactories(SPlanet planet)
    {
    ResultSet rs = null;
   
    try {
    PreparedStatement ps = null;	
    	

    ps = con.prepareStatement("SELECT * from factories WHERE FactoryPlanet = ?");
    ps.setString(1, planet.getName());

    rs = ps.executeQuery();
    while(rs.next())
      {
      SUnitFactory factory = new SUnitFactory();
      factory.setName(rs.getString("FactoryName"));
      factory.setSize(rs.getString("FactorySize"));
      factory.setFounder(rs.getString("FactoryFounder"));
      factory.setTicksUntilRefresh(rs.getInt("FactoryTicks"));
      factory.setRefreshSpeed(rs.getInt("FactoryRefreshSpeed"));
      factory.setID(rs.getInt("FactoryID"));
      factory.setType(rs.getInt("FactoryType"));
      factory.setBuildTableFolder(rs.getString("FactoryBuildTableFolder"));
      factory.setLock(Boolean.parseBoolean(rs.getString("FactoryisLocked")));
      factory.setAccessLevel(rs.getInt("FactoryAccessLevel"));
      factory.setPlanet(planet);
      planet.getUnitFactories().add(factory); 

      }
    rs.close();
    ps.close();
    } catch (SQLException e) {
      MWServ.mwlog.dbLog("SQL Error in factoryHandler.java: " + e.getMessage());
    }       
    }
  
  public int getFactoryIdByNameAndPlanet(String fName, String planetName) {
		ResultSet rs = null;
		  PreparedStatement ps = null;
		  
	  try {
		  ps = con.prepareStatement("SELECT factoryID from factories WHERE factoryPlanet = ? AND factoryName = ?");		  ps.setString(1, planetName);
		  ps.setString(2, fName);
		  rs = ps.executeQuery();
		  if(!rs.next()) {
			  rs.close();
			  ps.close();
			  return 0;
		  }
		  int fid = rs.getInt("factoryID");
		  rs.close();
		  ps.close();
		  return fid;
	} catch (SQLException e) {
		MWServ.mwlog.dbLog("SQLException in factoryHandler.getFactoryByNameAndPlanet: " + e.getMessage());
		return 0;
	}
	  
  }

  public void deleteFactory(int factoryID)
    {
    Statement stmt = null;
    String sql;
    
    try {
    	MWServ.mwlog.dbLog("Deleting factory " + factoryID);
      stmt = con.createStatement();
      sql = "DELETE from factories where FactoryID = " + factoryID;
      stmt.executeUpdate(sql);
      stmt.close();
      } catch (SQLException e) {
      MWServ.mwlog.dbLog("SQL ERROR in factoryHandler.java: " + e.getMessage());
      }
    }

  public void deletePlanetFactories(String planetName)
    {
    PreparedStatement stmt = null;
 
    try {
      stmt = con.prepareStatement("DELETE from factories where FactoryPlanet=?");
      stmt.setString(1, planetName);
      stmt.executeUpdate();
      stmt.close();
      }
      catch (SQLException e) {
        MWServ.mwlog.dbLog("SQL ERROR in factoryHandler.java: " + e.getMessage());
      }    
    }

  // CONSTRUCTOR
  public factoryHandler(Connection c)
    {
    this.con = c;
    }
}
