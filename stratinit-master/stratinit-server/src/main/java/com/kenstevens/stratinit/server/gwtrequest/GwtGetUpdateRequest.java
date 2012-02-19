package com.kenstevens.stratinit.server.gwtrequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.client.gwt.model.GWTUpdate;
import com.kenstevens.stratinit.server.remote.request.PlayerRequest;

@Scope("prototype")
@Component()
public class GwtGetUpdateRequest extends PlayerRequest<GWTUpdate>  {
	@Autowired
	private GwtUpdater gwtUpdater;

	@Override
	protected GWTUpdate execute() {
		return gwtUpdater.getUpdate(getNation());
	}
}
