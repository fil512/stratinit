package com.kenstevens.stratinit.event;

public class GameChangedEvent extends StratInitEvent<GameChangedEventHandler> {
	public static final Type<GameChangedEventHandler> TYPE = new Type<GameChangedEventHandler>();

	@Override
	protected void dispatch(GameChangedEventHandler handler) {
		handler.gameChanged();
	}

}
