package com.kenstevens.stratinit.remote.request;

import jakarta.validation.constraints.NotBlank;

public class ErrorJson implements IRestRequestJson {
    @NotBlank
    public String subject;
    public String stackTrace;

    public ErrorJson() {
    }

    public ErrorJson(String subject, String stackTrace) {
        this.subject = subject;
        this.stackTrace = stackTrace;
    }
}
