package com.kenstevens.stratinit.server.remote.helper;

import java.util.List;

import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.kenstevens.stratinit.dto.SIMessage;
import com.kenstevens.stratinit.model.Mail;

@Service
public class PlayerMessageList {

	public List<SIMessage> messagesToSIMessages(List<Mail> messages) {
		return Lists.newArrayList(Collections2.transform(messages, new Function<Mail, SIMessage>() {
			@Override
			public SIMessage apply(Mail mail) {
				return new SIMessage(mail);
			}
		}));
	}

}
