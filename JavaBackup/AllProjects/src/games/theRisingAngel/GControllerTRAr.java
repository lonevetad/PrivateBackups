package games.theRisingAngel;

import games.generic.controlModel.misc.LoaderGeneric;
import games.generic.controlModel.player.UserAccountGeneric;
import games.generic.controlModel.subimpl.GControllerRPG;
import games.generic.controlModel.subimpl.GameObjectsProvidersHolderRPG;
import games.theRisingAngel.loaders.LoaderAbilityTRAr;
import games.theRisingAngel.loaders.LoaderCreatureTRAr;
import games.theRisingAngel.loaders.LoaderEquipTRAr;
import games.theRisingAngel.loaders.LoaderEquipUpgradesTRAr;

public class GControllerTRAr extends GControllerRPG {

	public GControllerTRAr() {
		super();
	}

	@Override
	protected GameObjectsProvidersHolderRPG newGameObjectsManagerProvider() {
		return new GameObjectsProvidersHolderTRAr();
	}

	@Override
	protected void defineGameModalitiesFactories() {
		// TODO Auto-generated method stub

	}

	@Override
	protected UserAccountGeneric newUserAccount() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected LoaderGeneric newLoaderConfigurations(GControllerRPG cgRPG) {
		return new LoaderConfigurations();
	}

	@Override
	protected void onCreate() {
		super.onCreate();
		super.addGameObjectLoader(new LoaderAbilityTRAr(this.gameObjectsProvidersHolderRPG.getAbilitiesProvider()));
		super.addGameObjectLoader(
				new LoaderEquipUpgradesTRAr(this.gameObjectsProvidersHolderRPG.getEquipUpgradesProvider()));
		super.addGameObjectLoader(new LoaderEquipTRAr(this.gameObjectsProvidersHolderRPG.getEquipmentsProvider()));
		super.addGameObjectLoader(new LoaderCreatureTRAr(this.gameObjectsProvidersHolderRPG.getCreaturesProvider()));

	}

}