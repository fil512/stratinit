package com.kenstevens.stratinit.client.site.action.post;

import com.kenstevens.stratinit.client.model.UnitView;
import com.kenstevens.stratinit.client.site.PostAction;
import com.kenstevens.stratinit.client.site.command.post.DisbandCommand;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class DisbandAction extends PostAction<DisbandCommand> {
	public DisbandAction(List<UnitView> units) {
		super(new DisbandCommand(units));
	}
}