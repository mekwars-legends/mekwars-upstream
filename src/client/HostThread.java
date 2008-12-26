/*
 * MekWars - Copyright (C) 2004 
 * 
 * Derived from MegaMekNET (http://www.sourceforge.net/projects/megameknet)
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

package client;

import java.util.Enumeration;
import java.util.StringTokenizer;

import megamek.common.Building;
import megamek.common.Entity;
import megamek.common.IGame;
import megamek.common.Mech;
import megamek.common.MechWarrior;
import megamek.common.Player;
import megamek.common.IGame.Phase;
import megamek.common.event.GameBoardChangeEvent;
import megamek.common.event.GameBoardNewEvent;
import megamek.common.event.GameEndEvent;
import megamek.common.event.GameEntityChangeEvent;
import megamek.common.event.GameEntityNewEvent;
import megamek.common.event.GameEntityNewOffboardEvent;
import megamek.common.event.GameEntityRemoveEvent;
import megamek.common.event.GameEvent;
import megamek.common.event.GameListener;
import megamek.common.event.GameMapQueryEvent;
import megamek.common.event.GameNewActionEvent;
import megamek.common.event.GamePhaseChangeEvent;
import megamek.common.event.GamePlayerChangeEvent;
import megamek.common.event.GamePlayerChatEvent;
import megamek.common.event.GamePlayerConnectedEvent;
import megamek.common.event.GamePlayerDisconnectedEvent;
import megamek.common.event.GameReportEvent;
import megamek.common.event.GameSettingsChangeEvent;
import megamek.common.event.GameTurnChangeEvent;
import megamek.server.Server;
import client.util.SerializeEntity;

import common.CampaignData;
import common.util.UnitUtils;

class HostThread extends Thread implements GameListener {

	// VARIABLES
	private MWClient mwclient;
	private Server myServer = null;
	private int turn = 0;

	private Phase currentPhase = IGame.Phase.PHASE_DEPLOYMENT;

	final int N = 0;
	final int NE = 1;
	final int SE = 2;
	final int S = 3;
	final int SW = 4;
	final int NW = 5;

	// CONSTRUCTOR
	public HostThread(Server mmServer, MWClient mwclient) {
		this.mwclient = mwclient;
		myServer = mmServer;
	}

	// METHODS
	public int getTurn() {
		return turn;
	}

	public Server getServer() {
		return myServer;
	}

	@Override
	public void run() {
		myServer.getGame().addGameListener(this);
	}

	/**
	 * redundant code since MM does not always send a discon event.
	 */
	public void gamePlayerStatusChange(GameEvent e) {
	}

	public void gameTurnChange(GameTurnChangeEvent e) {
		if (myServer != null) {
			if (getTurn() == 0) {
				mwclient.serverSend("SHS|" + mwclient.getUsername() + "|Running");
			} else if (myServer.getGame().getPhase() != currentPhase && myServer.getGame().getOptions().booleanOption("paranoid_autosave")) {
				sendServerGameUpdate();
				currentPhase = myServer.getGame().getPhase();
			}
			turn += 1;

		}
	}

	public void gamePhaseChange(GamePhaseChangeEvent e) {

		try {

			if (myServer.getGame().getPhase() == IGame.Phase.PHASE_VICTORY) {

				sendGameReport();
				CampaignData.mwlog.infoLog("GAME END");

			}// end victory

			/*
			 * Reporting phases show deaths - units that try to stand and blow
			 * their ammo, units that have ammo explode from head, etc. This is
			 * also an opportune time to correct isses with the gameRemoveEntity
			 * ISU's. Removals happen ASAP, even if the removal condition and
			 * final condition of the unit are not the same (ie - remove on
			 * Engine crits even when a CT core comes later in the round).
			 */
			else if (myServer.getGame().getPhase() == IGame.Phase.PHASE_END_REPORT) {
				sendServerGameUpdate();
			}

		}// end try
		catch (Exception ex) {
			CampaignData.mwlog.errLog("Error reporting game!");
			CampaignData.mwlog.errLog(ex);
		}
	}

	/*
	 * When an entity is removed from play, check the reason. If the unit is
	 * ejected, captured or devestated and the player is invovled in the game at
	 * hand, report the removal to the server. The server stores these reports
	 * in pilotTree and deathTree in order to auto-resolve games after a player
	 * disconnects. NOTE: This send thefirst possible removal condition, which
	 * means that a unit which is simultanously head killed and then CT cored
	 * will show as salvageable.
	 */
	public void gameEntityRemove(GameEntityRemoveEvent e) {// only send if the
		// player is
		// actually involved
		// in the game

		// get the entity
		megamek.common.Entity removedE = e.getEntity();
		if (removedE.getOwner().getName().startsWith("War Bot")) {
			return;
		}

		String toSend = SerializeEntity.serializeEntity(removedE, true, false, mwclient.isUsingAdvanceRepairs());
		mwclient.serverSend("IPU|" + toSend);
	}

	public void gamePlayerConnected(GamePlayerConnectedEvent e) {
	}

	public void gamePlayerDisconnected(GamePlayerDisconnectedEvent e) {
	}

	public void gamePlayerChange(GamePlayerChangeEvent e) {
	}

	public void gamePlayerChat(GamePlayerChatEvent e) {
	}

	public void gameReport(GameReportEvent e) {
	}

	public void gameEnd(GameEndEvent e) {
	}

	public void gameBoardNew(GameBoardNewEvent e) {
	}

	public void gameBoardChanged(GameBoardChangeEvent e) {
	}

	public void gameSettingsChange(GameSettingsChangeEvent e) {
	}

	public void gameMapQuery(GameMapQueryEvent e) {
	}

	public void gameEntityNew(GameEntityNewEvent e) {
	}

	public void gameEntityNewOffboard(GameEntityNewOffboardEvent e) {
	}

	public void gameEntityChange(GameEntityChangeEvent e) {
	}

	public void gameNewAction(GameNewActionEvent e) {
	}

	public int getBuildingsLeft() {
		Enumeration<Building> buildings = myServer.getGame().getBoard().getBuildings();
		int buildingCount = 0;
		while (buildings.hasMoreElements()) {
			buildings.nextElement();
			buildingCount++;
		}
		return buildingCount;
	}

	private void sendServerGameUpdate() {
		// Report the mech stat

		// Only send data for units currently on the board.
		// any units removed from play will have already sent thier final
		// update.
		Enumeration<Entity> en = myServer.getGame().getEntities();
		while (en.hasMoreElements()) {
			Entity ent = en.nextElement();
			if (ent.getOwner().getName().startsWith("War Bot") || (!(ent instanceof MechWarrior) && !UnitUtils.hasArmorDamage(ent) && !UnitUtils.hasISDamage(ent) && !UnitUtils.hasCriticalDamage(ent) && !UnitUtils.hasLowAmmo(ent) && !UnitUtils.hasEmptyAmmo(ent))) {
				continue;
			}
			if (ent instanceof Mech && ent.getInternal(Mech.LOC_CT) <= 0) {
				mwclient.serverSend("IPU|" + SerializeEntity.serializeEntity(ent, true, true, mwclient.isUsingAdvanceRepairs()));
			} else {
				mwclient.serverSend("IPU|" + SerializeEntity.serializeEntity(ent, true, false, mwclient.isUsingAdvanceRepairs()));
			}
		}
	}

	private void sendGameReport() {
		if (myServer == null) {
			return;
		}

		StringBuilder result = new StringBuilder();
		String name = "";
		// Parse the real playername from the Modified In game one..
		String winnerName = "";
		if (myServer.getGame().getVictoryTeam() != Player.TEAM_NONE) {

			int numberOfWinners = 0;
			// Multiple Winners
			Enumeration<Player> en = myServer.getGame().getPlayers();
			while (en.hasMoreElements()) {
				Player p = en.nextElement();
				if (p.getTeam() == myServer.getGame().getVictoryTeam()) {
					StringTokenizer st = new StringTokenizer(p.getName().trim(), "~");
					name = "";
					while (st.hasMoreElements()) {
						name = st.nextToken().trim();
					}
					// some of the players set themselves as a team of 1.
					// This keeps that from happening.
					if (numberOfWinners > 0) {
						winnerName += "*";
					}
					numberOfWinners++;

					winnerName += name;
				}
			}
			if (winnerName.endsWith("*")) {
				winnerName = winnerName.substring(0, winnerName.length() - 1);
			}
			winnerName += "#";
		}

		// Only one winner
		else {
			if (myServer.getGame().getVictoryPlayerId() == Player.PLAYER_NONE) {
				winnerName = "DRAW#";
			} else {
				winnerName = myServer.getGame().getPlayer(myServer.getGame().getVictoryPlayerId()).getName();
				StringTokenizer st = new StringTokenizer(winnerName, "~");
				name = "";
				while (st.hasMoreElements()) {
					name = st.nextToken().trim();
				}
				winnerName = name + "#";
			}
		}

		result.append(winnerName);

		// Report the mech stat
		Enumeration<Entity> en = myServer.getGame().getDevastatedEntities();
		while (en.hasMoreElements()) {
			Entity ent = en.nextElement();
			if (ent.getOwner().getName().startsWith("War Bot")) {
				continue;
			}
			result.append(SerializeEntity.serializeEntity(ent, true, false, mwclient.isUsingAdvanceRepairs()));
			result.append("#");
		}
		en = myServer.getGame().getGraveyardEntities();
		while (en.hasMoreElements()) {
			Entity ent = en.nextElement();
			if (ent.getOwner().getName().startsWith("War Bot")) {
				continue;
			}
			result.append(SerializeEntity.serializeEntity(ent, true, false, mwclient.isUsingAdvanceRepairs()));
			result.append("#");

		}
		en = myServer.getGame().getEntities();
		while (en.hasMoreElements()) {
			Entity ent = en.nextElement();
			if (ent.getOwner().getName().startsWith("War Bot")) {
				continue;
			}
			result.append(SerializeEntity.serializeEntity(ent, true, false, mwclient.isUsingAdvanceRepairs()));
			result.append("#");
		}
		en = myServer.getGame().getRetreatedEntities();
		while (en.hasMoreElements()) {
			Entity ent = en.nextElement();
			if (ent.getOwner().getName().startsWith("War Bot")) {
				continue;
			}
			result.append(SerializeEntity.serializeEntity(ent, true, false, mwclient.isUsingAdvanceRepairs()));
			result.append("#");
		}

		if (mwclient.getBuildingTemplate() != null) {
			result.append("BL*" + getBuildingsLeft());
		}
		CampaignData.mwlog.infoLog("CR|" + result);

		// send the autoreport
		mwclient.serverSend("CR|" + result.toString());

		// we may assume that a server which reports a game is no longer
		// "Running"
		mwclient.serverSend("SHS|" + mwclient.myUsername + "|Open");

		// myServer.resetGame();

		if (mwclient.isDedicated()) {
			mwclient.checkForRestart();
		}
	}
}