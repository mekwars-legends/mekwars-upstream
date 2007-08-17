package server.mwmysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import server.MMServ;
import server.campaign.CampaignMain;

/*
 * Currently, there are at least 3 versions of phpBB in use.  For now,
 * I'm going to try to abstract this, so we can work semi-version-agnostic
 * with them.  If it proves to be a major pain in the ass, I might have
 * to pull support for phpBB integration.
 * 
 * For now, we're going to work with phpBB v2.0.16, 2.0.21, and 2.0.22
 * I'm not going to touch 3.0, as it's just in RC stage.
 * 
 * Either that, or I'm going to do *just* 2.0.22, since it's the latest
 * stable release, and force SOs to upgrade their phpBB installations
 * 
 */

public class PhpBBConnector {
	  Connection con = null;

	  private String userGroupTable = "";
	  private String groupsTable = "";
	  private String tablePrefix = CampaignMain.cm.getServer().getConfigParam("PHPBB_TABLE_PREFIX");
	  private String bbVersion = "0";
	  private String userTable = "";
	  private int bbMajorVersion = Integer.parseInt(CampaignMain.cm.getServer().getConfigParam("PHPBB_MAJOR_VERSION"));
	  private String bbUrl = "";
	  
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

	  private boolean userExistsInForum(String name) {
		  PreparedStatement ps = null;
		  ResultSet rs = null;
		  boolean exists = false;
		  try {
			ps = con.prepareStatement("SELECT COUNT(*) as numusers from " + userTable + " WHERE username = ?");
			ps.setString(1, name);
			rs = ps.executeQuery();
			if(rs.next()) {
				if(rs.getInt("numusers") > 0)
					exists = true;
				else
					exists = false;
			} else {
				exists = false;
			}
			rs.close();
			ps.close();
			return exists;
		  } catch (SQLException e) {
			  MMServ.mmlog.dbLog("SQL Error in PhpBBConnector.userExistsInForum: " + e.getMessage());
			  return false;
		  }
	  }
	  
	  public void addToForum(String name, String pass) {
		  if(userExistsInForum(name))
			  return;
		  
		  PreparedStatement ps = null;
		  StringBuffer sql = new StringBuffer();
		  ResultSet rs = null;
		  
		  try {
			  switch(bbMajorVersion) {
			  case 2:
				  if(bbVersion.equalsIgnoreCase(".0.22")) {
					sql.append("INSERT into " + userTable + " set ");
				  	sql.append("user_active = 1, ");
				  	sql.append("username = ?, ");
				  	sql.append("user_password = MD5(?), ");
				  	sql.append("user_session_time = 0, ");
				  	sql.append("user_session_page = 0, ");
				  	sql.append("user_lastvisit = 0, ");
				  	sql.append("user_regdate = ?, ");
				  	sql.append("user_id = ?");
		  
				  	ps = con.prepareStatement(sql.toString());
				  	ps.setString(1, name);
				  	ps.setString(2, pass);
				  	ps.setLong(3, (int)(System.currentTimeMillis()/1000));
			  
				  	int userID = 0;
				  	// grab an unused id.
				  	Statement stmt = con.createStatement();
				  	rs = stmt.executeQuery("SELECT MAX(user_id) as total FROM " + userTable);
				  	if(rs.next()) {
					  	userID = rs.getInt("total") + 1;
				  	}
				  	if (userID == 0) {
					  	// It's no good
					  	break;
				  	}
				  	ps.setInt(4, userID);
				  	ps.executeUpdate();
				  	stmt.close();
				  	// Now add to the groups
				  	ps = con.prepareStatement("INSERT INTO " + groupsTable + " (group_name, group_description, group_single_user, group_moderator) VALUES ('', 'Personal User', 1, 0)", PreparedStatement.RETURN_GENERATED_KEYS);
				  	ps.executeUpdate();
				  	rs = ps.getGeneratedKeys();
				  	rs.next();
				  	int groupID = rs.getInt(1);
			  
				  	ps = con.prepareStatement("INSERT INTO " + userGroupTable + " (user_id, group_id, user_pending) VALUES (?, ?, 0)");
				  	ps.setInt(1, userID);
				  	ps.setInt(2, groupID);
				  	ps.executeUpdate();
				  	rs.close();
				  	ps.close();
				  	CampaignMain.cm.toUser("Your forum account has been activated.  You can log in to the forum at " + bbUrl + ".", name);
				  }
				  break;
				  
				default:
					break;
			  	}


		  } catch(SQLException e) {
			  MMServ.mmlog.dbLog("SQL Error in PhpBBConnector.addToForum: " + e.getMessage());
		  }
	  }
	  
	  private String getBBConfigVar(String varName) {
		  try {
			  PreparedStatement ps = null;
			  ResultSet rs = null;
			  String sql = "SELECT config_value from " + tablePrefix + "config WHERE config_name = ?";
			  ps = con.prepareStatement(sql);
			  ps.setString(1, varName);
			  rs = ps.executeQuery();
			  if(!rs.next())
				  return null;
			  String ret = rs.getString("config_value");
			  rs.close();
			  ps.close();
			  return ret;			  
		  } catch(SQLException e) {
			  MMServ.mmlog.dbLog("SQL Error in PhpBBConnector.getBBConfigVar: " + e.getMessage());
			  return null;
		  }
	  }
	  
	  public void init() {
		  // Get all the phpBB defaults
		  //this.bbUrl = getBBConfigVar("");
		  this.bbVersion = getBBConfigVar("version");
		  this.bbUrl = CampaignMain.cm.getServer().getConfigParam("PHPBB_URL");
		  switch(bbMajorVersion) {
		  	case 2:
		  		if(bbVersion.equalsIgnoreCase(".0.22")) {
					  this.groupsTable = tablePrefix + "groups";
					  this.userGroupTable = tablePrefix + "user_group";
					  this.userTable = tablePrefix + "users";
					  MMServ.mmlog.dbLog("Valid phpBB Version");
					  
				  } else {
					  MMServ.mmlog.dbLog("Unsupported phpBB Version");
					  CampaignMain.cm.turnOffBBSynch();
				  }
		  		break;
		  	default:
		  		MMServ.mmlog.dbLog("Unsupported phpBB Version");
		  		CampaignMain.cm.turnOffBBSynch();
		  		break;
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
