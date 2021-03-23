package com.kenstevens.stratinit.client.api;

import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.IRestRequestJson;

public interface IClientInterceptor {
    <T> void get(String path, Result<T> result);

    <T> void post(String path, IRestRequestJson request, Result<T> result);

    void getFail(String path, Exception e);

    void postFail(String path, IRestRequestJson request, Exception e);
}
