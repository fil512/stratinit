package com.kenstevens.stratinit.event;



public class GameListArrivedEvent extends DataArrivedEvent<GameListArrivedEventHandler> {
	public static final Type<GameListArrivedEventHandler> TYPE = new Type<GameListArrivedEventHandler>();
}
