package com.kenstevens.stratinit.server.event.update;

import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.server.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

@Scope("prototype")
@Component
public class TechEventUpdate extends EventUpdate {
    private final Date date;
    @Autowired
    private GameService gameService;

    public TechEventUpdate(Date date) {
        this.date = date;
    }

    @Override
    protected void executeWrite() {
        Game game = getGame();
        gameService.updateGame(game, date);
    }
}
