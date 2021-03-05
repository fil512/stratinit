package com.kenstevens.stratinit.client.site.action.post;

import com.kenstevens.stratinit.client.control.selection.SelectEvent;
import com.kenstevens.stratinit.client.control.selection.Selection.Source;
import com.kenstevens.stratinit.client.site.ActionQueue;
import com.kenstevens.stratinit.client.site.PostAction;
import com.kenstevens.stratinit.client.site.command.BuildCityCommand;
import com.kenstevens.stratinit.remote.request.SIUnitListJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class BuildCityAction extends PostAction<BuildCityCommand> {
	@Autowired
	private ActionQueue actionQueue;
	@Autowired
	private SelectEvent selectEvent;

	public BuildCityAction(SIUnitListJson request) {
		super(new BuildCityCommand(request));
	}

	@Override
	public void postEvents() {
		super.postEvents();
		if (actionQueue.isEmpty()) {
			selectEvent.reSelectSectorCoords(Source.CANVAS_SELECT);
		}
	}
}