package com.kenstevens.stratinit.server.event.update;

import com.kenstevens.stratinit.server.daoservice.GameDaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class MapGameEventUpdate extends EventUpdate {
    @Autowired
    private GameDaoService gameDaoService;

    @Override
    protected void executeWrite() {
        gameDaoService.mapGame(getGame());
    }
}
