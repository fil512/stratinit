package com.kenstevens.stratinit.server.rest.mail;

public interface SMTPService {
    void sendEmail(String to, String from, String subject, String body);

    void sendException(String subject, String stackTrace);
}
