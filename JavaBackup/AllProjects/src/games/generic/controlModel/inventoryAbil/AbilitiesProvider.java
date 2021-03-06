package games.generic.controlModel.inventoryAbil;

import games.generic.controlModel.GModality;
import games.generic.controlModel.subimpl.GObjProviderRarityPartitioning;

public class AbilitiesProvider extends GObjProviderRarityPartitioning<AbilityGeneric> {
	public static final String NAME = "AbilP";

	public AbilitiesProvider() {
		super();
	}

	/** Should be preferred over {@link #getObjIdentifiedByID(Integer)}. */
	public AbilityGeneric getAbilityByName(GModality gm, String name) {
		if (name == null)
			return null;
//		return getObjByName(name).newInstance(gm);
		return getNewObjByName(gm, name);
	}

	public int getAbilitiesCount() {
		return getObjectsFactoriesCount();
	}
}