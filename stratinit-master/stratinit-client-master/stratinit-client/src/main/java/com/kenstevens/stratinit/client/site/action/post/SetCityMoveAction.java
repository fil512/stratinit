package com.kenstevens.stratinit.client.site.action.post;

import com.kenstevens.stratinit.client.control.selection.SelectEvent;
import com.kenstevens.stratinit.client.control.selection.Selection.Source;
import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.client.site.Action;
import com.kenstevens.stratinit.client.site.ActionQueue;
import com.kenstevens.stratinit.client.site.command.SetCityMoveCommand;
import com.kenstevens.stratinit.type.SectorCoords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class SetCityMoveAction extends Action<SetCityMoveCommand> {
	@Autowired
	private ActionQueue actionQueue;
	@Autowired
	private SelectEvent selectEvent;

	private final City city;
	private final SectorCoords coords;

	public SetCityMoveAction(City city, SectorCoords coords) {
		this.city = city;
		this.coords = coords;
	}

	protected SetCityMoveCommand buildCommand() {
		return new SetCityMoveCommand(city, coords);
	}

	@Override
	public void postEvents() {
		super.postEvents();
		if (actionQueue.isEmpty()) {
			selectEvent.reSelectSectorCoords(Source.CANVAS_SELECT);
		}
	}

}