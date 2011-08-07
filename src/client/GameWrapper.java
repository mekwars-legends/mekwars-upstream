package client;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import megamek.common.Entity;
import megamek.common.IGame;
import megamek.common.Player;

import common.GameInterface;

public class GameWrapper implements GameInterface {
	
	private final IGame game;
	
	public GameWrapper(IGame game) {
		this.game = game;
	}

	@Override
	public Enumeration<Entity> getDevastatedEntities() {
		return game.getDevastatedEntities();
	}

	@Override
	public Enumeration<Entity> getGraveyardEntities() {
		return game.getGraveyardEntities();
	}

	@Override
	public Enumeration<Entity> getEntities() {
		return game.getEntities();
	}

	@Override
	public Enumeration<Entity> getRetreatedEntities() {
		return game.getRetreatedEntities();
	}

	@Override
	public List<String> getWinners() {
		ArrayList<String> result = new ArrayList<String>();
		Enumeration<Player> en = game.getPlayers();
		while (en.hasMoreElements()){
			Player player = en.nextElement();
			if (player.getTeam() == game.getVictoryTeam()){
				result.add(player.getName().trim());
			}
		}
		return result;
	}

	@Override
	public boolean hasWinner() {
		return game.getVictoryTeam() != Player.TEAM_NONE;
	}


}
