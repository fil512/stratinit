package com.kenstevens.stratinit.client.site.action.post;

import com.kenstevens.stratinit.client.model.UnitView;
import com.kenstevens.stratinit.client.site.PostAction;
import com.kenstevens.stratinit.client.site.command.post.SwitchTerrainCommand;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class SwitchTerrainAction extends PostAction<SwitchTerrainCommand> {
	public SwitchTerrainAction(List<UnitView> units) {
		super(new SwitchTerrainCommand(units));
	}
}