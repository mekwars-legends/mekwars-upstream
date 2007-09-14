package server.mwmysql;

//import java.sql.*;
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
      factory.setLock(Boolean.parseBoolean(rs.getString("FactoryisLocked")));
      factory.setPlanet(planet);
      planet.getUnitFactories().add(factory); 

      }
    rs.close();
    ps.close();
    } catch (SQLException e) {
      MWServ.mwlog.dbLog("SQL Error in factoryHandler.java: " + e.getMessage());
    }       
    }

  public void deleteFactory(int factoryID)
    {
    Statement stmt = null;
    String sql;
    
    try {
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
