package com.kenstevens.stratinit.server.rest.mail;

import com.kenstevens.stratinit.type.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

@Lazy
@Service
public class SMTPService {
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
        Session session = Session.getInstance(props, null);

        // create a new MimeMessage object (using the Session created above)
        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, new InternetAddress[]{new InternetAddress(to)});
            message.setSubject(subject);
            message.setContent(body, "text/plain");
//			session.setDebug(true);
            Transport.send(message);
        } catch (Exception e) {
            logger.error("Unable to send e-mail to " + to, e);
        }
    }

    public void sendException(String subject, String stackTrace) {
        sendEmail(Constants.EMAIL_ADMIN_ADDRESS,
                Constants.EMAIL_FROM_ADDRESS, subject,
                stackTrace);
    }
}

