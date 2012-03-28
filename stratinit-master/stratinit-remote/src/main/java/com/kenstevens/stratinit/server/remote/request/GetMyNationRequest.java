package com.kenstevens.stratinit.server.remote.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.dto.SINation;
import com.kenstevens.stratinit.server.remote.helper.PlayerNationList;

@Scope("prototype")
@Component
public class GetMyNationRequest extends PlayerRequest<SINation> {
	@Autowired
	private PlayerNationList playerNationList;

	@Override
	protected SINation execute() {
		return playerNationList.getMyNation(getNation());
	}
}
