package com.kenstevens.stratinit.event;



public class NationListArrivedEvent extends DataArrivedEvent<NationListArrivedEventHandler> {
	public static final Type<NationListArrivedEventHandler> TYPE = new Type<NationListArrivedEventHandler>();
}
