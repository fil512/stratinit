package com.kenstevens.stratinit.server.remote.mail;


public class MailTemplate {

    private final String subject;
    private final String body;
    private final MailTemplateType type;

    public MailTemplate(MailTemplateType type, String subject, String body) {
        this.type = type;
        this.subject = subject;
        this.body = body;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public MailTemplateType getType() {
        return type;
    }
}
