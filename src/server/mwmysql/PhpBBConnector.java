package server.mwmysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import server.MWServ;
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
	    MWServ.mwlog.dbLog("Attempting to close MySQL phpBB Connection");
	    try {
	    	this.con.close();
	    } catch (SQLException e) {
	    	MWServ.mwlog.dbLog("SQL Exception in PhpBBConnector.close: " + e.getMessage());
	    	MWServ.mwlog.errLog("SQL Exception in PhpBBConnector.close: ");
	    	MWServ.mwlog.errLog(e);
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
			  MWServ.mwlog.dbLog("SQL Error in PhpBBConnector.userExistsInForum: " + e.getMessage());
			  return false;
		  }  
	  }
	  
	  
	  
	  private boolean userExistsInForum(String name, String email) {
		  PreparedStatement ps = null;
		  ResultSet rs = null;
		  boolean exists = false;
		  try {
			ps = con.prepareStatement("SELECT COUNT(*) as numusers from " + userTable + " WHERE username = ? AND user_email = ?");
			ps.setString(1, name);
			ps.setString(2, email);
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
			  MWServ.mwlog.dbLog("SQL Error in PhpBBConnector.userExistsInForum: " + e.getMessage());
			  return false;
		  }
	  }
	  
	  public void deleteForumAccount(int forumID) {
		  PreparedStatement ps = null;
		  
		  try {
			  // First, the user groups
			  ps = con.prepareStatement("DELETE from " + userGroupTable + " WHERE user_id = " + forumID);
			  ps.executeUpdate();
			  ps.executeUpdate("DELETE from " + userTable + " WHERE user_id = " + forumID);
			  
			  ps.close();
		  } catch (SQLException e) {
			  MWServ.mwlog.dbLog("SQLException in PhpBBConnector.deleteForumAccount: " + e.getMessage());
		  }
	  }
	  
	  public void addToHouseForum(int userID, int houseForumID) {
		  if(userID < 1) {
			  MWServ.mwlog.dbLog("User ID < 1 in addToHouseForum, exiting");
			  return;
		  }
		  try {
			  PreparedStatement ps = con.prepareStatement("SELECT count(*) as num from " + userGroupTable + " WHERE group_id = " + houseForumID + " AND user_id = " + userID);
			  ResultSet rs = ps.executeQuery();
			  if(rs.next())
				  if(rs.getInt("num") > 0)
					  removeFromHouseForum(userID, houseForumID);
			  rs.close();
			  ps.close();
			  ps = con.prepareStatement("INSERT into " + userGroupTable + " set group_id = "+ houseForumID + ", user_id = " + userID + ", user_pending=0");
			  ps.executeUpdate();
			  ps.close();
			} catch (SQLException e) {
			  MWServ.mwlog.dbLog("SQLException in PhpBBConnector.addToHouseForum: " + e.getMessage());
		  }
	  }
	  
	  public void removeFromHouseForum(int userID, int forumID) {
		  if(userID < 1) {
			  MWServ.mwlog.dbLog("User ID < 1 in removeFromHouseForum, exiting");
			  return;
		  }
		  try {
			  PreparedStatement ps = con.prepareStatement("DELETE from " + userGroupTable + " WHERE group_id = " + forumID + " AND user_id = " + userID);
			  ps.executeUpdate();
			  ps.close();
		  } catch (SQLException e) {
			  MWServ.mwlog.dbLog("SQLException in PhpBBConnector.removeFromHouseForum: " + e.getMessage());
		  }
	  }
	  
	  public int getHouseForumID(String houseForumName) {
		  int forumID=0;
		  try {
			  PreparedStatement ps = con.prepareStatement("SELECT group_id from " + groupsTable + " WHERE group_name = ?");
			  ps.setString(1, houseForumName);
			  ResultSet rs = ps.executeQuery();
			  if(rs.next()) {
				  forumID = rs.getInt("group_id");
			  }
			  rs.close();
			  ps.close();
			  MWServ.mwlog.dbLog("Searching for forumID for house " + houseForumName + ": " + forumID);
		  } catch (SQLException e) {
			  MWServ.mwlog.dbLog("SQLException in PhpBBConnector.getHouseForumID: " + e.getMessage());
		  }
		  return forumID;
	  }
	  
	  public int getUserForumID(String userName, String userEmail) {
		  int userID = 0;
		  try {
			  PreparedStatement ps = con.prepareStatement("SELECT user_id from " + userTable + " WHERE (username = ? AND user_email = ?)");
			  ps.setString(1, userName);
			  ps.setString(2, userEmail);
			  ResultSet rs = ps.executeQuery();
			  if(rs.next()) {
				  userID = rs.getInt("user_id");
			  }
			  rs.close();
			  ps.close();
		  } catch (SQLException e) {
			  MWServ.mwlog.dbLog("SQLException in PhpBBConnector.getUserForumID: " + e.getMessage());
		  }
		  return userID;
	  }
	  
	  public void addToForum(String name, String pass, String email) {
		  if(userExistsInForum(name, email)) 
			  return;
		  // The don't exist, using that email.  The username might still be taken
		  if(userExistsInForum(name))
			  return;
		  
		  /*
		   * TODO: Need to return the phpBB ID somewhere in here.
		   */
		  
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
				  	sql.append("user_email = ?, ");
				  	sql.append("user_id = ?");
		  
				  	ps = con.prepareStatement(sql.toString());
				  	ps.setString(1, name);
				  	ps.setString(2, pass);
				  	ps.setLong(3, (int)(System.currentTimeMillis()/1000));
				  	ps.setString(4, email);
			  
				  	int userID = 0;
				  	// grab an unused id.
				  	Statement stmt = con.createStatement();
				  	rs = stmt.executeQuery("SELECT MAX(user_id) as total FROM " + userTable);
				  	// Note that while this is how phpBB does it, it is a potential race condition
				  	
				  	if(rs.next()) {
					  	userID = rs.getInt("total") + 1;
				  	}
				  	if (userID == 0) {
					  	// It's no good
					  	break;
				  	}
				  	ps.setInt(5, userID);
				  	ps.executeUpdate();
				  	stmt.close();
				  	// Now add to the groups
				  	ps.close();
				  	ps = con.prepareStatement("INSERT INTO " + groupsTable + " (group_name, group_description, group_single_user, group_moderator) VALUES ('', 'Personal User', 1, 0)", PreparedStatement.RETURN_GENERATED_KEYS);
				  	ps.executeUpdate();
				  	rs = ps.getGeneratedKeys();
				  	rs.next();
				  	int groupID = rs.getInt(1);
				  	ps.close();
				  	
				  	ps = con.prepareStatement("INSERT INTO " + userGroupTable + " (user_id, group_id, user_pending) VALUES (?, ?, 0)");
				  	ps.setInt(1, userID);
				  	ps.setInt(2, groupID);
				  	ps.executeUpdate();

				  	CampaignMain.cm.toUser("Your forum account has been activated.  You can log in to the forum at " + bbUrl + ".", name);
				  }
				  break;
				  
				default:
					break;
			  	}

			  	if(rs != null)
			  		rs.close();
			  	if(ps != null)
			  		ps.close();
		  } catch(SQLException e) {
			  MWServ.mwlog.dbLog("SQL Error in PhpBBConnector.addToForum: " + e.getMessage());
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
			  MWServ.mwlog.dbLog("SQL Error in PhpBBConnector.getBBConfigVar: " + e.getMessage());
			  return null;
		  }
	  }
	  
	  public void init() {
		  this.bbVersion = getBBConfigVar("version");
		  this.bbUrl = CampaignMain.cm.getServer().getConfigParam("PHPBB_URL");
		  switch(bbMajorVersion) {
		  	case 2:
		  		if(bbVersion.equalsIgnoreCase(".0.22")) {
					  this.groupsTable = tablePrefix + "groups";
					  this.userGroupTable = tablePrefix + "user_group";
					  this.userTable = tablePrefix + "users";
					  MWServ.mwlog.dbLog("Valid phpBB Version");
					  
				  } else {
					  MWServ.mwlog.dbLog("Unsupported phpBB Version");
					  CampaignMain.cm.turnOffBBSynch();
				  }
		  		break;
		  	default:
		  		MWServ.mwlog.dbLog("Unsupported phpBB Version");
		  		CampaignMain.cm.turnOffBBSynch();
		  		break;
		  }
		  
	  }
	  
	  public void sendEmailValidation(String emailAddress, String activationKey) {
		  Properties props = new Properties();
		  String smtphost=null;
		  if((smtphost=CampaignMain.cm.getServer().getConfigParam("MAILHOST")) == null) {
			  MWServ.mwlog.errLog("SMTPHOST not set in serverconfig");
			  CampaignMain.cm.doSendModMail("NOTE", "SMTPHOST not set in serverconfig.");
			  return;
		  }
		  props.put("mail.smtp.host", smtphost);
		  props.put("mail.from", "MekWars Server Admins<donotreply@mekwars.org>");
		  Session session = Session.getInstance(props, null);
		  
		  try {
			  MimeMessage msg = new MimeMessage(session);
			  msg.setFrom();
			  msg.setRecipients(Message.RecipientType.TO, emailAddress);
			  msg.setSubject("Test Email");
			  msg.setSentDate(new Date());
			  msg.setText("This is a test");
			  Transport.send(msg);
		  } catch (MessagingException e) {
			  MWServ.mwlog.errLog("Email send failed:");
			  MWServ.mwlog.errLog(e);
		  }
	  }
	  
	  public PhpBBConnector(){
	    String url = "jdbc:mysql://" + CampaignMain.cm.getServer().getConfigParam("PHPBB_HOST") + "/" + CampaignMain.cm.getServer().getConfigParam("PHPBB_DB") + "?user=" + CampaignMain.cm.getServer().getConfigParam("PHPBB_USER") + "&password=" + CampaignMain.cm.getServer().getConfigParam("PHPBB_PASS");
	    MWServ.mwlog.dbLog("Attempting phpBB Connection");
	    
	    try{
	      Class.forName("com.mysql.jdbc.Driver");
	    }
	    catch(ClassNotFoundException e){
	      MWServ.mwlog.dbLog("ClassNotFoundException: " + e.getMessage());
	    }
	    try{
	    	this.con=DriverManager.getConnection(url);
	      	if(con != null)
	    	  MWServ.mwlog.dbLog("phpBB Connection established");
	    }
	    catch(SQLException ex){
	    	MWServ.mwlog.dbLog("SQLException in PhpBBConnector: " + ex.getMessage());
	    }
	  }
}
