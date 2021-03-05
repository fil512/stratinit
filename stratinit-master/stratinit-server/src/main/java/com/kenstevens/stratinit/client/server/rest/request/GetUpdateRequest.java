package com.kenstevens.stratinit.client.server.rest.request;

import com.kenstevens.stratinit.client.server.rest.svc.PlayerWorldViewUpdate;
import com.kenstevens.stratinit.dto.SIUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class GetUpdateRequest extends PlayerRequest<SIUpdate> {
	@Autowired
	private PlayerWorldViewUpdate playerWorldViewUpdate;

	@Override
	protected SIUpdate execute() {
		return playerWorldViewUpdate.getWorldViewUpdate(getNation());
	}
}
