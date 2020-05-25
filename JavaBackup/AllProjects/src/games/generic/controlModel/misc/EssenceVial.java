package games.generic.controlModel.misc;

import java.io.Serializable;

import games.generic.controlModel.inventoryAbil.AbilityGeneric;
import games.generic.controlModel.inventoryAbil.EquipmentUpgrade;
import tools.ObjectNamedID;

/**
 * Storage for an "essence" of something, like {@link EquipmentUpgrade} or an
 * {@link AbilityGeneric}.<br>
 * (Actually, it just store its key, that is its name, since both are instances
 * of {@link ObjectNamedID}.)
 */
public class EssenceVial implements Serializable {
	private static final long serialVersionUID = 365014755285420100L;

	public EssenceVial() {}

	protected boolean isEquipmentUpgrade;
	protected String essenceName = null;

	/**
	 * Returns <code>true</code> if the extracted essence, if any, refers to an
	 * {@link EquipmentUpgrade} or an {@link AbilityGeneric}.
	 */
	public boolean isEquipmentUpgrade() { return isEquipmentUpgrade; }

	public String getEssenceName() { return essenceName; }

	/** Test if some essence is stored */
	public boolean isEmpty() { return essenceName == null; }

	//

	public boolean storeEssence(EquipmentUpgrade essence) {
		if (essence == null || this.essenceName != null)
			return false;
		this.essenceName = essence.getName();
		this.isEquipmentUpgrade = true;
		return true;
	}

	public boolean storeEssence(AbilityGeneric essence) {
		if (essence == null || this.essenceName != null)
			return false;
		this.essenceName = essence.getName();
		this.isEquipmentUpgrade = false;
		return true;
	}

	public void removeEssence() { this.essenceName = null; }
}