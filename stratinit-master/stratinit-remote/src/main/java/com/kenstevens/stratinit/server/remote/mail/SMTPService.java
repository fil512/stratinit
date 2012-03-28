package com.kenstevens.stratinit.server.remote.mail;

public interface SMTPService {
	public abstract void sendEmail(String to, String from, String subject, String body);

	public abstract void setSmptHostname(String smptHostname);

	public abstract String getSmptHostname();

	public abstract void disable();

	public abstract void sendException(String subject, String stackTrace);
}
