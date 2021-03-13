package com.kenstevens.stratinit.server.rest.request.write;

import com.kenstevens.stratinit.client.model.Mail;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.dao.NationDao;
import com.kenstevens.stratinit.dto.SIMessage;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.MessageDaoService;
import com.kenstevens.stratinit.type.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class SendMessageRequest extends PlayerWriteRequest<Integer> {
	private final SIMessage simessage;
	@Autowired
	private NationDao nationDao;
	@Autowired
	private MessageDaoService messageDaoService;

	public SendMessageRequest(SIMessage simessage) {
		this.simessage = simessage;
	}

	@Override
	protected Result<Integer> executeWrite() {
		Nation nation = getNation();
		Nation to = null;
		if (simessage.toNationId != Constants.UNASSIGNED) {
			to = nationDao.getNation(nation.getGameId(), simessage.toNationId);
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
