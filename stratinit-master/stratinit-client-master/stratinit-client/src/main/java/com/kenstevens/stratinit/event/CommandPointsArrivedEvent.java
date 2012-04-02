package com.kenstevens.stratinit.event;



public class CommandPointsArrivedEvent extends DataArrivedEvent<CommandPointsArrivedEventHandler> {
	public static final Type<CommandPointsArrivedEventHandler> TYPE = new Type<CommandPointsArrivedEventHandler>();
}
