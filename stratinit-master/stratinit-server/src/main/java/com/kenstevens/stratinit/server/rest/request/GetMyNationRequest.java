package com.kenstevens.stratinit.server.rest.request;

import com.kenstevens.stratinit.dto.SINation;
import com.kenstevens.stratinit.server.rest.svc.NationSvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class GetMyNationRequest extends PlayerRequest<SINation> {
    @Autowired
    private NationSvc nationSvc;

	@Override
	protected SINation execute() {
        return nationSvc.getMyNation(getNation());
    }
}
