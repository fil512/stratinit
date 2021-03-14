package com.kenstevens.stratinit.client.site.command.get;

import com.kenstevens.stratinit.client.event.NewsListArrivedEvent;
import com.kenstevens.stratinit.client.model.Data;
import com.kenstevens.stratinit.client.site.GetCommand;
import com.kenstevens.stratinit.dto.news.SINewsLogsDay;
import com.kenstevens.stratinit.remote.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class GetNewsCommand extends GetCommand<List<SINewsLogsDay>> {
	@Autowired
	private Data db;

	@Override
	public Result<List<SINewsLogsDay>> execute() {
        return stratInitServer.getNewsLogs();
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
