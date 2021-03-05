package com.kenstevens.stratinit.client.site.action.post;

import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.NationView;
import com.kenstevens.stratinit.client.model.UnitView;
import com.kenstevens.stratinit.client.site.Action;
import com.kenstevens.stratinit.client.site.command.CedeUnitsCommand;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class CedeUnitsAction extends Action<CedeUnitsCommand> {
	private final List<UnitView> units;
	private final Nation nation;

	public CedeUnitsAction(List<UnitView> units, NationView nation) {
		this.units = units;
		this.nation = nation;
	}

	protected CedeUnitsCommand buildCommand() {
		return new CedeUnitsCommand(
				units, nation);
	}
}