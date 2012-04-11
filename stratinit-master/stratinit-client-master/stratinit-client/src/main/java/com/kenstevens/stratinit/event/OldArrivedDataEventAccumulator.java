package com.kenstevens.stratinit.event;

@Deprecated
public interface OldArrivedDataEventAccumulator {

	void addEvent(
			OldDataArrivedEvent<? extends OldDataArrivedEventHandler> arrivedEvent);

	void clear();

	void fireEvents();

}
