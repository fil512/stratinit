package com.kenstevens.stratinit.event;

public interface ArrivedDataEventAccumulator {

	void addEvent(DataArrivedEvent arrivedEvent);

	void clear();

	void fireEvents();

}
