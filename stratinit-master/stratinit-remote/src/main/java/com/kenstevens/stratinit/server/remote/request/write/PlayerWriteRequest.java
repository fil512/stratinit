package com.kenstevens.stratinit.server.remote.request.write;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.cache.DataCache;
import com.kenstevens.stratinit.main.Spring;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.GameDaoService;
import com.kenstevens.stratinit.server.remote.helper.DataWriter;
import com.kenstevens.stratinit.server.remote.helper.SynchronizedDataAccess;
import com.kenstevens.stratinit.server.remote.request.PlayerRequest;

@Scope("prototype")
@Component
public abstract class PlayerWriteRequest<T> extends PlayerRequest<T> implements
		DataWriter {
	@Autowired
	protected GameDaoService gameDaoService;
	@Autowired
	private Spring spring;
	@Autowired
	private DataCache dataCache;
	private Result<T> result;

	@Override
	public final T execute() {
		if (isGameRequired()) {
			spring.autowire(new SynchronizedDataAccess( getGame(), this )).write();
		} else {
			writeData();
		}
		return result.getValue();
	}

	public void writeData() {
		Nation nation = getNation();
		if (nation != null && getCommandCost() > 0) {
			if (nation.getCommandPoints() <= 0) {
				result = new Result<T>("You are out of command points.", false);
				return;
			}
		}
		setLastAction();
		result = executeWrite();
		if (result.isSuccess() && nation != null) {
			nation.decreaseCommandPoints(getCommandCost());
		}
	}

	protected abstract int getCommandCost();

	protected abstract Result<T> executeWrite();

	@Override
	protected Result<T> getResult() {
		return result;
	}

	protected void setLastAction() {
		Nation nation = getNation();
		
		if (nation == null || dataCache.getGameCache(nation.getGame()) == null) {
			return;
		}
		nation.setLastAction(new Date());
		gameDaoService.merge(nation);
	}
}
