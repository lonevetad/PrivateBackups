package games.theRisingAngel.abilities;

import java.util.ArrayList;
import java.util.List;

import games.generic.controlModel.GEventObserver;
import games.generic.controlModel.GModality;
import games.generic.controlModel.IGEvent;
import games.generic.controlModel.gEvents.EventHealing;
import games.generic.controlModel.gObj.CurrencyHolder;
import games.generic.controlModel.gObj.LivingObject;
import games.generic.controlModel.inventoryAbil.abilitiesImpl.EquipmentAbilityBaseImpl;
import games.generic.controlModel.misc.CurrencySet;
import games.theRisingAngel.events.EventsTRAr;
import tools.ObjectWithID;

/**
 * Upon receiving healing, make the owner earn N percentage of the base
 * currency, if possible.
 * <p>
 * N = 50.
 */
public class AHealingMakesEarnBaseCurrency extends EquipmentAbilityBaseImpl implements GEventObserver {
	private static final long serialVersionUID = -5898625452208602147L;
	public static final String NAME = "Healthy wallet in healthy body";

	public AHealingMakesEarnBaseCurrency() {
		super();
		this.eventsWatching = new ArrayList<>(2);
		this.eventsWatching.add(EventsTRAr.HealReceived.getName());
		this.hasReceivedOddReneration = false;
	}

	protected boolean hasReceivedOddReneration;
	protected List<String> eventsWatching;

//	public CreatureSimple getCreatureReferred() {return creatureReferred;}

	@Override
	public List<String> getEventsWatching() {
		return eventsWatching;
	}

	@Override
	public void performAbility(GModality gm) {
	}

	@Override
	public void resetAbility() {
		this.hasReceivedOddReneration = false;
	}

	@Override
	public void notifyEvent(GModality modality, IGEvent ge) {
		if (EventsTRAr.HealReceived.getName() == ge.getName()) {
			int amoutHealed;
			EventHealing<?> eventHealing;
			CurrencyHolder ch;
			CurrencySet cs;
			LivingObject ol;
			ObjectWithID owid;
			eventHealing = (EventHealing<?>) ge;
			amoutHealed = eventHealing.getHeal().getHealAmount();
			owid = getOwner();
			if (eventHealing.getTarget() == owid //
//					&&(amoutHealed > 1
//							|| (hasReceivedOddReneration && amoutHealed > 0))//
					&& (owid instanceof CurrencyHolder)) {
				ol = (LivingObject) owid;
				if ((amoutHealed & 0x1) == 1) {
					if (hasReceivedOddReneration) {
						amoutHealed++;
						hasReceivedOddReneration = false;
					} else {
						hasReceivedOddReneration = true;
					}
				}
				if (ol.getLife() < ol.getLifeMax()) {
					// apply only if the regeneration has really happened
					ch = (CurrencyHolder) owid;
					cs = ch.getCurrencies();
					cs.setMoneyAmount(CurrencySet.BASE_CURRENCY_INDEX,
							cs.getMoneyAmount(CurrencySet.BASE_CURRENCY_INDEX) + (amoutHealed >> 1));
				}
			}
		}
	}
}