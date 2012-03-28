package com.kenstevens.stratinit.server.remote.request;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.dto.SINation;
import com.kenstevens.stratinit.server.remote.helper.PlayerNationList;

@Scope("prototype")
@Component
public class GetNationsRequest extends PlayerRequest<List<SINation>> {
	@Autowired
	private PlayerNationList playerNationList;

	@Override
	protected List<SINation> execute() {
		return playerNationList.getNations(getNation(), true);
	}
}
