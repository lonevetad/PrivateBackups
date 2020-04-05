package games.generic.controlModel.player;

import games.generic.controlModel.gObj.ExperienceLevelHolder;
import games.generic.controlModel.subImpl.GEventInterfaceRPG;
import games.generic.controlModel.subImpl.GModalityET;

/** Delegates to a {@link ExperienceLevelHolderImpl} and add some features. */
public interface PlayerWithExperience extends PlayerGeneric, ExperienceLevelHolder {

	public ExperienceLevelHolderImpl getExpLevelHolder();

	public void setExpLevelHolder(ExperienceLevelHolderImpl expLevelHolder);

//	protected ExperienceLevelHolderImpl expLevelHolder;

	@Override
	public default int acquireExperience(int amount) {
		return getExpLevelHolder().acquireExperience(amount);
	}

	@Override
	public default int getExpToLevelUp() {
		return getExpLevelHolder().getExpToLevelUp();
	}

	@Override
	public default ExperienceLevelHolderImpl setLevel(int level) {
		return getExpLevelHolder().setLevel(level);
	}

	@Override
	public default ExperienceLevelHolderImpl setExperienceNow(int experienceNow) {
		return getExpLevelHolder().setExperienceNow(experienceNow);
	}

	@Override
	public default ExperienceLevelHolderImpl setExpToLevelUp(int experienceRequiredToLevelUp) {
		return getExpLevelHolder().setExpToLevelUp(experienceRequiredToLevelUp);
	}

	//

	public default int getExp() {
		return this.getExpLevelHolder().getExperienceNow();
	}

	@Override
	public default int getLevel() {
		return this.getExpLevelHolder().getLevel();
	}

	@Override
	public default void recalculateExpToLevelUp() {
		this.getExpLevelHolder().recalculateExpToLevelUp();
	}

	/** See {@link ExperienceLevelHolderImpl#acquireExperience(int)}. */
	public default int gainExp(int exp) {
		int levelGained;
		GModalityET gm;
		GEventInterfaceRPG geirpg;
		gm = (GModalityET) this.getGameModality();
		levelGained = this.getExpLevelHolder().acquireExperience(exp);
		geirpg = (GEventInterfaceRPG) gm.getEventInterface();
		geirpg.fireExpGainedEvent(gm, exp);
		geirpg.fireLevelGainedEvent(gm, levelGained);
		return levelGained;
	}

	//

}