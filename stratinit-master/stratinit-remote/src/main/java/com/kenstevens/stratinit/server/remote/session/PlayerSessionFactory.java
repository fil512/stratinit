package com.kenstevens.stratinit.server.remote.session;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Service;

@Service
public class PlayerSessionFactory {
    @Lookup
    public PlayerSession getPlayerSession() {
        return new PlayerSession();
    }
}
