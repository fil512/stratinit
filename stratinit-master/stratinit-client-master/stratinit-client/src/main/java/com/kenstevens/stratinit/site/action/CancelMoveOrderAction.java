package com.kenstevens.stratinit.site.action;

import com.kenstevens.stratinit.model.UnitView;
import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.command.CancelMoveOrderCommand;
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