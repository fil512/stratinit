package com.kenstevens.stratinit.server.rest.request.write;

import com.kenstevens.stratinit.dao.MessageDao;
import com.kenstevens.stratinit.dto.SIMessage;
import com.kenstevens.stratinit.model.Mail;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.rest.helper.PlayerMessageList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class GetMailRequest extends PlayerWriteRequest<List<SIMessage>> {
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private PlayerMessageList playerMessageList;

    @Override
    protected Result<List<SIMessage>> executeWrite() {
        Nation nation = getNation();
        nation.setNewMail(false);
        gameDaoService.merge(nation);
        Iterable<Mail> messages = messageDao.getMail(nation);
        return Result.make(playerMessageList.messagesToSIMessages(messages));
    }

    @Override
    protected int getCommandCost() {
        return 0;
    }
}
