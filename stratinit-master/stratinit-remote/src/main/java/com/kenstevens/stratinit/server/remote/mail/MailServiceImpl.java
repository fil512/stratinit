package com.kenstevens.stratinit.server.remote.mail;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.type.Constants;

@Service
public class MailServiceImpl implements MailService {
	private final Log log = LogFactory.getLog(getClass());

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
		log.info("Sending "+template.getType()+" email to "+player.getUsername());
		smtpService.sendEmail(player.getEmail(), from, template.getSubject(), template.getBody());
	}
}
