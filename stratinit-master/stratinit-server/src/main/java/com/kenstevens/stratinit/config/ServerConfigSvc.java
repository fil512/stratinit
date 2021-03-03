package com.kenstevens.stratinit.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ServerConfigSvc extends ServerConfig {

    @Autowired
    public ServerConfigSvc(@Value("${stratinit.mode}") RunModeEnum runModeEnum) {
        super(runModeEnum);
    }
}
