package com.kenstevens.stratinit.server.rest.request;

import com.kenstevens.stratinit.dto.SINation;
import com.kenstevens.stratinit.server.rest.svc.NationSvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class GetNationsRequest extends PlayerRequest<List<SINation>> {
    @Autowired
    private NationSvc nationSvc;

	@Override
	protected List<SINation> execute() {
        return nationSvc.getNations(getNation(), true);
    }
}
