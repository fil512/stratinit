package com.kenstevens.stratinit.client.site;

// TODO REF can we get rid of actions and just use commands?


import com.kenstevens.stratinit.client.util.Spring;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

public abstract class PostAction<T extends Command<?>> extends Action<T> {
    private final T command;
    @Autowired
    private Spring spring;

    protected PostAction(T command) {
        this.command = command;
    }

    @PostConstruct
    public void inititialize() {
        spring.autowire(command);
    }

    @Override
    public T getCommand() {
        return command;
    }

    @Override
    public boolean canRepeat() {
        return true;
    }

    // FIXME remove once all post actions have been converted to use this subclass
    @Override
    protected T buildCommand() {
        throw new UnsupportedOperationException();
    }
}
