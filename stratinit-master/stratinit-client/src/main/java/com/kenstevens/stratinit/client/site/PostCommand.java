package com.kenstevens.stratinit.client.site;

import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.IRestRequestJson;

public abstract class PostCommand<T, R extends IRestRequestJson> extends Command<T> {
    private final R request;
    private final String description;

    public PostCommand(R request, String description) {
        this.request = request;
        this.description = description;
    }

    @Override
    public final Result<T> execute() {
        return executePost(request);
    }

    protected abstract Result<T> executePost(R request);

    protected R getRequest() {
        return request;
    }

    public final String getDescription() {
        return description;
    }
}
