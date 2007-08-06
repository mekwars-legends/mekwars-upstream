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
    	this.bbcon.close();
    } catch (SQLException e) {
    	MMServ.mmlog.dbLog("SQL Exception: " + e.getMessage());
    	MMServ.mmlog.errLog("SQL Exception:");
    	MMServ.mmlog.errLog(e);
    }
  } 

  public void backupDB() {
	  Runtime runtime = Runtime.getRuntime();
	  String[] call = {"mysqldump", "-u " + CampaignMain.cm.getServer().getConfigParam("MYSQLUSER"), "-p" + CampaignMain.cm.getServer().getConfigParam("MYSQLPASS"), CampaignMain.cm.getServer().getConfigParam("MYSQLDB"), " > ./campaign/backup/DB_Backup." + System.currentTimeMillis() + ".sql"};
	  
	  try {
		  runtime.exec(call);		  
	  } catch (IOException ex){
		  MMServ.mmlog.dbLog("Error in backupDB: " + ex.toString());
	  }
  }
  
  public void createPHPBBConnection() {
  	String url = "jdbc:mysql://" + CampaignMain.cm.getServer().getConfigParam("PHPBB_HOST") + "/" + CampaignMain.cm.getServer().getConfigParam("PHPBB_DB") + "?user=" + CampaignMain.cm.getServer().getConfigParam("PHPBB_USER") + "&password=" + CampaignMain.cm.getServer().getConfigParam("PHPBB_PASS");
	MMServ.mmlog.dbLog("Connecting to PHPBB Database");
	try {
		Class.forName("com.mysql.jdbc.Driver");
	} catch(ClassNotFoundException e) {
		MMServ.mmlog.dbLog("ClassNotFoundException: " + e.getMessage());
	}
	try {
		bbcon = DriverManager.getConnection(url);
		if(bbcon != null)
			MMServ.mmlog.dbLog("PHPBB connection established");
	} catch(SQLException ex) {
		MMServ.mmlog.dbLog("SQLException: " + ex.getMessage());
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
