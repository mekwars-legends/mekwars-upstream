package server.mwmysql;

//import java.sql.*;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import server.campaign.SUnitFactory;
import server.MMServ;
import common.Planet;
import server.campaign.SPlanet;

public class factoryHandler {
  Connection con = null;

  public void loadFactories(SPlanet planet)
    {
    StringBuffer sql = new StringBuffer();
    Statement stmt = null;
    ResultSet rs = null;

    try {
    sql.append("SELECT * from factories WHERE FactoryPlanet = '");
    sql.append(planet.getName());
    sql.append("'");

    stmt = con.createStatement();
    rs = stmt.executeQuery(sql.toString());
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
      factory.setLock(rs.getBoolean("FactoryisLocked"));
      factory.setPlanet(planet);
      planet.getUnitFactories().add(factory); 
      }
    stmt.close();
    rs.close();
    } catch (SQLException e) {
      MMServ.mmlog.dbLog("SQL Error in factoryHandler.java: " + e.getMessage());
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
      MMServ.mmlog.dbLog("SQL ERROR in factoryHandler.java: " + e.getMessage());
      }
    }

  public void deletePlanetFactories(String planetName)
    {
    Statement stmt = null;
    StringBuffer sql = new StringBuffer();

    try {
      stmt = con.createStatement();
      sql.append("DELETE from factories where FactoryPlanet='");
      sql.append(planetName);
      sql.append("'");
      stmt.executeUpdate(sql.toString());
      stmt.close();
      }
      catch (SQLException e) {
        MMServ.mmlog.dbLog("SQL ERROR in factoryHandler.java: " + e.getMessage());
      }    
    }

  public void saveFactory(SUnitFactory factory)
    {
    int fid=0, ud;
    Statement stmt = null;
    ResultSet rs = null;
    StringBuffer sql = new StringBuffer();
    Planet planet = factory.getPlanet();
     
    try {
    if(con.isClosed())
	MMServ.mmlog.dbLog("Error: con closed"); 
    stmt = con.createStatement();
    sql.setLength(0); 
    sql.append("SELECT FactoryID from factories WHERE FactoryID = '");
	sql.append(factory.getID());
	sql.append("'"); 
    rs = stmt.executeQuery(sql.toString());
    if(!rs.next())
      {
      // This doesn't exist, so INSERT it
	sql.setLength(0);
      sql.append("INSERT into factories set ");
	sql.append("FactoryName = '");
	sql.append(factory.getName());
	sql.append("', ");
	sql.append("FactorySize = '");
	sql.append(factory.getSize());
	sql.append("', ");
	sql.append("FactoryFounder = '");
	sql.append(factory.getFounder());
	sql.append("', ");
	sql.append("FactoryTicks = '");
	sql.append(factory.getTicksUntilRefresh());
	sql.append("', ");
	sql.append("FactoryRefreshSpeed = '");
	sql.append(factory.getRefreshSpeed());
	sql.append("', ");
	sql.append("FactoryType = '");
	sql.append(factory.getType());
	sql.append("', ");
	sql.append("FactoryPlanet = '");
	sql.append(planet.getName());
	sql.append("', ");
	sql.append("FactoryisLocked = '");
	sql.append(factory.isLocked());
	sql.append("'");
	ud = stmt.executeUpdate(sql.toString(), Statement.RETURN_GENERATED_KEYS);
	rs = stmt.getGeneratedKeys();
	if (rs.next())
	  {
	  fid = rs.getInt(1);
	  factory.setID(fid);
	  }
      }
    else
      {
      // It already exists, so UPDATE it
      fid = rs.getInt("FactoryID");
	sql.setLength(0);
      sql.append("UPDATE factories set ");
        sql.append("FactoryName = '");
	sql.append(factory.getName());
	sql.append("', ");
        sql.append("FactorySize = '");
	sql.append(factory.getSize());
	sql.append("', ");
	sql.append("FactoryPlanet = '");
	sql.append(planet.getName());
	sql.append("', ");
        sql.append("FactoryFounder = '");
	sql.append(factory.getFounder());
	sql.append("', ");
        sql.append("FactoryTicks = '");
	sql.append(factory.getTicksUntilRefresh());
	sql.append("', ");
        sql.append("FactoryRefreshSpeed = '");
	sql.append(factory.getRefreshSpeed());
	sql.append("', ");
        sql.append("FactoryType = '");
	sql.append(factory.getType());
	sql.append("', ");
        sql.append("FactoryisLocked = '");
	sql.append(factory.isLocked());
	sql.append("' ");
	sql.append("WHERE FactoryID = ");
	sql.append(fid);

      ud = stmt.executeUpdate(sql.toString());
      }
        rs.close();
        stmt.close();
    }
    catch (SQLException e)
      {
      MMServ.mmlog.dbLog("SQL ERROR in factoryHandler.java: " + e.getMessage());
      }
    }

  // CONSTRUCTOR
  public factoryHandler(Connection c)
    {
    this.con = c;
    }
}
