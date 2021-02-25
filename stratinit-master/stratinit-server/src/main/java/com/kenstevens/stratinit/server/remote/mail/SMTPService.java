package com.kenstevens.stratinit.server.remote.mail;

public interface SMTPService {
    void sendEmail(String to, String from, String subject, String body);

    String getSmptHostname();

    void setSmptHostname(String smptHostname);

    void disable();

    void sendException(String subject, String stackTrace);
}
