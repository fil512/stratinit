package com.kenstevens.stratinit.site.command;

import com.kenstevens.stratinit.dto.SIRelation;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.processor.RelationListProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class GetRelationsCommand extends Command<List<SIRelation>> {
	@Autowired
	private RelationListProcessor relationListProcessor;

	@Override
	public Result<List<SIRelation>> execute() {
		return stratInit.getRelations();
	}

	@Override
	public String getDescription() {
		return "Get relations";
	}

	@Override
	public void handleSuccess(List<SIRelation> sirelations) {
		relationListProcessor.process(sirelations);
	}
}
