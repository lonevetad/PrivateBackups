package games.generic.controlModel.inventory;

import java.util.function.Function;

import games.generic.controlModel.GEventObserver;
import games.generic.controlModel.GModality;
import games.generic.controlModel.gameObj.AbilityGeneric;
import games.generic.controlModel.gameObj.CreatureOfRPGs;
import games.generic.controlModel.gameObj.TimedObject;

/**
 * Some {@link EquipmentItem} could have special abilities. Instead of relying
 * on equipment's subclasses implementations, separates the ability in this
 * interface to enhance OOP design and allow abilities reuse (and maybe
 * "extraction in a magic essence to apply in other items")
 */
public interface EquipItemAbility extends AbilityGeneric {

	public static final Function<EquipItemAbility, Integer> ID_EXTRACTOR = e -> e.getID();
	public static final Function<EquipItemAbility, String> NAME_EXTRACTOR = e -> e.getName();

	//

	/** Returns the {@link EquipmentItem} having this ability. */
	public EquipmentItem getEquipItem();

	public void setEquipItem(EquipmentItem equipmentItem);

	/**
	 * Upon being equipped to "someone" (i.e.: {@link CreatureOfRPGs} instance),
	 * this {@link EquipmentItem} (provided by {@link getEquipItem()}) could need to
	 * perform some work and adjustment.<br>
	 * It's provided the {@link GModality} to let modifications to be applied and
	 * abilities ({@link EquipItemAbility}) to be activated. If it's needed the
	 * {@link CreatureOfRPGs}, which have the {@link EquipmentSet} where is equipped
	 * the {@link EquipmentItem} (returned by {@link #getEquipItem()}) having this
	 * ability (that my father at the market bought...), then see the code at the
	 * bottom.
	 * <p>
	 * Example of that "works":
	 * <ul>
	 * <li>Could apply static effects like auras.</li>
	 * <li>{@link TimedObject}s requires to be added to the {@link GModality}:
	 * example a "chest" dealing a nova explosion each N seconds</li>
	 * <li>{@link GEventObserver}: a belt dropping moneys every time some damage is
	 * received, or a ring healing the creature upon dropping moneys.</li>
	 * <li>An item could modify some attribute(S) depending on some factors (i.e.:
	 * bonus in LifeMax equal to 10% of current money's amount). An implementation
	 * could be being a {@link TimedObject} that, each seconds, subtract the
	 * "previous value(s)" (initially 0 and used as a "cache") and add the newly
	 * computed value(s) to the {@link CreatureOfRPGs}'s attribute(s), that will be
	 * saved in that "cache".</li>
	 * </ul>
	 * <p>
	 * NOTE: to obtain the {@link CreatureOfRPGs} which this equipment's ability is
	 * applied on, use the following chain code:
	 * 
	 * <pre>
	 * <code>
	 * CreatureOfRPGs creatureHolder;
	 * creatureHolder = {@link #getEquipItem()}.getBelongingEquipmentSet().getCreatureWearingEquipments();
	 * // use creatureHolder ... , but beware of nulls 
	 * 
	 * // or the shorthand
	 * creatureHolder = {@link #getEquipItem()}.getCreatureWearingEquipments();
	 * // use creatureHolder ... , but beware of nulls
	 * </code>
	 * </pre>
	 * 
	 * See {@link EquipmentItem#getBelongingEquipmentSet()} and
	 * {@link EquipmentSet#getCreatureWearingEquipments()} for further
	 * informations.>
	 */
	public void onEquip(GModality gm);

	/**
	 * The opposite work of {@link #onEquip(GModality)}, stopping every acting work
	 * AND resetting to the original state
	 */
	public void onUnEquipping(GModality gm);
}