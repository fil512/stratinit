package com.kenstevens.stratinit.event;


public abstract class DataArrivedEvent<H extends DataArrivedEventHandler> extends StratInitEvent<H> {
	@Override
	protected void dispatch(H handler) {
		handler.dataArrived();
	}

}
