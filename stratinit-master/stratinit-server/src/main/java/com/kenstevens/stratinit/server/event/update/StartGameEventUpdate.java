package com.kenstevens.stratinit.server.event.update;

import com.kenstevens.stratinit.server.event.svc.EventScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Scope("prototype")
@Component
public class StartGameEventUpdate extends EventUpdate {
    @Autowired
    private EventScheduler eventScheduler;

    @Override
    protected void executeWrite() {
        eventScheduler.startGame(getGame(), true);
    }
}
