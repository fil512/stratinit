package com.kenstevens.stratinit.server.remote.mail;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.type.Constants;

@Service
public class MailServiceImpl implements MailService {
	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private SMTPService smtpService;

	private String from = Constants.EMAIL_FROM_ADDRESS;

	@Override
	public void setFrom(String from) {
		this.from = from;
	}

	public String getFrom() {
		return from;
	}

	@Override
	public void sendEmail(Player player, MailTemplate template) {
		logger.info("Sending "+template.getType()+" email to "+player.getUsername());
		smtpService.sendEmail(player.getEmail(), from, template.getSubject(), template.getBody());
	}
}
