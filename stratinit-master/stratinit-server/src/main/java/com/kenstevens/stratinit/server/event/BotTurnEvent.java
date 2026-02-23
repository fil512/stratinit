package com.kenstevens.stratinit.server.event;

import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.client.model.Updatable;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.server.event.svc.StratInitUpdater;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class BotTurnEvent extends Event {
    @Autowired
    private StratInitUpdater stratInitUpdater;

    public BotTurnEvent(Game game) {
        super(new BotTurnUpdatable(game));
    }

    @Override
    protected void execute() {
        Integer gameId = (Integer) getEventKey().key();
        stratInitUpdater.executeBotTurns(gameId);
    }

    /**
     * Wraps a Game to provide bot-specific update period while keeping the
     * same game ID as the event key, using a distinct class for EventKey uniqueness.
     */
    private static class BotTurnUpdatable extends Updatable {
        private static final long serialVersionUID = 1L;
        private final Game game;

        BotTurnUpdatable(Game game) {
            this.game = game;
            setLastUpdated(game.getLastUpdated());
        }

        @Override
        public long getUpdatePeriodMilliseconds() {
            return Constants.BOT_TURN_INTERVAL_SECONDS * DateUtils.MILLIS_PER_SECOND;
        }

        @Override
        public boolean isBlitz() {
            return game.isBlitz();
        }

        @Override
        public Object getKey() {
            return game.getId();
        }

        @Override
        public boolean isKeyUnique() {
            return false;
        }
    }
}
