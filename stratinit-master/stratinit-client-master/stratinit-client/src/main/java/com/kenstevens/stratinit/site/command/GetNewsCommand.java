package com.kenstevens.stratinit.site.command;

import com.kenstevens.stratinit.dto.news.SINewsLogsDay;
import com.kenstevens.stratinit.event.NewsListArrivedEvent;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.remote.SIResponseEntity;
import com.kenstevens.stratinit.site.Command;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class GetNewsCommand extends Command<List<SINewsLogsDay>> {
	@Autowired
	private Data db;

	@Override
	public SIResponseEntity<List<SINewsLogsDay>> execute() {
		return stratInit.getNewsLogs();
	}

	@Override
	public String getDescription() {
		return "Get news";
	}

	@Override
	public void handleSuccess(List<SINewsLogsDay> newsLogs) {
		db.getNewsLogList().addAll(newsLogs);
		arrivedDataEventAccumulator.addEvent(new NewsListArrivedEvent());
	}
}
