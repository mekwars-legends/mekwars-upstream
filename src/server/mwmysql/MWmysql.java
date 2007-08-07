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

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import server.MMServ;
import server.campaign.CampaignMain;

public class MWmysql{
  Connection con = null;
  Connection bbcon = null;
 
  public void close(){
    MMServ.mmlog.dbLog("Attempting to close MySQL Connection");
    try {
    	this.con.close();
//    	this.bbcon.close();
    } catch (SQLException e) {
    	MMServ.mmlog.dbLog("SQL Exception: " + e.getMessage());
    	MMServ.mmlog.errLog("SQL Exception:");
    	MMServ.mmlog.errLog(e);
    }
  } 

  public void backupDB() {
	  String fs = System.getProperty("file.separator");
	  StringBuffer cl = new StringBuffer();
	  cl.append("mysqldump -u ");
	  cl.append(CampaignMain.cm.getServer().getConfigParam("MYSQLUSER"));
	  cl.append(" -p");
	  cl.append(CampaignMain.cm.getServer().getConfigParam("MYSQLPASS"));
	  cl.append(" ");
	  cl.append(CampaignMain.cm.getServer().getConfigParam("MYSQLDB"));
	  cl.append(" > ." + fs + "campaign" + fs + "backup" + fs + "DB_Backup.");
	  cl.append(System.currentTimeMillis());
	  cl.append(".sql");
	  
	  Runtime runtime = Runtime.getRuntime();
	  String[] call = {cl.toString()};
	  try {
		  runtime.exec(call);		  
	  } catch (IOException ex){
		  MMServ.mmlog.dbLog("Error in backupDB: " + ex.toString());
	  }
  }

  public MWmysql(){
    String url = "jdbc:mysql://" + CampaignMain.cm.getServer().getConfigParam("MYSQLHOST") + "/" + CampaignMain.cm.getServer().getConfigParam("MYSQLDB") + "?user=" + CampaignMain.cm.getServer().getConfigParam("MYSQLUSER") + "&password=" + CampaignMain.cm.getServer().getConfigParam("MYSQLPASS");
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
