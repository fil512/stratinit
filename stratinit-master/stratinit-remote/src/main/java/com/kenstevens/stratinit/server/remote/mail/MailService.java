package com.kenstevens.stratinit.server.remote.mail;

import com.kenstevens.stratinit.model.Player;

public interface MailService {
	public abstract void setFrom(String from);

	public abstract String getFrom();

	public abstract void sendEmail(Player player, MailTemplate template);

}