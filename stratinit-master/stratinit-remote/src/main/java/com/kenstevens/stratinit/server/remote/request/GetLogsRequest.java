package com.kenstevens.stratinit.server.remote.request;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.dto.news.SINewsLogsDay;
import com.kenstevens.stratinit.server.daoservice.MessageDaoService;

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
