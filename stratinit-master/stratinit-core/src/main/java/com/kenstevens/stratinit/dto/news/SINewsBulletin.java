package com.kenstevens.stratinit.dto.news;

import com.kenstevens.stratinit.model.Mail;

public class SINewsBulletin extends SINewsLog {
	private static final long serialVersionUID = 1L;
	public final String message;

	public SINewsBulletin(Mail bulletin) {
		super(bulletin);
		message = bulletin.getSubject();
	}
}
