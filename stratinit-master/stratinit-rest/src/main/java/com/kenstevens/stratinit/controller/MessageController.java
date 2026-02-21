package com.kenstevens.stratinit.controller;

import com.kenstevens.stratinit.client.model.Mail;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.rest.SIRestPaths;
import com.kenstevens.stratinit.dao.MessageDao;
import com.kenstevens.stratinit.dao.NationDao;
import com.kenstevens.stratinit.dto.SIMessage;
import com.kenstevens.stratinit.dto.news.SINewsLogsDay;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.service.GameService;
import com.kenstevens.stratinit.server.service.MessageService;
import com.kenstevens.stratinit.server.rest.request.RequestProcessor;
import com.kenstevens.stratinit.server.rest.request.WriteProcessor;
import com.kenstevens.stratinit.server.rest.svc.PlayerMessageList;
import com.kenstevens.stratinit.type.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = SIRestPaths.BASE_PATH)
public class MessageController {
    @Autowired
    private RequestProcessor requestProcessor;
    @Autowired
    private WriteProcessor writeProcessor;
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private NationDao nationDao;
    @Autowired
    private PlayerMessageList playerMessageList;
    @Autowired
    private MessageService messageService;
    @Autowired
    private GameService gameService;

    @PostMapping(path = SIRestPaths.SEND_MESSAGE)
    public Result<Integer> sendMessage(@RequestBody SIMessage simessage) {
        return writeProcessor.process(nation -> {
            Nation to = null;
            if (simessage.toNationId != Constants.UNASSIGNED) {
                to = nationDao.getNation(nation.getGameId(), simessage.toNationId);
            }
            Mail mail = messageService.sendMail(nation, to, simessage.subject, simessage.body);
            return Result.make(Integer.valueOf(mail.getMessageId()));
        }, 0);
    }

    @GetMapping(path = SIRestPaths.MESSAGE_MAIL)
    public Result<List<SIMessage>> getMail() {
        return writeProcessor.process(nation -> {
            nation.setNewMail(false);
            gameService.merge(nation);
            Iterable<Mail> messages = messageDao.getMail(nation);
            return Result.make(playerMessageList.messagesToSIMessages(messages));
        }, 0);
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
        return requestProcessor.processWithGame(game -> messageService.getNewsLogs(game));
    }
}
