package com.kenstevens.stratinit.ui.window;

import com.google.common.eventbus.Subscribe;
import com.kenstevens.stratinit.event.NewsListArrivedEvent;
import com.kenstevens.stratinit.event.StratinitEventBus;
import com.kenstevens.stratinit.model.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Scope("prototype")
@Component
public class NewsWindowControl {
	private final NewsWindow newsWindow;
	@Autowired
	private Data db;
	@Autowired
	private StratinitEventBus eventBus;
	
	public NewsWindowControl(NewsWindow newsWindow) {
		this.newsWindow = newsWindow;
	}
	
	@Subscribe
	public void handleNewsListArrivedEvent(NewsListArrivedEvent event) {
		if (newsWindow.isDisposed()) {
			return;
		}
		newsWindow.updateNews(db.getNewsLogList());
	}

	@SuppressWarnings("unused")
	@PostConstruct
	private void addObservers() {
		eventBus.register(this);
	}
}
