package com.kenstevens.stratinit.controller;

import com.kenstevens.stratinit.client.rest.SIRestPaths;
import com.kenstevens.stratinit.dao.MessageDao;
import com.kenstevens.stratinit.dto.SIMessage;
import com.kenstevens.stratinit.dto.news.SINewsLogsDay;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.MessageDaoService;
import com.kenstevens.stratinit.server.rest.request.RequestFactory;
import com.kenstevens.stratinit.server.rest.request.RequestProcessor;
import com.kenstevens.stratinit.server.rest.svc.PlayerMessageList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = SIRestPaths.BASE_PATH)
public class MessageController {
    @Autowired
    private RequestFactory requestFactory;
    @Autowired
    private RequestProcessor requestProcessor;
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private PlayerMessageList playerMessageList;
    @Autowired
    private MessageDaoService messageDaoService;

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
        return requestProcessor.process(nation ->
                playerMessageList.messagesToSIMessages(messageDao.getSentMail(nation)));
    }

    @GetMapping(path = SIRestPaths.MESSAGE)
    public Result<List<SIMessage>> getMessages() {
        return requestProcessor.process(nation ->
                playerMessageList.messagesToSIMessages(messageDao.getMessages(nation)));
    }

    @GetMapping(path = SIRestPaths.MESSAGE_ANNOUNCEMENT)
    public Result<List<SIMessage>> getAnnouncements() {
        return requestProcessor.processWithGame(game ->
                playerMessageList.messagesToSIMessages(messageDao.getAnnouncements(game)));
    }

    @GetMapping(path = SIRestPaths.NEWS_LOG)
    public Result<List<SINewsLogsDay>> getNewsLogs() {
        return requestProcessor.processWithGame(game -> messageDaoService.getNewsLogs(game));
    }
}
