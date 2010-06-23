	/*
	 * MekWars - Copyright (C) 2009 
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

import java.sql.Connection;

import server.campaign.CampaignMain;

public class JDBCConnectionHandler {
	
	public Connection getConnection() {
		return CampaignMain.cm.MySQL.getConnection();
	}
	
	public void returnConnection(Connection c) {
		CampaignMain.cm.MySQL.returnConnection(c);
	}
	
	public JDBCConnectionHandler() {
		
	}
	
}
