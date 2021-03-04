package com.kenstevens.stratinit.server.rest.request;

import com.kenstevens.stratinit.dto.SISector;
import com.kenstevens.stratinit.server.rest.svc.PlayerWorldView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

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
