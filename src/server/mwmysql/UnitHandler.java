package server.mwmysql;

import java.sql.Connection;

import server.campaign.SUnit;

public class UnitHandler {
	
	Connection con;
	
	public void saveUnit(SUnit u) {
		
	}
	
	public UnitHandler(Connection c) {
		this.con = c;
	}

}
