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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
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

import common.CampaignData;
import server.campaign.CampaignMain;

/*
 * Currently, there are at least 3 versions of phpBB in use. For now, I'm going
 * to try to abstract this, so we can work semi-version-agnostic with them. If
 * it proves to be a major pain in the ass, I might have to pull support for
 * phpBB integration.
 * 
 * For now, we're going to work with phpBB v2.0.16, 2.0.21, and 2.0.22 I'm not
 * going to touch 3.0, as it's just in RC stage.
 * 
 * Either that, or I'm going to do *just* 2.0.22, since it's the latest stable
 * release, and force SOs to upgrade their phpBB installations
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

    public void close() {
        CampaignData.mwlog.dbLog("Attempting to close MySQL phpBB Connection");
        try {
            this.con.close();
        } catch (SQLException e) {
            CampaignData.mwlog.dbLog("SQL Exception in PhpBBConnector.close: " + e.getMessage());
            CampaignData.mwlog.errLog("SQL Exception in PhpBBConnector.close: ");
            CampaignData.mwlog.errLog(e);
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
            if (rs.next()) {
                if (rs.getInt("numusers") > 0)
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
            CampaignData.mwlog.dbLog("SQL Error in PhpBBConnector.userExistsInForum: " + e.getMessage());
            CampaignData.mwlog.dbLog(e);
            return false;
        }
    }

    private boolean emailExistsInForum(String email) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean exists = false;
        try {
            ps = con.prepareStatement("SELECT COUNT(*) as numusers from " + userTable + " WHERE user_email = ?");
            ps.setString(1, email);
            rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getInt("numusers") > 0)
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
            CampaignData.mwlog.dbLog("SQLException in PhpBBConnector.emailExistsInForum: " + e.getMessage());
            CampaignData.mwlog.dbLog(e);
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
            if (rs.next()) {
                if (rs.getInt("numusers") > 0)
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
            CampaignData.mwlog.dbLog("SQL Error in PhpBBConnector.userExistsInForum: " + e.getMessage());
            CampaignData.mwlog.dbLog(e);
            return false;
        }
    }

    private boolean userExistsInForum(String name, String email, String password) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean exists = false;
        try {
            ps = con.prepareStatement("SELECT COUNT(*) as numusers from " + userTable + " WHERE username = ? AND user_email = ? AND user_password=MD5(?)");
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);
            rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getInt("numusers") > 0)
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
            CampaignData.mwlog.dbLog("SQLException in PhpBBConnector.userExistsInForum(String, String, String): " + e.getMessage());
            CampaignData.mwlog.dbLog(e);
            try {
                if (rs != null)
                    rs.close();
                if (ps != null)
                    ps.close();
            } catch (SQLException ex) {
                CampaignData.mwlog.dbLog(ex);
            }
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
            CampaignData.mwlog.dbLog("SQLException in PhpBBConnector.deleteForumAccount: " + e.getMessage());
            CampaignData.mwlog.dbLog(e);
        }
    }

    public void addToHouseForum(int userID, int houseForumID) {
        if (userID < 1) {
            CampaignData.mwlog.dbLog("User ID < 1 in addToHouseForum, exiting");
            return;
        }
        try {
            PreparedStatement ps = con.prepareStatement("SELECT count(*) as num from " + userGroupTable + " WHERE group_id = " + houseForumID + " AND user_id = " + userID);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                if (rs.getInt("num") > 0)
                    removeFromHouseForum(userID, houseForumID);
            rs.close();
            ps.close();
            ps = con.prepareStatement("INSERT into " + userGroupTable + " set group_id = " + houseForumID + ", user_id = " + userID + ", user_pending=0");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            CampaignData.mwlog.dbLog("SQLException in PhpBBConnector.addToHouseForum: " + e.getMessage());
            CampaignData.mwlog.dbLog(e);
        }
    }

    public void removeFromHouseForum(int userID, int forumID) {
        if (userID < 1) {
            CampaignData.mwlog.dbLog("User ID < 1 in removeFromHouseForum, exiting");
            return;
        }
        try {
            PreparedStatement ps = con.prepareStatement("DELETE from " + userGroupTable + " WHERE group_id = " + forumID + " AND user_id = " + userID);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            CampaignData.mwlog.dbLog("SQLException in PhpBBConnector.removeFromHouseForum: " + e.getMessage());
            CampaignData.mwlog.dbLog(e);
        }
    }

    public int getHouseForumID(String houseForumName) {
        int forumID = 0;
        try {
            PreparedStatement ps = con.prepareStatement("SELECT group_id from " + groupsTable + " WHERE group_name = ?");
            ps.setString(1, houseForumName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                forumID = rs.getInt("group_id");
            }
            rs.close();
            ps.close();
            CampaignData.mwlog.dbLog("Searching for forumID for house " + houseForumName + ": " + forumID);
        } catch (SQLException e) {
            CampaignData.mwlog.dbLog("SQLException in PhpBBConnector.getHouseForumID: " + e.getMessage());
            CampaignData.mwlog.dbLog(e);
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
            if (rs.next()) {
                userID = rs.getInt("user_id");
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            CampaignData.mwlog.dbLog("SQLException in PhpBBConnector.getUserForumID: " + e.getMessage());
            CampaignData.mwlog.dbLog(e);
        }
        return userID;
    }

    public boolean addToForum(String name, String pass, String email) {
        /*
         * if(userExistsInForum(name, email)) return; // The don't exist, using
         * that email. The username might still be taken
         * if(userExistsInForum(name)) return; if(emailExistsInForum(email)) {
         * CampaignMain.cm.toUser("AM: That email address is already registered
         * to a different account.", name, true); return; }
         */
        boolean toReturn = false;

        if (userExistsInForum(name)) {
            // There's already a user with that name - figure out if it's the
            // same one
            if (userExistsInForum(name, email, pass)) {
                // Name, email, and passwords match. It should be good. Send the
                // authentication email and return true;
                addActivationKey(getUserForumID(name, email), name);

                toReturn = this.sendEmailValidation(getUserForumID(name, email), email, getActivationKey(getUserForumID(name, email)));
            } else if (userExistsInForum(name, email)) {
                // Most likely still ok, and if they don't get the email, they
                // won't be authenticating anyway
                addActivationKey(getUserForumID(name, email), name);
                toReturn = this.sendEmailValidation(getUserForumID(name, email), email, getActivationKey(getUserForumID(name, email)));
            } else {
                // Username is already registered in the forums, but to a
                // different email address
                // Probably not the same guy - this *could* cause issues if the
                // user changes email addresses, but
                // an admin can fix the entry in the phpBB database manually. I
                // don't see it happening too often.
                CampaignMain.cm.toUser("This name is already registered to a different email address.", name, true);
                return false;
            }
        } else if (emailExistsInForum(email)) {
            // This email is already in use
            CampaignMain.cm.toUser("This email address is already registered to another user.", name, true);
            return false;
        } else {

            PreparedStatement ps = null;
            StringBuffer sql = new StringBuffer();
            ResultSet rs = null;

            try {
                switch (bbMajorVersion) {
                case 2:
                    if (bbVersion.equalsIgnoreCase(".0.22")) {
                        sql.append("INSERT into " + userTable + " set ");
                        sql.append("user_active = 0, ");
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
                        ps.setLong(3, (int) (System.currentTimeMillis() / 1000));
                        ps.setString(4, email);

                        int userID = 0;
                        // grab an unused id.
                        Statement stmt = con.createStatement();
                        rs = stmt.executeQuery("SELECT MAX(user_id) as total FROM " + userTable);
                        // Note that while this is how phpBB does it, it is a
                        // potential race condition

                        if (rs.next()) {
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

                        addActivationKey(userID, name);
                        toReturn = sendEmailValidation(userID, email, getActivationKey(userID));
                    }
                    break;

                default:
                    break;
                }

                if (rs != null)
                    rs.close();
                if (ps != null)
                    ps.close();

            } catch (SQLException e) {
                CampaignData.mwlog.dbLog("SQL Error in PhpBBConnector.addToForum: " + e.getMessage());
                CampaignData.mwlog.dbLog(e);
            }
        }
        if (toReturn) {
            StringBuilder text = new StringBuilder();
            text.append("Your forum account has been created.  You can log in to the forum at " + bbUrl + ".<br />");
            text.append("You will be receiving an email with an activation code.  You can validate your email address by using the ValidateEmail command.");
            CampaignMain.cm.toUser(text.toString(), name, true);
        }
        return toReturn;
    }

    private String getBBConfigVar(String varName) {
        try {
            PreparedStatement ps = null;
            ResultSet rs = null;
            String sql = "SELECT config_value from " + tablePrefix + "config WHERE config_name = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, varName);
            rs = ps.executeQuery();
            if (!rs.next())
                return null;
            String ret = rs.getString("config_value");
            rs.close();
            ps.close();
            return ret;
        } catch (SQLException e) {
            CampaignData.mwlog.dbLog("SQL Error in PhpBBConnector.getBBConfigVar: " + e.getMessage());
            CampaignData.mwlog.dbLog(e);
            return null;
        }
    }

    public void init() {
        this.bbVersion = getBBConfigVar("version");
        this.bbUrl = CampaignMain.cm.getServer().getConfigParam("PHPBB_URL");
        switch (bbMajorVersion) {
        case 2:
            if (bbVersion.equalsIgnoreCase(".0.22")) {
                this.groupsTable = tablePrefix + "groups";
                this.userGroupTable = tablePrefix + "user_group";
                this.userTable = tablePrefix + "users";
                CampaignData.mwlog.dbLog("Valid phpBB Version");

            } else {
                CampaignData.mwlog.dbLog("Unsupported phpBB Version");
                CampaignMain.cm.turnOffBBSynch();
            }
            break;
        default:
            CampaignData.mwlog.dbLog("Unsupported phpBB Version");
            CampaignMain.cm.turnOffBBSynch();
            break;
        }

    }

    public boolean sendEmailValidation(int userID, String emailAddress, String activationKey) {

        // Set up the body of the email
        File file = new File("./data/activationemail.txt");
        if (!file.exists()) {
            CampaignData.mwlog.errLog("/data/activationemail.txt does not exist");
            return false;
        }
        String line = "";
        String subject = "";
        String mailFrom = "";
        StringBuilder body = new StringBuilder();
        try {
            FileInputStream fis = new FileInputStream(file);
            BufferedReader dis = new BufferedReader(new InputStreamReader(fis));
            while (dis.ready()) {
                line = dis.readLine();
                if (line.startsWith("[SUBJECT]")) {
                    subject = line;
                    subject = subject.replace("[SUBJECT]", "");
                } else if (line.startsWith("[MAILFROM]")) {
                    mailFrom = line;
                    mailFrom = mailFrom.replace("[MAILFROM]", "");
                } else {
                    // Add it to the body
                    body.append(line + "\n");
                }
            }
            dis.close();
            fis.close();
        } catch (FileNotFoundException fnfe) {
            CampaignData.mwlog.errLog("FileNotFoundException in PhpBBConnector.sendActivationEmail: " + fnfe.getMessage());
            return false;
        } catch (IOException ioe) {
            CampaignData.mwlog.errLog("IOException in PhpBBConnector.sendActivationEmail: " + ioe.getMessage());
            return false;
        }
        String bodyString = body.toString().replaceAll("%USERACTKEY%", activationKey);
        Properties props = new Properties();
        String smtphost = null;
        if ((smtphost = CampaignMain.cm.getServer().getConfigParam("MAILHOST")) == null) {
            CampaignData.mwlog.errLog("MAILHOST not set in serverconfig");
            CampaignMain.cm.doSendModMail("NOTE", "MAILHOST not set in serverconfig.");
            return false;
        }

        String protocol = "smtp";
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", Boolean.toString(Boolean.parseBoolean(CampaignMain.cm.getServer().getConfigParam("MAILPASSREQUIRED"))));
        props.put("mail.smtp.host", smtphost);
        props.put("mail.from", mailFrom);
        Session session = Session.getInstance(props, null);

        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom();
            msg.setRecipients(Message.RecipientType.TO, emailAddress);
            msg.setSubject(subject);
            msg.setSentDate(new Date());
            msg.setText(bodyString);
            if (Boolean.parseBoolean(props.get("mail.smtp.auth").toString())) {
                Transport trans = session.getTransport(protocol);
                trans.connect(CampaignMain.cm.getServer().getConfigParam("MAILUSER"), CampaignMain.cm.getServer().getConfigParam("MAILPASS"));
                trans.sendMessage(msg, msg.getAllRecipients());
            } else {
                Transport.send(msg);
            }
        } catch (MessagingException e) {
            CampaignData.mwlog.errLog("Email send failed:");
            CampaignData.mwlog.errLog(e);
            return false;
        }
        return true;
    }

    private String addActivationKey(int userID, String userName) {
        PreparedStatement ps = null;
        String toReturn = "";
        try {
            ps = con.prepareStatement("UPDATE " + userTable + " SET user_actkey = MD5(?) WHERE user_id = ?");
            ps.setString(1, userName + Long.toString(System.currentTimeMillis()));
            ps.setInt(2, userID);
            ps.executeUpdate();

            toReturn = getActivationKey(userID);

            ps.close();
        } catch (SQLException e) {
            CampaignData.mwlog.dbLog("SQLException in PhpBBConnector.addActivationKey: " + e.getMessage());
            CampaignData.mwlog.dbLog(e);
            try {
                if (ps != null)
                    ps.close();
            } catch (SQLException ex) {
                CampaignData.mwlog.dbLog(ex);
            }
        }
        return toReturn;

    }

    public String getActivationKey(int userID) {
        String toReturn = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement("SELECT user_actkey from " + userTable + " WHERE user_id = ?");
            ps.setInt(1, userID);
            rs = ps.executeQuery();
            if (rs.next())
                toReturn = rs.getString("user_actkey");
            rs.close();
            ps.close();
        } catch (SQLException e) {
            try {
                if (rs != null)
                    rs.close();
                if (ps != null)
                    ps.close();
            } catch (SQLException ex) {

            }
        }
        return toReturn;
    }

    public void validateUser(int forumID) {
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement("UPDATE " + userTable + " SET user_active = 1 WHERE user_id = " + forumID);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            CampaignData.mwlog.dbLog("SQLException in PhpBBConnector.validateUser: " + e.getMessage());
            CampaignData.mwlog.dbLog(e);
        }
    }

    public void changeForumName(String oldname, String newname) {
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement("UPDATE " + userTable + " SET username = ? WHERE username = ?");
            ps.setString(1, newname);
            ps.setString(2, oldname);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            CampaignData.mwlog.dbLog("SQLException in PhpBBConnector.changeForumName: " + e.getMessage());
            CampaignData.mwlog.dbLog(e);
        }
    }

    public PhpBBConnector() {
        String url = "jdbc:mysql://" + CampaignMain.cm.getServer().getConfigParam("PHPBB_HOST") + "/" + CampaignMain.cm.getServer().getConfigParam("PHPBB_DB") + "?user=" + CampaignMain.cm.getServer().getConfigParam("PHPBB_USER") + "&password=" + CampaignMain.cm.getServer().getConfigParam("PHPBB_PASS");
        CampaignData.mwlog.dbLog("Attempting phpBB Connection");

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            CampaignData.mwlog.dbLog("ClassNotFoundException: " + e.getMessage());
            CampaignData.mwlog.dbLog(e);
        }
        try {
            this.con = DriverManager.getConnection(url);
            if (con != null)
                CampaignData.mwlog.dbLog("phpBB Connection established");
        } catch (SQLException ex) {
            CampaignData.mwlog.dbLog("SQLException in PhpBBConnector: " + ex.getMessage());
            CampaignData.mwlog.dbLog(ex);
        }
    }
}
