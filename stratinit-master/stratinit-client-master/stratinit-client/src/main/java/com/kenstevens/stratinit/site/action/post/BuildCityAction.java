package com.kenstevens.stratinit.site.action.post;

import com.kenstevens.stratinit.control.selection.SelectEvent;
import com.kenstevens.stratinit.control.selection.Selection.Source;
import com.kenstevens.stratinit.remote.request.SIUnitListJson;
import com.kenstevens.stratinit.site.ActionQueue;
import com.kenstevens.stratinit.site.PostAction;
import com.kenstevens.stratinit.site.command.BuildCityCommand;
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