package common;

import java.util.Enumeration;
import java.util.List;

import megamek.common.Entity;

public interface GameInterface {
	List<String> getWinners();
	
	boolean hasWinner();

	Enumeration<Entity> getDevastatedEntities();

	Enumeration<Entity> getGraveyardEntities();

	Enumeration<Entity> getEntities();

	Enumeration<Entity> getRetreatedEntities();

}
