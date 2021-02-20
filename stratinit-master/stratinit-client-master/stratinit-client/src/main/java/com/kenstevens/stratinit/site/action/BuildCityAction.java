package com.kenstevens.stratinit.site.action;

import com.kenstevens.stratinit.control.selection.SelectEvent;
import com.kenstevens.stratinit.control.selection.Selection.Source;
import com.kenstevens.stratinit.model.UnitView;
import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.ActionQueue;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.command.BuildCityCommand;
import com.kenstevens.stratinit.util.Spring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Scope("prototype")
@Component
public class BuildCityAction extends Action {
	@Autowired
	private Spring spring;
	@Autowired
	private ActionQueue actionQueue;
	@Autowired
	private SelectEvent selectEvent;
	
	private BuildCityCommand buildCityCommand;

	private final List<UnitView> units;

	public BuildCityAction(List<UnitView> units) {
		this.units = units;
	}

	@SuppressWarnings("unused")
	@PostConstruct
	private void initialize() {
		buildCityCommand = spring.autowire(new BuildCityCommand(units));
	}

	@Override
	public Command<? extends Object> getCommand() {
		return buildCityCommand;
	}

	@Override
	public void postEvents() {
		super.postEvents();
		if (actionQueue.isEmpty()) {
			selectEvent.reSelectSectorCoords(Source.CANVAS_SELECT);
		}
	}
}