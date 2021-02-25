package com.kenstevens.stratinit.server.daoservice;

import com.kenstevens.stratinit.model.Game;

public interface IntegrityCheckerService {

    void checkAndFix(Game game);

}
