package com.kenstevens.stratinit.site.action.post;

import com.kenstevens.stratinit.control.selection.SelectEvent;
import com.kenstevens.stratinit.control.selection.Selection.Source;
import com.kenstevens.stratinit.model.UnitView;
import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.ActionQueue;
import com.kenstevens.stratinit.site.command.BuildCityCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class BuildCityAction extends Action<BuildCityCommand> {
	@Autowired
	private ActionQueue actionQueue;
	@Autowired
	private SelectEvent selectEvent;

	private final List<UnitView> units;

	public BuildCityAction(List<UnitView> units) {
		this.units = units;
	}

	protected BuildCityCommand buildCommand() {
		return new BuildCityCommand(units);
	}

	@Override
	public void postEvents() {
		super.postEvents();
		if (actionQueue.isEmpty()) {
			selectEvent.reSelectSectorCoords(Source.CANVAS_SELECT);
		}
	}
}