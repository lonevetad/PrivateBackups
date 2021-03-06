package games.generic.controlModel.player;

import games.generic.controlModel.GEventObserver;
import games.generic.controlModel.gObj.GModalityHolder;
import games.generic.controlModel.gObj.GameObjectGeneric;

/**
 * 27/03/2020 transformed into an interface since a "in game player" should
 * extend some "creature" class
 */
public interface PlayerGeneric extends GEventObserver, GameObjectGeneric, GModalityHolder {
	/**
	 * Perform a clean-up operation upon changing map, leaving the game, etc
	 */
//	public abstract void onLeavingMap();
//	public abstract void onEnteringInGame(GModality gm);

}