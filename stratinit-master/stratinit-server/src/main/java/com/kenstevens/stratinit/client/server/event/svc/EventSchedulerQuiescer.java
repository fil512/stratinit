package com.kenstevens.stratinit.client.server.event.svc;

import com.kenstevens.stratinit.QuiesceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventSchedulerQuiescer implements QuiesceService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private EventSchedulerImpl eventScheduler;

    @Override
    public void quiesce() {
        logger.info("Shutting down event scheduler.");
        eventScheduler.shutdown();
    }
}
