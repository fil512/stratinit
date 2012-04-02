package com.kenstevens.stratinit.event;



public class MessageListArrivedEvent extends DataArrivedEvent<MessageListArrivedEventHandler> {
	public static final Type<MessageListArrivedEventHandler> TYPE = new Type<MessageListArrivedEventHandler>();
}
