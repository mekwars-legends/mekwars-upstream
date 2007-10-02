/*
 * MekWars - Copyright (C) 2007  
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
package server.campaign.commands;

import java.util.StringTokenizer;

import server.MWServ;

public class SendClientDataCommand implements Command {
	
	public int getExecutionLevel(){return 0;}
	public void setExecutionLevel(int i) {}
	
	public void process(StringTokenizer command,String Username) {
		
		try{

			StringBuilder userData = new StringBuilder("USERDATA:");
			
			userData.append(Username);
			userData.append(";");
			
			while ( command.hasMoreElements() ){
				userData.append(command.nextToken());
				userData.append(";");
			}
			
			MWServ.mwlog.ipLog(userData.toString());
		}catch (Exception ex){
			//do nothing
		}
	}//end process
	
}//end AttackFromReserveCommand