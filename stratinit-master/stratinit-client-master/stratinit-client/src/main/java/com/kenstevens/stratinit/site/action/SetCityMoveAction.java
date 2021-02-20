package com.kenstevens.stratinit.site.action;

import com.kenstevens.stratinit.control.selection.SelectEvent;
import com.kenstevens.stratinit.control.selection.Selection.Source;
import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.ActionQueue;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.command.SetCityMoveCommand;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.util.Spring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Scope("prototype")
@Component
public class SetCityMoveAction extends Action {
	@Autowired
	private Spring spring;
	@Autowired
	private ActionQueue actionQueue;
	@Autowired
	private SelectEvent selectEvent;
	private SetCityMoveCommand setCityMoveCommand;

	private final City city;
	private final SectorCoords coords;

	public SetCityMoveAction(City city, SectorCoords coords) {
		this.city = city;
		this.coords = coords;
	}

	@SuppressWarnings("unused")
	@PostConstruct
	private void initialize() {
		setCityMoveCommand = spring.autowire(new SetCityMoveCommand(
					city, coords ));
	}

	@Override
	public Command<? extends Object> getCommand() {
		return setCityMoveCommand;
	}
	
	@Override
	public void postEvents() {
		super.postEvents();
		if (actionQueue.isEmpty()) {
			selectEvent.reSelectSectorCoords(Source.CANVAS_SELECT);
		}
	}

}