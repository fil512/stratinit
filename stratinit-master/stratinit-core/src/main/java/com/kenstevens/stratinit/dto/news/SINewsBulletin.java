package com.kenstevens.stratinit.dto.news;

import com.kenstevens.stratinit.client.model.Mail;

public class SINewsBulletin extends SINewsLog {
	private static final long serialVersionUID = 1L;
	public String message;

	public SINewsBulletin() {
	}

	public SINewsBulletin(Mail bulletin) {
		super(bulletin);
		message = bulletin.getSubject();
	}
}
