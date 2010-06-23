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
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import common.CampaignData;

import server.mwmysql.ObjectPool;

public class ConnectionPool extends ObjectPool {
	
	private String url;
	private String usr;
	private String pwd;

	Object create() {
		try {
			return (DriverManager.getConnection(url, usr, pwd));
		} catch (SQLException e) {
			CampaignData.mwlog.dbLog(e);
			return (null);
		}
	}

	@Override
	void expire(Object o) {
		try {
			((Connection)o).close();
		} catch (SQLException e) {
			CampaignData.mwlog.dbLog(e);
		}
	}

	@Override
	boolean validate(Object o) {
		try {
			return( ! ((Connection)o).isClosed());
		} catch (SQLException e) {
			CampaignData.mwlog.dbLog(e);
			return (false);
		}
	}

	public Connection borrowConnection() {
		return( ( Connection ) super.checkOut() );
	}
		
	public void returnConnection( Connection c) {
		super.checkIn(c);
	}
	
	public ConnectionPool(String driver, String url, String usr, String pwd) {
		try {
			Class.forName( driver ).newInstance();
			Connection con = DriverManager.getConnection(url, usr, pwd);
			if (con != null) {
				CampaignData.mwlog.dbLog("Connection established");
				Statement s = con.createStatement();
				s.executeUpdate("SET NAMES 'utf8'");
				s.close();
				con.close();
			}
		} catch (Exception e) {
			CampaignData.mwlog.dbLog(e);
		} 
		this.url = url;
		this.pwd = pwd;
		this.usr = usr;
	}
}
