package com.kenstevens.stratinit.client.server.rest.request;

import com.kenstevens.stratinit.client.server.rest.svc.RelationSvc;
import com.kenstevens.stratinit.dto.SIRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class GetRelationsRequest extends PlayerRequest<List<SIRelation>> {
	@Autowired
	RelationSvc relationSvc;

	@Override
	protected List<SIRelation> execute() {
		return relationSvc.getRelations(getNation());
	}
}
