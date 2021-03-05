package com.kenstevens.stratinit.client.server.rest.request;

import com.kenstevens.stratinit.client.server.daoservice.MessageDaoService;
import com.kenstevens.stratinit.dto.news.SINewsLogsDay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class GetLogsRequest extends PlayerRequest<List<SINewsLogsDay>> {
	@Autowired
	private MessageDaoService messageDaoService;

	@Override
	protected List<SINewsLogsDay> execute() {
		return messageDaoService.getNewsLogs(getGame());
	}
}
