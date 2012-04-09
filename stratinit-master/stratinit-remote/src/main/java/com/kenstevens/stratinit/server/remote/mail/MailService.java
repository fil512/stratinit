package com.kenstevens.stratinit.server.remote.mail;

import com.kenstevens.stratinit.model.Player;

public interface MailService {
	void setFrom(String from);

	String getFrom();

	void sendEmail(Player player, MailTemplate template);

}