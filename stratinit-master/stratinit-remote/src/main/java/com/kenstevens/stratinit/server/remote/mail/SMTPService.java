package com.kenstevens.stratinit.server.remote.mail;

public interface SMTPService {
	void sendEmail(String to, String from, String subject, String body);

	void setSmptHostname(String smptHostname);

	String getSmptHostname();

	void disable();

	void sendException(String subject, String stackTrace);
}
