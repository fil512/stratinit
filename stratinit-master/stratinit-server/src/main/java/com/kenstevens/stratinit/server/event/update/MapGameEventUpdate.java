package com.kenstevens.stratinit.server.event.update;

import com.kenstevens.stratinit.server.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class MapGameEventUpdate extends EventUpdate {
    @Autowired
    private GameService gameService;

    @Override
    protected void executeWrite() {
        gameService.mapGame(getGame());
    }
}
