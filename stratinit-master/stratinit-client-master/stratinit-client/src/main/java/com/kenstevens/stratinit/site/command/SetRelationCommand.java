package com.kenstevens.stratinit.site.command;

import com.kenstevens.stratinit.dto.SIRelation;
import com.kenstevens.stratinit.event.NationListArrivedEvent;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.NationView;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.processor.RelationProcessor;
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
		return stratInit.setRelation(nation.getNationId(), relationType);
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
