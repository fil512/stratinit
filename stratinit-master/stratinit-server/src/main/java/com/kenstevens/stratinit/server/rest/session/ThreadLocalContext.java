package com.kenstevens.stratinit.server.rest.session;

import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Player;
import org.springframework.stereotype.Repository;

@Repository
public class ThreadLocalContext {

    private final ThreadLocal<StratInitSession> threadSession = new ThreadLocal<StratInitSession>();

    private void init() {
// TODO * use the following instead of threadlocal to hold session
//		RequestContextHolder.currentRequestAttributes().setAttribute("session", new StratInitSession(), RequestAttributes.SCOPE_GLOBAL_SESSION);

        if (threadSession.get() == null) {
            threadSession.set(new StratInitSession());
        }
    }

    public Nation getNation() {
        return threadSession.get().getNation();
    }

    private void setNation(Nation nation) {
        threadSession.get().setNation(nation);
    }

    public void cleanupThread() {
        threadSession.remove();
    }

    public void initialize(Player player, StratInitSessionManager sessionManager) {
        init();
        setNation(sessionManager.getNation(player));
    }
}
