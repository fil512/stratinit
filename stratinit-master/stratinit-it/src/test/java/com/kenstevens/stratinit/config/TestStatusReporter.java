package com.kenstevens.stratinit.config;

import com.kenstevens.stratinit.client.api.IStatusReporter;
import com.kenstevens.stratinit.client.site.Command;
import com.kenstevens.stratinit.remote.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestStatusReporter implements IStatusReporter {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String lastError;

    @Override
    public void reportResult(String message) {
        logger.info(message);
    }

    @Override
    public void reportError(String text) {
        logger.error(text);
        lastError = text;
    }

    @Override
    public void reportError(Exception e) {
        logger.error(e.getMessage(), e);
        lastError = e.getMessage();
    }

    @Override
    public void reportAction(String description) {
        logger.info(">A> {}", description);
    }

    @Override
    public void reportLoginError() {
        logger.error("LOGIN ERROR");
    }

    @Override
    public <T> void reportResult(Command command, Result<T> result) {
        logger.info(">C> {}", command.getDescription());
        logger.info(">R> {}", result.toString());
    }

    public String getLastError() {
        return lastError;
    }
}
