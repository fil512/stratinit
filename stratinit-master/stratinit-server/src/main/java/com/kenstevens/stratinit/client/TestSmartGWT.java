package com.kenstevens.stratinit.client;

import com.google.gwt.core.client.EntryPoint;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class TestSmartGWT implements EntryPoint {
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		IButton button = new IButton("Hello World");
		button.addClickHandler(new ClickHandler() {
							   public void onClick(ClickEvent event) {
							   SC.say("Hello World from SmartGWT");
							   }
							   });
		
		button.draw();
		
		//or
		//RootPanel.get().add(button);
	}
}