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

import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;

import server.campaign.CampaignMain;

import common.CampaignData;

public class MWmysql{
  Connection con = null;
  ConnectionPool cPool = null;
 
  public void close(){
    CampaignData.mwlog.dbLog("Attempting to close MySQL Connection");
    try {
    	//this.con.close();
    	cPool.shutdown();
    	CampaignData.mwlog.dbLog("MySQL Connection closed.");
    } catch (Exception e) {
    	CampaignData.mwlog.errLog(e);
    	CampaignData.mwlog.dbLog("Error shutting down MySQL Connection");
    }
  } 

  public Connection getCon() {
	  return cPool.borrowConnection();
  }
  
  public void returnCon(Connection c) {
	  cPool.returnConnection(c);
  }
  
  public void backupDB(long time) {
	  String fs = System.getProperty("file.separator");
	  Runtime runtime=Runtime.getRuntime();

	  String dateTimeFormat = "yyyy.MM.dd.HH.mm";
      SimpleDateFormat sDF = new SimpleDateFormat(dateTimeFormat);
      Date date = new Date(time);
      String dateTime = sDF.format(date);
      
	  try {
		  if(fs.equalsIgnoreCase("/"))
		  {
			  // It's Unix
			  String[] call={"./dump_db.sh", dateTime};
			  runtime.exec(call);
		  } else {
			  // It's Windows
			  String[] call={"dump_db.bat", dateTime};
			  runtime.exec(call);
		  }		  
	  } catch (IOException ex){
		  CampaignData.mwlog.dbLog("Error in backupDB: " + ex.toString());
          CampaignData.mwlog.dbLog(ex);
	  }
  }

  public MWmysql(){
//	String url = "jdbc:mysql://" + CampaignMain.cm.getServer().getConfigParam("MYSQLHOST") + "/" + CampaignMain.cm.getServer().getConfigParam("MYSQLDB") + "?user=" + CampaignMain.cm.getServer().getConfigParam("MYSQLUSER") + "&password=" + CampaignMain.cm.getServer().getConfigParam("MYSQLPASS") + "&useUnicode=true&characterEncoding=UTF-8";
//    CampaignData.mwlog.dbLog("Attempting MySQL Connection");
//    
//    try{
//      Class.forName("com.mysql.jdbc.Driver");
//    }
//    catch(ClassNotFoundException e){
//      CampaignData.mwlog.dbLog("ClassNotFoundException: " + e.getMessage());
//      CampaignData.mwlog.dbLog(e);
//    }
//    try{
//    	con=DriverManager.getConnection(url);
//      	if(con != null) {
//    	  CampaignData.mwlog.dbLog("Connection established");
//    	  Statement s = con.createStatement();
//    	  s.executeUpdate("SET NAMES 'utf8'");
//
//      	}
//    }
//    catch(SQLException ex){
//    	CampaignData.mwlog.dbLog("SQLException: " + ex.getMessage());
//        CampaignData.mwlog.dbLog(ex);
//    }
	  String url = "jdbc:mysql://" + CampaignMain.cm.getServer().getConfigParam("MYSQLHOST") + "/" + CampaignMain.cm.getServer().getConfigParam("MYSQLDB") + "?user=" + "&useUnicode=true&characterEncoding=UTF-8";  
	  cPool = new ConnectionPool("com.mysql.jdbc.Driver", url, CampaignMain.cm.getServer().getConfigParam("MYSQLUSER"), CampaignMain.cm.getServer().getConfigParam("MYSQLPASS"));  
  }
}
