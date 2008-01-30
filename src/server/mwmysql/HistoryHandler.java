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

	/*
	 * My personal thanks go out to McWizard for the inspiration for this.
	 * Unit histories were positively one of the *GREAT* additions to the
	 * MegaMekNET campaigns, and I can only hope that my efforts here can
	 * result in a similar outcome.  Much of this has been drawn directly
	 * or semi-directly from the MegaMekNET sourcecode on Sourceforge.
	 */
	
package server.mwmysql;

import java.sql.Connection;

public class HistoryHandler {
  Connection con = null;

  
  public void addUnitCreatedEntry(int unitID, String fluff) {
	  
  }
  
  // CONSTRUCTOR
  public HistoryHandler(Connection c)
    {
    this.con = c;
    }
}
