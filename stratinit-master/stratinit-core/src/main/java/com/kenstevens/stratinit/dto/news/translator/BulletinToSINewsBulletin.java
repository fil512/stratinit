package com.kenstevens.stratinit.dto.news.translator;

import com.google.common.base.Function;
import com.kenstevens.stratinit.client.model.Mail;
import com.kenstevens.stratinit.dto.news.SINewsBulletin;

public class BulletinToSINewsBulletin implements
		Function<Mail, SINewsBulletin> {

	@Override
	public SINewsBulletin apply(Mail bulletin) {
		return new SINewsBulletin(bulletin);
	}

}
