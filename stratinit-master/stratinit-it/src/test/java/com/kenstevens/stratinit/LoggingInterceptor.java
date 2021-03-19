package com.kenstevens.stratinit;

import com.kenstevens.stratinit.client.api.IClientInterceptor;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.IRestRequestJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingInterceptor implements IClientInterceptor {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public <T> void get(String path, Result<T> result) {
        logger.info("{}: {}", path, result.getValue());
    }

    @Override
    public <T> void post(String path, IRestRequestJson request, Result<T> result) {
        logger.info("{}: {}\n{}", path, request, result.getValue());
    }

    @Override
    public void getFail(String path, Exception e) {
        logger.error("{}", path, e);
    }

    @Override
    public void postFail(String path, IRestRequestJson request, Exception e) {
        logger.error("{}: {}", path, request, e);
    }
}
