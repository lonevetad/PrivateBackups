package games.generic.controlModel.misc;

import java.util.Map;

import games.generic.controlModel.GModality;
import games.generic.controlModel.inventoryAbil.AbilityGeneric;
import games.generic.controlModel.inventoryAbil.EquipmentItem;
import games.generic.controlModel.inventoryAbil.EquipmentUpgrade;

public abstract class EssenceExtractor {

	public static enum EssenceApplianceStatus {
		Success, NullParameter, Error404EssenceNotFound, YetPresent, VialEmpty
	}

	public EssenceExtractor() {}

	//

	/**
	 * Get the name of the {@link GameObjectsProvider} associated with
	 * {@link EquipmentUpgrade}.
	 */
	public abstract String getEquipmentUpgradeObjProviderName(GModality gm);

	/**
	 * Get the name of the {@link GameObjectsProvider} associated with
	 * {@link AbilityGeneric}.
	 */
	public abstract String getAbilityObjProviderName(GModality gm);

	public boolean storeEssence(EssenceVial vial, EquipmentUpgrade upgrade) {
		if (vial == null || upgrade == null || (!vial.isEmpty()))
			return false;
		vial.storeEssence(upgrade);
		return true;
	}

	public boolean storeEssence(EssenceVial vial, AbilityGeneric ability) {
		if (vial == null || ability == null || (!vial.isEmpty()))
			return false;
		vial.storeEssence(ability);
		return true;
	}

	/**
	 * Remove the essence from a given {@link EquipmentItem} and store it into a
	 * {@link EssenceVial}. No costs are applied: it depends on the given
	 * {@link GModality} implementing this feature.
	 */
	public boolean extractEssenceByName(EssenceVial vial, EquipmentItem equipment, String nameEssence) {
		Map<String, EquipmentUpgrade> equips;
		Map<String, AbilityGeneric> abil;
		EquipmentUpgrade upgrade;
		AbilityGeneric ability;
		if (vial == null || equipment == null || vial.isEmpty())
			return false;
		abil = equipment.getAbilities();
		equips = equipment.getUpgradesMap();
		upgrade = equips.get(nameEssence);
		if (upgrade != null) {
			storeEssence(vial, upgrade);
			equips.remove(nameEssence);
		} else {
			ability = abil.get(nameEssence);
			if (ability != null) {
				storeEssence(vial, ability);
				abil.remove(nameEssence);
			}
		}
		return true;
	}

	/**
	 * If possible, apply the given essence, taken from the given essence, to the
	 * equipment, returning {@link EssenceApplianceStatus#Success}. In case of
	 * error, another {@link EssenceApplianceStatus}'s value is returned.
	 */
	@SuppressWarnings("unchecked")
	public EssenceApplianceStatus applyEssence(GModality gm, EssenceVial vial, EquipmentItem equipment) {
		Map<String, EquipmentUpgrade> equips;
		Map<String, AbilityGeneric> abil;
		String essenceName;
		FactoryObjGModalityBased<EquipmentUpgrade> factoryEquipUpgrade;
		FactoryObjGModalityBased<AbilityGeneric> factoryAbility;
		if (gm == null || vial == null || equipment == null)
			return EssenceApplianceStatus.NullParameter;
		if (vial.isEmpty())
			return EssenceApplianceStatus.VialEmpty;
		essenceName = vial.getEssenceName();
		abil = equipment.getAbilities();
		equips = equipment.getUpgradesMap();
		factoryEquipUpgrade = (FactoryObjGModalityBased<EquipmentUpgrade>) gm.getGameObjectsProvider()
				.getProvider(getEquipmentUpgradeObjProviderName(gm)).getObjByName(essenceName);
		factoryAbility = (FactoryObjGModalityBased<AbilityGeneric>) gm.getGameObjectsProvider()
				.getProvider(getAbilityObjProviderName(gm)).getObjByName(essenceName);
		if (factoryEquipUpgrade != null) {
			if (abil.containsKey(essenceName))
				return EssenceApplianceStatus.YetPresent;
			equipment.addUpgrade(factoryEquipUpgrade.newInstance(gm));
			vial.removeEssence();
		} else if (factoryAbility != null) {
			if (equips.containsKey(essenceName))
				return EssenceApplianceStatus.YetPresent;
			equipment.addAbility(factoryAbility.newInstance(gm));
			vial.removeEssence();
		} else {
			return EssenceApplianceStatus.Error404EssenceNotFound;
		}
		return EssenceApplianceStatus.Success;
	}
}