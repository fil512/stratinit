package com.kenstevens.stratinit.event;

public interface ArrivedDataEventAccumulator {

	void addEvent(
			DataArrivedEvent<? extends DataArrivedEventHandler> arrivedEvent);

	void clear();

	void fireEvents();

}
