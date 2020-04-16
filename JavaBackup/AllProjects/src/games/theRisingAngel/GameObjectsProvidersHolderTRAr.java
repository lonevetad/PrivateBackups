package games.theRisingAngel;

import java.util.Random;

import games.generic.controlModel.gObj.BaseCreatureRPG;
import games.generic.controlModel.gObj.CreaturesProvider;
import games.generic.controlModel.inventoryAbil.EquipItemProvider;
import games.generic.controlModel.inventoryAbil.EquipmentUpgradesProvider;
import games.generic.controlModel.subimpl.GameObjectsProvidersHolderRPG;
import tools.minorTools.RandomWeightedIndexes;

// todoooo

public class GameObjectsProvidersHolderTRAr extends GameObjectsProvidersHolderRPG {
	public GameObjectsProvidersHolderTRAr() {
		super();
		this.random = new Random();
	}

	RandomWeightedIndexes equipItemsWeights;
	Random random;

	public RandomWeightedIndexes getEquipItemsWeights() {
		return equipItemsWeights;
	}

	public Random getRandom() {
		return random;
	}

	public void setEquipItemsWeights(RandomWeightedIndexes equipItemsWeights) {
		this.equipItemsWeights = equipItemsWeights;
	}

	//

	@Override
	public EquipItemProvider newEquipItemProvider() {
		return new EquipItemProvider();
	}

	@Override
	public EquipmentUpgradesProvider newEquipUpgradesProvider() {
		return new EquipmentUpgradesProvider();
	}

	@Override
	public CreaturesProvider<BaseCreatureRPG> newCreatureProvider() {
		return new CreaturesProvider<>();
	}
}