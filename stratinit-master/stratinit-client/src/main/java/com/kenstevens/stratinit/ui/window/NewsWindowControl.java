package com.kenstevens.stratinit.ui.window;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gwt.event.shared.HandlerManager;
import com.kenstevens.stratinit.event.NewsListArrivedEvent;
import com.kenstevens.stratinit.event.NewsListArrivedEventHandler;
import com.kenstevens.stratinit.model.Data;

@Scope("prototype")
@Component
public class NewsWindowControl {
	private final NewsWindow newsWindow;
	@Autowired
	private Data db;
	@Autowired
	private HandlerManager handlerManager;

	public NewsWindowControl(NewsWindow newsWindow) {
		this.newsWindow = newsWindow;
	}

	@SuppressWarnings("unused")
	@PostConstruct
	private void addObservers() {
		handlerManager.addHandler(NewsListArrivedEvent.TYPE,
				new NewsListArrivedEventHandler() {
					@Override
					public void dataArrived() {
						if (newsWindow.isDisposed()) {
							return;
						}
						newsWindow.updateNews(db.getNewsLogList());
					}
				});
	}
}
