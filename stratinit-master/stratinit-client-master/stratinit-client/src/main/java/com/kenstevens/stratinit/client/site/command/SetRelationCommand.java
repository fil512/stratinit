package com.kenstevens.stratinit.client.site.command;

import com.kenstevens.stratinit.client.event.NationListArrivedEvent;
import com.kenstevens.stratinit.client.model.Data;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.NationView;
import com.kenstevens.stratinit.client.site.Command;
import com.kenstevens.stratinit.client.site.processor.RelationProcessor;
import com.kenstevens.stratinit.dto.SIRelation;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.SetRelationJson;
import com.kenstevens.stratinit.type.RelationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class SetRelationCommand extends Command<SIRelation> {
	private final Nation nation;
	private final RelationType relationType;

	@Autowired
	private Data db;
	@Autowired
	private RelationProcessor relationProcessor;

	public SetRelationCommand(Nation nation, RelationType relationType) {
		this.nation = nation;
		this.relationType = relationType;
	}

	@Override
	public Result<SIRelation> execute() {
		SetRelationJson request = new SetRelationJson(nation.getNationId(), relationType);
		return stratInitServer.setRelation(request);
	}

	@Override
	public String getDescription() {
		return "Setting relation with "+nation+" to "+relationType;
	}

	@Override
	public void handleSuccess(SIRelation sirelation) {
		// TODO REF could we just use nation here?
		NationView nationView = db.getNation();
		relationProcessor.process(nationView, sirelation);
		arrivedDataEventAccumulator.addEvent(new NationListArrivedEvent());
	}
}
