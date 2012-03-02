package com.kenstevens.stratinit.event;



public class CityListArrivedEvent extends DataArrivedEvent<CityListArrivedEventHandler> {
	public static final Type<CityListArrivedEventHandler> TYPE = new Type<CityListArrivedEventHandler>();
}
