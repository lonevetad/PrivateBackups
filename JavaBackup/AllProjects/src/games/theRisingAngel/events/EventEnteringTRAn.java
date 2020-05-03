package games.theRisingAngel.events;

import games.generic.controlModel.ObjectNamed;
import games.generic.controlModel.gEvents.EventEnteringOnMap;
import games.generic.controlModel.gObj.ObjectInSpace;

public class EventEnteringTRAn extends EventEnteringOnMap {
	private static final long serialVersionUID = 55845824591500L;

	public EventEnteringTRAn(ObjectInSpace objectInvolved) {
		super(objectInvolved, EventsTRAn.ObjectAdded);
	}

	@Override
	public boolean isOnMapEventType(ObjectNamed eventType) {
		return eventType.getName() == EventsTRAn.ObjectAdded.getName();
	}
}