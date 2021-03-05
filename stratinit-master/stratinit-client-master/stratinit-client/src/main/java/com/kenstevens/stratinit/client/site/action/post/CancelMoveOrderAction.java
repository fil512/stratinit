package com.kenstevens.stratinit.client.site.action.post;

import com.kenstevens.stratinit.client.model.UnitView;
import com.kenstevens.stratinit.client.site.Action;
import com.kenstevens.stratinit.client.site.command.CancelMoveOrderCommand;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class CancelMoveOrderAction extends Action<CancelMoveOrderCommand> {
	private final List<UnitView> units;

	public CancelMoveOrderAction(List<UnitView> units) {
		this.units = units;
	}

	protected CancelMoveOrderCommand buildCommand() {
		return new CancelMoveOrderCommand(units);
	}
}