package com.kenstevens.stratinit.dto.news.translator;

import com.google.common.base.Function;
import com.kenstevens.stratinit.dto.news.SINewsBulletin;
import com.kenstevens.stratinit.model.Mail;

public class BulletinToSINewsBulletin implements
		Function<Mail, SINewsBulletin> {

	@Override
	public SINewsBulletin apply(Mail bulletin) {
		return new SINewsBulletin(bulletin);
	}

}
