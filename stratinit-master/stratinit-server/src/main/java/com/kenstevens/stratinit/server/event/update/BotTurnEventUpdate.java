package com.kenstevens.stratinit.server.event.update;

import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.dao.NationDao;
import com.kenstevens.stratinit.server.bot.BotExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class BotTurnEventUpdate extends EventUpdate {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private NationDao nationDao;
    @Autowired
    private BotExecutor botExecutor;

    @Override
    protected void executeWrite() {
        List<Nation> nations = nationDao.getNations(getGame());
        for (Nation nation : nations) {
            if (nation.getPlayer().isBot()) {
                try {
                    botExecutor.executeTurn(nation);
                } catch (Exception e) {
                    logger.error("Bot turn failed for nation {} in game {}",
                            nation.getName(), getGame().getId(), e);
                }
            }
        }
    }
}
