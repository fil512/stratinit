package com.kenstevens.stratinit.spring;

import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.MessageDao;
import com.kenstevens.stratinit.dao.PlayerDao;

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
}
