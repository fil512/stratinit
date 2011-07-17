package com.kenstevens.stratinit.spring;

import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.MessageDao;
import com.kenstevens.stratinit.dao.PlayerDao;
import com.kenstevens.stratinit.wicket.games.GameActiveListProvider;
import com.kenstevens.stratinit.wicket.games.GameArchiveListProvider;

public class SpringAware {
	protected GameDao getGameDao() {
		return (GameDao) SpringContext.getBean("gameDaoImpl");
	}

	protected PlayerDao getPlayerDao() {
		return (PlayerDao) SpringContext.getBean("playerDaoImpl");
	}
	
	protected MessageDao getMessageDao() {
		return (MessageDao) SpringContext.getBean("messageDaoImpl");
	}
	
	protected GameActiveListProvider getActiveGameListProvider() {
		return (GameActiveListProvider)SpringContext.getBean("gameActiveListProvider");
	}

	protected GameArchiveListProvider getArchiveGameListProvider() {
		return (GameArchiveListProvider)SpringContext.getBean("gameArchiveListProvider");
	}
}
