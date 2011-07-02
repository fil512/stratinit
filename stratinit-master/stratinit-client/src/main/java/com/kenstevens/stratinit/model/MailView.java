package com.kenstevens.stratinit.model;

import com.kenstevens.stratinit.dto.SIMessage;

public class MailView extends Mail {

	public MailView(SIMessage simessage) {
		this.setDate(simessage.date);
		this.setSubject(simessage.subject);
		this.setBody(simessage.body);
		this.setMessageId(simessage.messageId);
	}
}
