package com.kenstevens.stratinit.remote.request;

public class ErrorJson implements IRestRequestJson {
    public String subject;
    public String stackTrace;

    public ErrorJson() {
    }

    public ErrorJson(String subject, String stackTrace) {
        this.subject = subject;
        this.stackTrace = stackTrace;
    }
}
