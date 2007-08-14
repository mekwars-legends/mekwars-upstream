package server.mwmysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import server.MMServ;
import server.campaign.CampaignMain;

public class PhpBBConnector {
	  Connection con = null;
	 
	  public void close(){
	    MMServ.mmlog.dbLog("Attempting to close MySQL phpBB Connection");
	    try {
	    	this.con.close();
	    } catch (SQLException e) {
	    	MMServ.mmlog.dbLog("SQL Exception in PhpBBConnector.close: " + e.getMessage());
	    	MMServ.mmlog.errLog("SQL Exception in PhpBBConnector.close: ");
	    	MMServ.mmlog.errLog(e);
	    }
	  } 

	  public PhpBBConnector(){
	    String url = "jdbc:mysql://" + CampaignMain.cm.getServer().getConfigParam("PHPBB_HOST") + "/" + CampaignMain.cm.getServer().getConfigParam("PHPBB_DB") + "?user=" + CampaignMain.cm.getServer().getConfigParam("PHPBB_USER") + "&password=" + CampaignMain.cm.getServer().getConfigParam("PHPBB_PASS");
	    MMServ.mmlog.dbLog("Attempting phpBB Connection");
	    
	    try{
	      Class.forName("com.mysql.jdbc.Driver");
	    }
	    catch(ClassNotFoundException e){
	      MMServ.mmlog.dbLog("ClassNotFoundException: " + e.getMessage());
	    }
	    try{
	    	this.con=DriverManager.getConnection(url);
	      	if(con != null)
	    	  MMServ.mmlog.dbLog("phpBB Connection established");
	    }
	    catch(SQLException ex){
	    	MMServ.mmlog.dbLog("SQLException in PhpBBConnector: " + ex.getMessage());
	    }
	  }
}
