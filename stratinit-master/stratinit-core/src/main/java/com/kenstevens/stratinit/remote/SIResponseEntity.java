package com.kenstevens.stratinit.remote;

import com.google.common.annotations.VisibleForTesting;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class SIResponseEntity<T> extends ResponseEntity<Result<T>> {
    private SIResponseEntity(T body, HttpStatus status) {
        super(Result.make(body), status);
    }

    public SIResponseEntity(Result<T> result) {
        super(result, result.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    private SIResponseEntity(String message, boolean success, HttpStatus status) {
        super(new Result<>(message, success), status);
    }

    public static <T> SIResponseEntity<T> make(T body) {
        return new SIResponseEntity<>(body, HttpStatus.OK);
    }

    public static SIResponseEntity<None> success(String message) {
        return new SIResponseEntity<>(message, true, HttpStatus.OK);
    }

    public static <T> SIResponseEntity<T> failure(String message) {
        return new SIResponseEntity<>(message, false, HttpStatus.NOT_FOUND);
    }

    public static SIResponseEntity<None> trueInstance() {
        return new SIResponseEntity<>(Result.trueInstance());
    }

    public T getValue() {
        Result<T> body = getBody();
        if (body == null) {
            return null;
        }
        return body.getValue();
    }

    @VisibleForTesting
    public boolean isSuccess() {
        return getBody().isSuccess();
    }

    public boolean isMoveSuccess() {
        return getBody().isMoveSuccess();
    }
}
