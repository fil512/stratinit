package com.kenstevens.stratinit.event;



public class NewsListArrivedEvent extends DataArrivedEvent<NewsListArrivedEventHandler> {
	public static final Type<NewsListArrivedEventHandler> TYPE = new Type<NewsListArrivedEventHandler>();
}
