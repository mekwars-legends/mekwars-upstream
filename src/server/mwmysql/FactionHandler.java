package server.mwmysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



import server.MMServ;
import server.campaign.SHouse;

public class FactionHandler {
	Connection con;
	
	public void saveFaction (SHouse h) {
		PreparedStatement ps;
		StringBuffer sql = new StringBuffer();
		ResultSet rs;
		
		try {
			ps = con.prepareStatement("SELECT COUNT(*) as num from factions WHERE factionID = ?");
			ps.setInt(1, h.getId());
			rs = ps.executeQuery();
			rs.next();
			if(rs.getInt("num")== 0) {
				// Not in the database - INSERT it
				sql.setLength(0);
				sql.append("INSERT into factions set ");
				sql.append("");
			} else {
				// Already in the database - UPDATE it
				sql.setLength(0);
				
			}
		} catch (SQLException e) {
			MMServ.mmlog.dbLog("SQL Error in FactionHandler.saveFaction: " + e.getMessage());
		}

		
	}
	
	public FactionHandler (Connection c) {
		this.con = c;
	}

}
