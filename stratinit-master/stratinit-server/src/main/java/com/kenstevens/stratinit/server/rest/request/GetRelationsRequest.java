package com.kenstevens.stratinit.server.rest.request;

import com.kenstevens.stratinit.dto.SIRelation;
import com.kenstevens.stratinit.server.rest.svc.RelationSvc;
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
