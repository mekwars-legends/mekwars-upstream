/*
 * MekWars - Copyright (C) 2004 
 * 
 * Derived from MegaMekNET (http://www.sourceforge.net/projects/megamek)
 * Original author Helge Richter (McWizard)
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

package dedicatedhost.cmd;

import java.util.Enumeration;
import java.util.StringTokenizer;

import common.CampaignData;

import dedicatedhost.MWDedHost;
import dedicatedhost.util.SerializeEntity;

import megamek.common.Building;
import megamek.common.Entity;
import megamek.common.Player;
import megamek.server.Server;

/**
 * @author Imi (immanuel.scholz@gmx.de)
 */
public class RGTS extends Command {
	
	private Server server = null;
	/**
	 * @param client
	 */
	public RGTS(MWDedHost mwclient) {
		super(mwclient);
	}
	
	/**
	 * @see server.cmd.Command#execute(java.lang.String)
	 */
	@Override
	public void execute(String input) {
		
		//CampaignData.mwlog.errLog("Inside RGTS");
		server = mwclient.getMyServer();
		StringBuilder result = new StringBuilder();
		String name = "";
		//Parse the real playername from the Modified In game one..
		String winnerName = "";
		if ( server.getGame().getVictoryTeam() != Player.TEAM_NONE) {
			
			int numberOfWinners = 0;
			//Multiple Winners
			Enumeration<Player> en = server.getGame().getPlayers();
			while (en.hasMoreElements()) {
				Player p = en.nextElement();
				if (p.getTeam() == server.getGame().getVictoryTeam()) {
					StringTokenizer st = new StringTokenizer(p.getName().trim(), "~");
					name = "";
					while (st.hasMoreElements()) {
						name = st.nextToken().trim();
					}
                    //some of the players set themselves as a team of 1. 
                    //This keeps that from happening.
                    if ( numberOfWinners > 0 )
                        winnerName += "*";
                    numberOfWinners++;
                    
                    winnerName += name;
                }
            }
            if ( winnerName.endsWith("*") )
                winnerName = winnerName.substring(0,winnerName.length()-1);
			winnerName += "#";
		} 
		
		//Only one winner
		else {
			if (server.getGame().getVictoryPlayerId() == Player.PLAYER_NONE) {
				winnerName = "DRAW#";
			} else {
				winnerName = server.getGame().getPlayer(server.getGame().getVictoryPlayerId()).getName();
				StringTokenizer st = new StringTokenizer(winnerName, "~");
				name = "";
				while (st.hasMoreElements())
					name = st.nextToken().trim();
				winnerName = name + "#";
			}
		}
		
		result.append(winnerName);
		
		//Report the mech stat
		Enumeration<Entity> en = server.getGame().getDevastatedEntities();
		while (en.hasMoreElements()) {
			Entity ent = en.nextElement();
			if ( ent.getOwner().getName().startsWith("War Bot"))
				continue;
			result.append(SerializeEntity.serializeEntity(ent, true, false,mwclient.isUsingAdvanceRepairs()) + "#");
		}
		en = server.getGame().getGraveyardEntities();
		while (en.hasMoreElements()) {
			Entity ent = en.nextElement();
			if ( ent.getOwner().getName().startsWith("War Bot"))
				continue;
			result.append(SerializeEntity.serializeEntity(ent, true, false,mwclient.isUsingAdvanceRepairs()) + "#");
			
		}
		en = server.getGame().getEntities();
		while (en.hasMoreElements()) {
			Entity ent = en.nextElement();
			if ( ent.getOwner().getName().startsWith("War Bot"))
				continue;
			result.append(SerializeEntity.serializeEntity(ent, true, false,mwclient.isUsingAdvanceRepairs()) + "#");
		}
		en = server.getGame().getRetreatedEntities();
		while (en.hasMoreElements()) {
			Entity ent = en.nextElement();
			if ( ent.getOwner().getName().startsWith("War Bot"))
				continue;
			result.append(SerializeEntity.serializeEntity(ent, true, false,mwclient.isUsingAdvanceRepairs()) + "#");
		}
		
		if ( mwclient.getBuildingTemplate()!= null )
			result.append("BL*"+this.getBuildingsLeft());
		CampaignData.mwlog.infoLog("CR|" + result);
		
		//send the autoreport
		mwclient.serverSend("CR|" + result.toString());
		
		//we may assume that a server which reports a game is no longer "Running"
		mwclient.serverSend("SHS|" + mwclient.myUsername + "|Open");
	}
	
	public int getBuildingsLeft(){
		Enumeration<Building> buildings = server.getGame().getBoard().getBuildings();
		int buildingCount = 0;
		while ( buildings.hasMoreElements() ){
			buildings.nextElement();
			buildingCount++;
		}
		return buildingCount;
	}
	
}
