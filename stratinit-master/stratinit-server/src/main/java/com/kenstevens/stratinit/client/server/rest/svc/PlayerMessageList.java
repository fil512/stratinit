package com.kenstevens.stratinit.client.server.rest.svc;

import com.google.common.collect.Streams;
import com.kenstevens.stratinit.client.model.Mail;
import com.kenstevens.stratinit.dto.SIMessage;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlayerMessageList {
    public List<SIMessage> messagesToSIMessages(Iterable<Mail> messages) {
        return Streams.stream(messages).map(SIMessage::new).collect(Collectors.toList());
    }
}
