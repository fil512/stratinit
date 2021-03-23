package com.kenstevens.stratinit.client.site.processor;

import com.kenstevens.stratinit.client.event.MessageListArrivedEvent;
import com.kenstevens.stratinit.client.event.NationListArrivedEvent;
import com.kenstevens.stratinit.client.model.MessageList;
import com.kenstevens.stratinit.client.site.translator.MessageListTranslator;
import com.kenstevens.stratinit.dto.SIMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
