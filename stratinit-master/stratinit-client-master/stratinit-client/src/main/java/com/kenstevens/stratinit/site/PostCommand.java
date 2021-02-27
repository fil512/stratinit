package com.kenstevens.stratinit.site;

import com.kenstevens.stratinit.remote.request.RestRequestJson;

public abstract class PostCommand<T, R extends RestRequestJson> extends Command<T> {
    private final R request;

    public PostCommand(R request) {
        this.request = request;
    }

    public R getRequest() {
        return request;
    }
}
