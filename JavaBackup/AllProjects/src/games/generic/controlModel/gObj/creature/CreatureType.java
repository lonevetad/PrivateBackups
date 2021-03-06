package games.generic.controlModel.gObj.creature;

import games.generic.controlModel.ObjectNamed;
import games.generic.controlModel.misc.RarityHolder;

/**
 * Some creatures, like enemy creatures, could be grouped under sets, like:
 * "goblin", "animal", "undead", "angel", "human", etc.<br>
 * Those sets has, as it's clear, a name and could have associated a kind of
 * "spawn probability". This concept is identified by the interface
 * {@link RarityHolder}.
 */
public interface CreatureType extends ObjectNamed, RarityHolder {
	@Override
	public default int getRarityIndex() {
		return 0;
	}

	@Override
	public default RarityHolder setRarityIndex(int rarityIndex) {
		return this;
	}
}