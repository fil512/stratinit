package com.kenstevens.stratinit.server.rest.mail;

import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.type.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SMTPService smtpService;

    private String from = Constants.EMAIL_FROM_ADDRESS;

    public String getFrom() {
        return from;
    }

    @Override
    public void setFrom(String from) {
        this.from = from;
    }

    @Override
    public void sendEmail(Player player, MailTemplate template) {
        logger.info("Sending " + template.getType() + " email to " + player.getUsername());
        smtpService.sendEmail(player.getEmail(), from, template.getSubject(), template.getBody());
    }
}
