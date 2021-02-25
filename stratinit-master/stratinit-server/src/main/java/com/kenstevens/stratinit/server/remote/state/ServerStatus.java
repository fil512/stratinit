package com.kenstevens.stratinit.server.remote.state;

import org.springframework.stereotype.Repository;

@Repository
public class ServerStatus {

    private State state = State.STARTING;

    public boolean isRunning() {
        return state == State.RUNNING;
    }

    public void setShutDown() {
        state = State.STOPPED;
    }

    public void setRunning() {
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
