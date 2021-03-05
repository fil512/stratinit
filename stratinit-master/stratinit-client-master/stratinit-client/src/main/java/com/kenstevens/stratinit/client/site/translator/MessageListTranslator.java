package com.kenstevens.stratinit.client.site.translator;

import com.kenstevens.stratinit.client.model.Mail;
import com.kenstevens.stratinit.client.model.MailView;
import com.kenstevens.stratinit.client.model.Message;
import com.kenstevens.stratinit.dto.SIMessage;
import org.springframework.stereotype.Service;

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
