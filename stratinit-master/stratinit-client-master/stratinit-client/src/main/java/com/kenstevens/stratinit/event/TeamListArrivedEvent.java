package com.kenstevens.stratinit.event;



public class TeamListArrivedEvent extends DataArrivedEvent<TeamListArrivedEventHandler> {
	public static final Type<TeamListArrivedEventHandler> TYPE = new Type<TeamListArrivedEventHandler>();
}
