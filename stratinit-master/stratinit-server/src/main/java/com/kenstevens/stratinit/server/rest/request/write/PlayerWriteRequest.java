package com.kenstevens.stratinit.server.rest.request.write;

import com.kenstevens.stratinit.cache.DataCache;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.GameDaoService;
import com.kenstevens.stratinit.server.daoservice.PlayerDaoService;
import com.kenstevens.stratinit.server.rest.request.PlayerRequest;
import com.kenstevens.stratinit.server.rest.svc.DataWriter;
import com.kenstevens.stratinit.server.rest.svc.SynchronizedDataAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;

@Scope("prototype")
@Component
public abstract class PlayerWriteRequest<T> extends PlayerRequest<T> implements
        DataWriter {
    @Autowired
    protected GameDaoService gameDaoService;
    @Autowired
    protected PlayerDaoService playerDaoService;
    @Autowired
    private DataCache dataCache;
    private Result<T> result;
    private SynchronizedDataAccess synchronizedDataAccess;

    @PostConstruct
    public void init() {
        synchronizedDataAccess = new SynchronizedDataAccess(dataCache, this);
    }

    @Override
    public final T execute() {
        if (isGameRequired()) {
            synchronizedDataAccess.write(getGame());
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
            } else if (nation.getCommandPoints() < getCommandCost()) {
                result = new Result<T>("Insufficient command points.  Need " + getCommandCost() + " have " + nation.getCommandPoints() + ".", false);
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
        Date now = new Date();
        nation.setLastAction(now);
        gameDaoService.merge(nation);
        playerDaoService.setLastLogin(nation.getPlayer(), now);
    }
}
