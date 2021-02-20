package com.kenstevens.stratinit.server.remote.helper;

import com.google.common.collect.Streams;
import com.kenstevens.stratinit.dto.SIMessage;
import com.kenstevens.stratinit.model.Mail;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlayerMessageList {
	public List<SIMessage> messagesToSIMessages(Iterable<Mail> messages) {
		return Streams.stream(messages).map(SIMessage::new).collect(Collectors.toList());
	}
}
