package com.kenstevens.stratinit.server.remote.mail;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.server.remote.helper.StackTraceHelper;
import com.kenstevens.stratinit.type.Constants;

@Service
public class SMTPServiceImpl implements SMTPService {
	private Logger logger = Logger.getLogger(getClass());

	private String smptHostname = "localhost";

	private boolean enabled = true;

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
	public void sendException(String subject, Exception exception) {
		sendEmail(Constants.EMAIL_ADMIN_ADDRESS,
				Constants.EMAIL_FROM_ADDRESS, subject,
				StackTraceHelper.getStackTrace(exception));
	}
}
