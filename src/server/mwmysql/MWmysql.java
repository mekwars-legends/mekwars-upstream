/*
 * MekWars - Copyright (C) 2007  
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

/**
 * @author Spork aka BillyPinHead
 */
package server.mwmysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import server.MMServ;
import server.campaign.CampaignMain;

public class MWmysql{
  Connection con = null;
 
  public void close(){
    MMServ.mmlog.dbLog("Attempting to close MySQL Connection");
    try {
    	this.con.close();
    } catch (SQLException e) {
    	MMServ.mmlog.dbLog("SQL Exception: " + e.getMessage());
    	MMServ.mmlog.errLog("SQL Exception:");
    	MMServ.mmlog.errLog(e);
    }
  } 

  public MWmysql(){
    String url = "jdbc:mysql://127.0.0.1/" + CampaignMain.cm.getServer().getConfigParam("MYSQLDB") + "?user=" + CampaignMain.cm.getServer().getConfigParam("MYSQLUSER") + "&password=" + CampaignMain.cm.getServer().getConfigParam("MYSQLPASS");
    MMServ.mmlog.dbLog("Attempting MySQL Connection");
    
    try{
      Class.forName("com.mysql.jdbc.Driver");
    }
    catch(ClassNotFoundException e){
      MMServ.mmlog.dbLog("ClassNotFoundException: " + e.getMessage());
    }
    try{
    	con=DriverManager.getConnection(url);
      	if(con != null)
    	  MMServ.mmlog.dbLog("Connection established");
    }
    catch(SQLException ex){
    	MMServ.mmlog.dbLog("SQLException: " + ex.getMessage());
    }
  }
}
