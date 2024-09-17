package com.kenstevens.stratinit.client.site.action.post;

import com.kenstevens.stratinit.client.model.UnitView;
import com.kenstevens.stratinit.client.site.PostAction;
import com.kenstevens.stratinit.client.site.command.post.CancelMoveOrderCommand;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class CancelMoveOrderAction extends PostAction<CancelMoveOrderCommand> {
	public CancelMoveOrderAction(List<UnitView> units) {
		super(new CancelMoveOrderCommand(units));
	}
}