package games.theRisingAngel;

import games.generic.controlModel.GModality;
import games.generic.controlModel.gameObj.CreatureOfRPGs;
import games.generic.controlModel.inventory.EquipmentSet;
import games.generic.controlModel.misc.CreatureAttributes;
import games.generic.controlModel.player.PlayerInGame_Generic;
import games.generic.controlModel.subImpl.CreatureAttributesModsCaching;

/**
 * This is NOT a {@link PlayerInGame_Generic}, even if it's similar (but there's
 * no multiple inheritance, so ... interfaces and redundancy).
 */
public class BaseNPCCreatureTRAr implements CreatureOfRPGs {
	protected boolean isDestroyed;
	protected EquipmentSet equipmentSet;
	protected CreatureAttributes attributes;
	protected Integer ID;
	protected GModality gameModality;

	public BaseNPCCreatureTRAr() {
		this.isDestroyed = false;
		this.ID = CreatureUIDProvider.newID();
		this.equipmentSet = new EquipmentSetTRAr();
		this.attributes = new CreatureAttributesModsCaching(AttributesTRAr.VALUES.length);
	}

	@Override
	public boolean isDestroyed() {
		return false;
	}

	@Override
	public Integer getID() {
		return ID;
	}

	@Override
	public int getLifeMax() {
		return 0;
	}

	@Override
	public int getLife() {

		return 0;
	}

	@Override
	public CreatureAttributes getAttributes() {
		return attributes;
	}

	@Override
	public EquipmentSet getEquipmentSet() {
		return equipmentSet;
	}

	@Override
	public GModality getGameModality() {
		return gameModality;
	}

	//

	@Override
	public void setGameModality(GModality gameModality) {
		this.gameModality = gameModality;
	}

	@Override
	public void setEquipmentSet(EquipmentSet equips) {
		this.equipmentSet = equips;
	}

	@Override
	public void setAttributes(CreatureAttributes attributes) {
		this.attributes = attributes;
	}

	@Override
	public void setLife(int life) {

	}

	@Override
	public void setLifeMax(int lifeMax) {

	}

	//

	@Override
	public void receiveDamage(GModality gm, int damage) {

	}

	@Override
	public void receiveHealing(GModality gm, int healingAmount) {

	}

	@Override
	public void fireDamageReceived(GModality gm, int originalDamage) {

	}

	@Override
	public void fireHealingReceived(GModality gm, int originalHealing) {

	}

	@Override
	public void fireDestruction(GModality gm) {

	}

	@Override
	public boolean destroy() {

		return false;
	}

	@Override
	public void act(GModality modality, long milliseconds) {
		// TODO make progress ALL abilities .. or maybe not, jus hardcoded abilities
	}
}