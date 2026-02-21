package com.kenstevens.stratinit.controller;

import com.kenstevens.stratinit.client.rest.SIRestPaths;
import com.kenstevens.stratinit.dto.SIMessage;
import com.kenstevens.stratinit.dto.news.SINewsLogsDay;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.rest.request.RequestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = SIRestPaths.BASE_PATH)
public class MessageController {
    @Autowired
    private RequestFactory requestFactory;

    @PostMapping(path = SIRestPaths.SEND_MESSAGE)
    public Result<Integer> sendMessage(@RequestBody SIMessage simessage) {
        return requestFactory.getSendMessageRequest(simessage).process();
    }

    @GetMapping(path = SIRestPaths.MESSAGE_MAIL)
    public Result<List<SIMessage>> getMail() {
        return requestFactory.getGetMailRequest().process();
    }

    @GetMapping(path = SIRestPaths.MESSAGE_SENT)
    public Result<List<SIMessage>> getSentMail() {
        return requestFactory.getGetSentMailRequest().process();
    }

    @GetMapping(path = SIRestPaths.MESSAGE)
    public Result<List<SIMessage>> getMessages() {
        return requestFactory.getGetMessagesRequest().process();
    }

    @GetMapping(path = SIRestPaths.MESSAGE_ANNOUNCEMENT)
    public Result<List<SIMessage>> getAnnouncements() {
        return requestFactory.getGetAnnouncementsRequest().process();
    }

    @GetMapping(path = SIRestPaths.NEWS_LOG)
    public Result<List<SINewsLogsDay>> getNewsLogs() {
        return requestFactory.getGetLogsRequest().process();
    }
}
