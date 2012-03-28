package com.kenstevens.stratinit.server.remote.request.write;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dto.SIMessage;
import com.kenstevens.stratinit.model.Mail;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.MessageDaoService;
import com.kenstevens.stratinit.type.Constants;

@Scope("prototype")
@Component
public class SendMessageRequest extends PlayerWriteRequest<Integer> {
	@Autowired
	private GameDao gameDao;
	@Autowired
	private MessageDaoService messageDaoService;

	private final SIMessage simessage;

	public SendMessageRequest(SIMessage simessage) {
		this.simessage = simessage;
	}

	@Override
	protected Result<Integer> executeWrite() {
		Nation nation = getNation();
		Nation to = null;
		if (simessage.toNationId != Constants.UNASSIGNED) {
			to = gameDao.getNation(nation.getGameId(), simessage.toNationId);
		}

		Mail mail = messageDaoService.sendMail(nation, to, simessage.subject,
				simessage.body);
		return new Result<Integer>(Integer.valueOf(mail.getMessageId()));
	}

	@Override
	protected int getCommandCost() {
		return 0;
	}
}
