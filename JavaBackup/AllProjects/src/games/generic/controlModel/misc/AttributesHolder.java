package games.generic.controlModel.misc;

/**
 * Marks a creature (i.e., a Game Object) as having a set of attributes (or
 * "parameters", "statistics", "features", "characteristics", name it as You
 * want).
 */
public interface AttributesHolder {
	public CreatureAttributes getAttributes();

	public void setAttributes(CreatureAttributes attributes);
}