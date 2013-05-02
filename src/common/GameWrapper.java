package common;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import megamek.common.Entity;
import megamek.common.IGame;
import megamek.common.Player;


public class GameWrapper implements GameInterface {
	
	private final IGame game;
	
	public GameWrapper(IGame game) {
		this.game = game;
	}

	public Enumeration<Entity> getDevastatedEntities() {
		return game.getDevastatedEntities();
	}

	public Enumeration<Entity> getGraveyardEntities() {
		return game.getGraveyardEntities();
	}

	public Enumeration<Entity> getEntities() {
		return game.getEntities();
	}

	public Enumeration<Entity> getRetreatedEntities() {
		return game.getRetreatedEntities();
	}

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

	public boolean hasWinner() {
		return game.getVictoryTeam() != Player.TEAM_NONE;
	}


}
