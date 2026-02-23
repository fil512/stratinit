package com.kenstevens.stratinit.server.rest.mail;

import com.kenstevens.stratinit.client.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired(required = false)
    private SMTPService smtpService;

    @Autowired
    private EmailProperties emailProperties;

    @Value("${stratinit.email.enabled}")
    private boolean mailEnabled;

    public String getFrom() {
        return emailProperties.getFromAddress();
    }

    public void sendEmail(Player player, MailTemplate template) {
        if (mailEnabled) {
            logger.info("Sending " + template.getType() + " email to " + player.getUsername());
            smtpService.sendEmail(player.getEmail(), emailProperties.getFromAddress(), template.getSubject(), template.getBody());
        } else {
            logger.info("Mail disabled.  Skipping " + template.getType() + " email to " + player.getUsername());
        }
    }
}
