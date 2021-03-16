package com.kenstevens.stratinit.client.site.action.post;

import com.kenstevens.stratinit.client.api.IEventSelector;
import com.kenstevens.stratinit.client.api.Selection.Source;
import com.kenstevens.stratinit.client.model.UnitView;
import com.kenstevens.stratinit.client.site.ActionQueue;
import com.kenstevens.stratinit.client.site.PostAction;
import com.kenstevens.stratinit.client.site.command.post.BuildCityCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class BuildCityAction extends PostAction<BuildCityCommand> {
	@Autowired
	private ActionQueue actionQueue;
	@Autowired
	private IEventSelector iEventSelector;

	public BuildCityAction(List<UnitView> units) {
		super(new BuildCityCommand(units));
	}

	@Override
	public void postEvents() {
		super.postEvents();
		if (actionQueue.isEmpty()) {
			iEventSelector.reSelectSectorCoords(Source.CANVAS_SELECT);
		}
	}
}