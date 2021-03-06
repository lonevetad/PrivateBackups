package games.generic.controlModel.gEvents;

import games.generic.controlModel.ObjectNamed;
import geometry.ObjectLocated;

public class EventLeavingMap extends EventOnMap {
	private static final long serialVersionUID = -562151896502L;

	public EventLeavingMap(ObjectLocated objectInvolved, ObjectNamed eventType) {
		super(objectInvolved, eventType != null ? eventType : ExampleGameEvents.ObjectRemoved);
		this.objectInvolved = objectInvolved;
	}

	@Override
	public boolean isOnMapEventType(ObjectNamed eventType) {
		return eventType.getName() == ExampleGameEvents.ObjectRemoved.getName();
	}
}