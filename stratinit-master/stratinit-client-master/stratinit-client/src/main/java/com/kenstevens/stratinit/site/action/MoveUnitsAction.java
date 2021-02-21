package com.kenstevens.stratinit.site.action;

import com.kenstevens.stratinit.control.selection.SelectEvent;
import com.kenstevens.stratinit.control.selection.Selection.Source;
import com.kenstevens.stratinit.model.UnitView;
import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.ActionQueue;
import com.kenstevens.stratinit.site.UnitAwareAction;
import com.kenstevens.stratinit.site.command.MoveUnitsCommand;
import com.kenstevens.stratinit.type.SectorCoords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Scope("prototype")
@Component
public class MoveUnitsAction extends Action<MoveUnitsCommand> implements UnitAwareAction {
	@Autowired
	private SelectEvent selectEvent;
	@Autowired
	private ActionQueue actionQueue;

	private final List<UnitView> units;
	private final SectorCoords target;
	private boolean launching = false;

	public MoveUnitsAction(List<UnitView> units, SectorCoords target) {
		this.units = units;
		this.target = target;
		if (units.size() == 1 && units.get(0).isLaunchable()) {
			launching = true;
		}
	}

	protected MoveUnitsCommand buildCommand() {
		return new MoveUnitsCommand(units, target);
	}

	@PostConstruct
	public void initUnitView() {
		for (UnitView unit : units) {
			unit.setInActionQueue(true);
		}
	}

	@Override
	public void postEvents() {
		super.postEvents();
		if (actionQueue.isEmpty()) {
			if (launching) {
				selectEvent.selectSectorCoordsNoFire(target, Source.CANVAS_SELECT);
			} else {
				selectEvent.selectUnitsWithMobNoFire(Source.CANVAS_SELECT);
			}
		}
	}

	public List<UnitView> getUnits() {
		return units;
	}
}
