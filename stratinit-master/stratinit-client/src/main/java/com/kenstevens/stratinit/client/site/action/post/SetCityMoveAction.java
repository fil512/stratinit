package com.kenstevens.stratinit.client.site.action.post;

import com.kenstevens.stratinit.client.api.IEventSelector;
import com.kenstevens.stratinit.client.api.Selection.Source;
import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.client.site.ActionQueue;
import com.kenstevens.stratinit.client.site.PostAction;
import com.kenstevens.stratinit.client.site.command.post.SetCityMoveCommand;
import com.kenstevens.stratinit.type.SectorCoords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class SetCityMoveAction extends PostAction<SetCityMoveCommand> {
	@Autowired
	private ActionQueue actionQueue;
	@Autowired
	private IEventSelector eventSelector;

	public SetCityMoveAction(City city, SectorCoords coords) {
		super(new SetCityMoveCommand(city, coords));
	}

	@Override
	public void postEvents() {
		super.postEvents();
		if (actionQueue.isEmpty()) {
			eventSelector.reSelectSectorCoords(Source.CANVAS_SELECT);
		}
	}

}