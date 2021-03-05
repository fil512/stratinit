package com.kenstevens.stratinit.client.server.event.update;

import com.kenstevens.stratinit.client.server.event.svc.EventSchedulerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Scope("prototype")
@Component
public class StartGameEventUpdate extends EventUpdate {
    @Autowired
    private EventSchedulerImpl eventScheduler;

    @Override
    protected void executeWrite() {
        eventScheduler.startGame(getGame(), true);
    }
}
