package com.kenstevens.stratinit.server.remote.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.server.remote.helper.PlayerWorldViewUpdate;

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
