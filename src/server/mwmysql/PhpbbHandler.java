package server.mwmysql;

import java.sql.Connection;

public class PhpbbHandler {
	
	Connection bbcon;
	
	public PhpbbHandler(Connection c) {
		this.bbcon = c;
	}

}
