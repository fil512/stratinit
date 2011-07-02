package com.kenstevens.stratinit.client.gwt.widget;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.Window;

public class GameWindow implements GameDisplayer {
	private HTMLFlow html;

	private final Window window = new Window();

	public GameWindow(int gameId) {
        window.setTitle("Game "+gameId);
        window.setWidth(600);
        window.setHeight(600);
        window.setAutoSize(true);
        window.setTop(10);
        window.setLeft(10);
        window.setCanDragReposition(true);
        window.setCanDragResize(true);
        Canvas canvas = new Canvas();
        html = new HTMLFlow();
        canvas.addChild(html);
        window.addChild(canvas);
	}
	public void displayGame(String result) {
		html.setContents("<PRE>"+result+"</PRE>");
        window.show();
	}
}
