package com.kenstevens.stratinit.server.remote.mail;

import com.kenstevens.stratinit.type.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class SMTPServiceImpl implements SMTPService {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${stratinit.email.hostname}")
	private String smptHostname;

	@Value("${stratinit.email.enabled}")
	private boolean enabled;

	public void sendEmail(String to, String from, String subject, String body) {
		if (!enabled) {
			return;
		}
		//Get system properties
		Properties props = System.getProperties();

		//Specify the desired SMTP server
		props.put("mail.smtp.host", smptHostname);

        // create a new Session object
        Session session = Session.getInstance(props,null);

        // create a new MimeMessage object (using the Session created above)
        Message message = new MimeMessage(session);
        try {
			message.setFrom(new InternetAddress(from));
			message.setRecipients(Message.RecipientType.TO, new InternetAddress[] { new InternetAddress(to) });
			message.setSubject(subject);
			message.setContent(body, "text/plain");
//			session.setDebug(true);
			Transport.send(message);
		} catch (Exception e) {
			logger.error("Unable to send e-mail to "+to, e);
		}
	}

	public void setSmptHostname(String smptHostname) {
		this.smptHostname = smptHostname;
	}

	public String getSmptHostname() {
		return smptHostname;
	}


	public void disable() {
		enabled  = false;
	}

	@Override
	public void sendException(String subject, String stackTrace) {
		sendEmail(Constants.EMAIL_ADMIN_ADDRESS,
				Constants.EMAIL_FROM_ADDRESS, subject,
				stackTrace);
	}
}
