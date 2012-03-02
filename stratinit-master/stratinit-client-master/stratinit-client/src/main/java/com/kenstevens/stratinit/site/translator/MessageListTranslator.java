package com.kenstevens.stratinit.site.translator;

import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.dto.SIMessage;
import com.kenstevens.stratinit.model.Mail;
import com.kenstevens.stratinit.model.MailView;
import com.kenstevens.stratinit.model.Message;

@Service
public class MessageListTranslator extends ListTranslator<SIMessage, Message> {

	@Override
	public Message translate(SIMessage input) {
		Mail mail = new MailView(input);
		mail.setTo(db.getNation(input.toNationId));
		mail.setFrom(db.getNation(input.fromNationId));
		return mail;
	}

}
