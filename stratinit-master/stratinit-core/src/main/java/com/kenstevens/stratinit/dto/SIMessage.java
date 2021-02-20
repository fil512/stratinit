package com.kenstevens.stratinit.dto;

import com.kenstevens.stratinit.model.Mail;
import com.kenstevens.stratinit.type.Constants;

import java.util.Date;


public class SIMessage implements StratInitDTO {
    private static final long serialVersionUID = 1L;
    public int messageId;
    public int fromNationId = Constants.UNASSIGNED;
    public int toNationId = Constants.UNASSIGNED;
    public String subject;
    public String body;
    public Date date;

    public SIMessage() {
    }
	
	public SIMessage(Mail mail) {
		if (mail.getMessageId() != null) {
			messageId = mail.getMessageId();
		}
		if (mail.getFrom() != null) {
			fromNationId = mail.getFrom().getNationId();
		}
		if (mail.getTo() != null) {
			toNationId = mail.getTo().getNationId();
		}
		date = mail.getDate();
		subject = mail.getSubject();
		body = mail.getBody();
	}
}
