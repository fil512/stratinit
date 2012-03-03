package com.kenstevens.stratinit.server.daoservice;

import java.util.List;

import com.kenstevens.stratinit.dto.news.SINewsLogsDay;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Mail;
import com.kenstevens.stratinit.model.Nation;

public interface MessageDaoService {

	public abstract Mail sendMail(Nation from, Nation to, String subject,
			String body);

	public abstract void notify(Nation to, String subject, String body);

	public abstract void postBulletin(Game game, String subject, String body);

	public abstract List<SINewsLogsDay> getNewsLogs(Game game);

}