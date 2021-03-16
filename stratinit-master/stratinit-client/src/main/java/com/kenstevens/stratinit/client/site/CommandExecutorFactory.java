package com.kenstevens.stratinit.client.site;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Service;

@Service
public class CommandExecutorFactory {
    @Lookup
    public CommandExecutor newCommandExecutor() {
        return new CommandExecutor();
    }
}
