package com.kenstevens.stratinit.server.remote.session;

import org.springframework.stereotype.Repository;

import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Player;

@Repository
public class ThreadLocalContext {

    private final ThreadLocal<StratInitSession> threadSession = new ThreadLocal<StratInitSession>();

    private void init() {
// TODO * use the following instead of threadlocal to hold session
//		RequestContextHolder.currentRequestAttributes().setAttribute("session", new StratInitSession(), RequestAttributes.SCOPE_GLOBAL_SESSION);

    	if(threadSession.get() == null){
    		threadSession.set(new StratInitSession());
    	}
    }
    
    private void setNation(Nation nation) {
        threadSession.get().setNation(nation);
    }
    
    public Nation getNation() {
    	return threadSession.get().getNation();
    }

    public void cleanupThread(){
        threadSession.remove();
    }

	public void initialize(Player player, StratInitSessionManager sessionManager) {
        init();
        setNation(sessionManager.getNation(player));
	}
}
