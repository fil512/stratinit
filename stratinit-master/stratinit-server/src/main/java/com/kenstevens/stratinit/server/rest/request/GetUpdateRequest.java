package com.kenstevens.stratinit.server.rest.request;

import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.server.rest.svc.PlayerWorldViewUpdate;
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
