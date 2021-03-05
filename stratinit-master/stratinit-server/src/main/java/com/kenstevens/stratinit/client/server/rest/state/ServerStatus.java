package com.kenstevens.stratinit.client.server.rest.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class ServerStatus {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private State state = State.STARTING;

    public boolean isRunning() {
        return state == State.RUNNING;
    }

    public void setShutDown() {
        logger.info("===============================================");
        logger.info("=         STOPPING SERVER                     =");
        logger.info("===============================================");
        state = State.STOPPED;
    }

    public void setRunning() {
        logger.info("###############################################");
        logger.info("#         STARTING SERVER                     #");
        logger.info("###############################################");
        state = State.RUNNING;
    }

    public State getState() {
        return state;
    }

    public enum State {
        STARTING,
        RUNNING,
        STOPPED
    }
}
