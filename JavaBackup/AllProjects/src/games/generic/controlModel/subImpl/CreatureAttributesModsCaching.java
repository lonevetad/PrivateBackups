package games.generic.controlModel.subImpl;

import games.generic.controlModel.inventory.EquipmentAttributeModifier;
import games.generic.controlModel.misc.CreatureAttributes;

/**
 * Caches all attribute's modifications (as like
 * {@link EquipmentAttributeModifier}) in a separated array: use
 * {@link #addAttributeModification(int, int)} and
 * {@link #removeAttributeModification(int, int)}.
 */
public class CreatureAttributesModsCaching extends CreatureAttributes {

	protected int[] computedAttributesModifications;

	public CreatureAttributesModsCaching(int attributesCount) {
		super(attributesCount);
		this.computedAttributesModifications = new int[attributesCount];
	}

	public int[] getComputedAttributesModifications() {
		return computedAttributesModifications;
	}

	@Override
	public void applyAttributeModifier(EquipmentAttributeModifier eam) {
		this.computedAttributesModifications[eam.getAttributeModified().getIndex()] += eam.getValue();
	}

	@Override
	public void removeAttributeModifier(EquipmentAttributeModifier eam) {
		this.computedAttributesModifications[eam.getAttributeModified().getIndex()] -= eam.getValue();
	}

	@Override
	public int getValue(int index) {
		return super.originalValues[index] + this.computedAttributesModifications[index];
	}
//	public void addAttributeModification(int index, int value) { this.computedAttributesModifications[index] += value; }
//	public void removeAttributeModification(int index, int value) { this.computedAttributesModifications[index] -= value; }
}