package tests.tGame.tgEvent1;

import games.generic.controlModel.GController;
import games.generic.controlModel.GEventInterface;
import games.generic.controlModel.GModel;
import games.generic.controlModel.GThread;
import games.generic.controlModel.player.PlayerInGame_Generic;
import games.generic.controlModel.player.PlayerOutside_Generic;
import games.generic.controlModel.subImpl.GModalityET;
import games.generic.controlModel.subImpl.PlayerInGameGeneric_ExampleRPG1;
import games.generic.controlModel.utils.CurrencyHolder;
import tests.tGame.tgEvent1.oggettiDesempio.ObjPrinterTO;
import tests.tGame.tgEvent1.oggettiDesempio.ObjPrinter_EventDeliver;
import tests.tGame.tgEvent1.oggettiDesempio.ObserverPrinterEvent;

public class GModality_E1 extends GModalityET {

	public GModality_E1(GController controller, String modalityName) {
		super(controller, modalityName);
	}

	GThread threadGame;
	PlayerInGameGeneric_ExampleRPG1 playerRPG;

	@Override
	public void onCreate() {
		super.onCreate();
		checkAndRebuildThreads();
		//

		// TODO add all stuffs .. qui � il posto in cui dovrebbero stare gli oggetti
		// strani che inserisco
//		GModel_E1 gModelE ;
//		addTimedObject(new ObjDamageDeliver());
		this.addGameObject(new ObjPrinterTO(1250, "LongWaiting"));
		this.addGameObject(new ObjPrinterTO(333, "Short"));
//		this.addGameObject(new ObjPrinter_EventDeliver(250, "Tiny"));
		// gModelE.addTimeProgressingObject(odd);

		this.addGameObject(new ObjPrinter_EventDeliver(2000, "HAKINA MATATA"));
		this.getEventManager().addEventObserver(new ObserverPrinterEvent());

		// TODO aggiungere gli esempi pensati negli Appunti e esempio
		// first make the player, then the damager, the healer, the fairy, the
		// money-maker, etc

//		addTimedObject(new ObjDamageDeliver());

		// then ...
	}

	@Override
	public GModel newGameModel() {
		return new GModel_E1();
	}

	@Override
	public GEventInterface newEventInterface() {
		GEventInterface_E1 gei;
		gei = new GEventInterface_E1();
		gei.setNewGameEventManager(this);
		return gei;
	}

	@Override
	public void startGame() {
		checkAndRebuildThreads();
		this.threadGame.start();
	}

	@Override
	public void closeAll() {
		super.closeAll();
		this.threadGame = null;
	}

	@Override
	protected PlayerInGame_Generic newPlayerInGame(PlayerOutside_Generic superPlayer) {
		Player_E1 p;
		p = new Player_E1(this);
		p.setMoneys(newCurrencyHolder());
		return p;
	}

	@Override
	public CurrencyHolder newCurrencyHolder() {
		return new CurrencyHolder_E1(this, 1);
	}

	//

	protected void checkAndRebuildThreads() {
		if (this.threadGame == null)
			this.threadGame = new GThread(new RunGameInstance());
	}

	//

	// previously was ThreadGame_GameRunner_E1
	protected class RunGameInstance implements GThread.GTRunnable {
		boolean isWorking = true; // when the

		@Override
		public void run() {
			while(isWorking) {
				runGameCycle();
			}
		}

		@Override
		public void stopAndDie() {
			this.isWorking = false;
		}
	}
}