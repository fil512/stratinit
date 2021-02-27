package com.kenstevens.stratinit.server.rest.request;

import com.kenstevens.stratinit.dto.SINation;
import com.kenstevens.stratinit.server.rest.helper.PlayerNationList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
