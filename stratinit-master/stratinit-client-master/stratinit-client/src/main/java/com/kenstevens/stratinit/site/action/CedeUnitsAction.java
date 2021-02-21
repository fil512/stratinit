package com.kenstevens.stratinit.site.action;

import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.NationView;
import com.kenstevens.stratinit.model.UnitView;
import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.command.CedeUnitsCommand;
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