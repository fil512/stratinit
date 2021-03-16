package com.kenstevens.stratinit.client.api;

import com.kenstevens.stratinit.client.site.Command;
import com.kenstevens.stratinit.remote.Result;

public interface IStatusReporter {
    void reportResult(String message);

    void reportError(String text);

    void reportError(Exception e);

    void reportAction(String description);

    void reportLoginError();

    <T> void reportResult(Command command, Result<T> result);
}
