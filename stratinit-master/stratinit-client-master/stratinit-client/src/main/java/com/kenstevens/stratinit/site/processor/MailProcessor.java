package com.kenstevens.stratinit.site.processor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.dto.SIMessage;
import com.kenstevens.stratinit.event.MessageListArrivedEvent;
import com.kenstevens.stratinit.event.NationListArrivedEvent;
import com.kenstevens.stratinit.model.MessageList;
import com.kenstevens.stratinit.site.translator.MessageListTranslator;

@Service
public class MailProcessor extends Processor {
	@Autowired
	private MessageListTranslator translator;
	
	public void process(MessageList messageList, List<SIMessage> entries) {
		messageList.addAll(translator.translate(entries));
		arrivedDataEventAccumulator.addEvent(new MessageListArrivedEvent());	
		db.getNation().setNewMail(false);
		arrivedDataEventAccumulator.addEvent(new NationListArrivedEvent());
	}
}
