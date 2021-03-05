package com.kenstevens.stratinit.client.site;

import com.kenstevens.stratinit.remote.request.IRestRequestJson;

public abstract class PostCommand<T, R extends IRestRequestJson> extends Command<T> {
    private final R request;

    public PostCommand(R request) {
        this.request = request;
    }

    public R getRequest() {
        return request;
    }
}
