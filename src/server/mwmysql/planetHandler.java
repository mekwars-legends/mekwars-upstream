package server.mwmysql;

import java.sql.*;

public class planetHandler {
  Connection con = null;


  // CONSTRUCTOR
  public planetHandler(Connection c)
    {
    this.con = c;
    }
}
