package games.generic.controlModel.subImpl;

import games.generic.controlModel.GModality;
import games.generic.controlModel.gameObj.ObjectInSpace;
import games.generic.controlModel.gameObj.WithLifeObject;
import games.generic.controlModel.misc.CurrencyHolder;
import games.generic.controlModel.player.PlayerIG_WithExperience;

/** Designed for Role Play Game. */
public abstract class PlayerInGameGeneric_ExampleRPG1 extends PlayerIG_WithExperience
		implements ObjectInSpace, WithLifeObject {
	private static final long serialVersionUID = -777564684007L;

	int life, lifeMax;
	CurrencyHolder moneys;
//	GModality gameModality;

	public PlayerInGameGeneric_ExampleRPG1(GModality gm) {
		super(gm);
//		this.gameModality = gm;
		this.life = 1;
		this.lifeMax = 1;
	}

	//

	@Override
	public int getLife() {
		return this.life;
	}

	@Override
	public int getLifeMax() {
		return this.lifeMax;
	}

	public CurrencyHolder getMoneys() {
		return moneys;
	}

	//

	//

	@Override
	public void setLife(int life) {
		if (life >= 0)
			this.life = life;
	}

	@Override
	public void setLifeMax(int lifeMax) {
		if (lifeMax > 0) {
			this.lifeMax = lifeMax;
			if (this.life > lifeMax)
				this.setLife(lifeMax);
		}
	}

	public void setMoneys(CurrencyHolder moneys) {
		this.moneys = moneys;
	}

	//

	//

	/**
	 * Override designed. <br>
	 * When the game actually starts and the player "drops into the game", some
	 * actions could be performed.
	 */
	public abstract void onStartingGame(GModality mg);

	//

	// TODO EVENTS FIRING

}