package com.kenstevens.stratinit.server.remote.request;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.dto.SISector;
import com.kenstevens.stratinit.server.remote.helper.PlayerWorldView;

@Scope("prototype")
@Component
public class GetSectorsRequest extends PlayerRequest<List<SISector>> {
	@Autowired
	private PlayerWorldView playerWorldView;

	@Override
	protected List<SISector> execute() {
		return playerWorldView.getWorldViewSectors(getNation());
	}
}
