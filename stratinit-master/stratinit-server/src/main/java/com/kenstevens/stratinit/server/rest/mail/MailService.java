package com.kenstevens.stratinit.server.rest.mail;

import com.kenstevens.stratinit.model.Player;

public interface MailService {
    String getFrom();

    void setFrom(String from);

    void sendEmail(Player player, MailTemplate template);

}