package com.kenstevens.stratinit.client.site.action.post;

import com.kenstevens.stratinit.client.model.UnitView;
import com.kenstevens.stratinit.client.site.Action;
import com.kenstevens.stratinit.client.site.command.SwitchTerrainCommand;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class SwitchTerrainAction extends Action<SwitchTerrainCommand> {
	private final List<UnitView> units;

	public SwitchTerrainAction(List<UnitView> units) {
		this.units = units;
	}

	protected SwitchTerrainCommand buildCommand() {
		return new SwitchTerrainCommand(units);
	}
}