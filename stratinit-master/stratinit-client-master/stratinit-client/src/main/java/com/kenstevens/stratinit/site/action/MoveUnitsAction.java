package com.kenstevens.stratinit.site.action;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.control.selection.SelectEvent;
import com.kenstevens.stratinit.control.selection.Selection.Source;
import com.kenstevens.stratinit.model.UnitView;
import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.ActionQueue;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.UnitAwareAction;
import com.kenstevens.stratinit.site.command.MoveUnitsCommand;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.util.Spring;

@Scope("prototype")
@Component
public class MoveUnitsAction extends Action implements UnitAwareAction {
	@Autowired
	private Spring spring;
	@Autowired
	private SelectEvent selectEvent;
	@Autowired
	private ActionQueue actionQueue;

	private MoveUnitsCommand moveUnitsCommand;

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

	@SuppressWarnings("unused")
	@PostConstruct
	private void initialize() {
		moveUnitsCommand = spring.autowire(new MoveUnitsCommand(units, target));
		for (UnitView unit : units) {
			unit.setInActionQueue(true);
		}
	}

	@Override
	public Command<? extends Object> getCommand() {
		return moveUnitsCommand;
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
