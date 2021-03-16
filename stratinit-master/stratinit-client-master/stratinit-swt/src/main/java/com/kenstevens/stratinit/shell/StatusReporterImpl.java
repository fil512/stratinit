package com.kenstevens.stratinit.shell;

import com.kenstevens.stratinit.client.event.StratinitEventBus;
import com.kenstevens.stratinit.client.site.Command;
import com.kenstevens.stratinit.remote.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
// FIXME rename
public class StatusReporterImpl implements StatusReporter {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private StratinitEventBus eventBus;

    @Override
    public void reportResult(String text) {
        Message message = new Message(text, Message.Type.RESPONSE);
        eventBus.post(new StatusReportEvent(message));
        logger.info("Response: {}", text);
    }

    @Override
    public void reportAction(String text) {
        Message message = new Message(text, Message.Type.ACTION);
        eventBus.post(new StatusReportEvent(message));
        logger.info("Action: {}", text);
    }

    @Override
    public void reportError(String text) {
        logger.error(text);
        Message message = new Message(text, Message.Type.ERROR);
        eventBus.post(new StatusReportEvent(message));
        logger.error("Error: {}", text);
    }

    @Override
    public void reportError(Exception e) {
        if (e != null && e.getMessage() != null) {
            reportError(e.getMessage());
        } else {
            reportError(e.getClass().getName());
        }
        logger.error(e.getMessage(), e);
    }

    @Override
    public void reportLoginError() {
        reportError("Not logged in.  Please login before performing any actions.");
    }

    @Override
    public <T> void reportResult(Command command, Result<T> result) {
        String text = result.toString();
        if (isBlank(text)) {
            text = command.getDescription() + " result";
        }
        if (result.isSuccess() && result.isMoveSuccess()) {
            reportAction(text);
        } else {
            reportError(text);
        }
    }
}
