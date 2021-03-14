package com.kenstevens.stratinit.client.site.action.post;

import com.kenstevens.stratinit.client.control.selection.SelectEvent;
import com.kenstevens.stratinit.client.control.selection.Selection.Source;
import com.kenstevens.stratinit.client.model.UnitView;
import com.kenstevens.stratinit.client.model.WorldSector;
import com.kenstevens.stratinit.client.site.ActionQueue;
import com.kenstevens.stratinit.client.site.PostAction;
import com.kenstevens.stratinit.client.site.UnitListAction;
import com.kenstevens.stratinit.client.site.command.post.MoveUnitsCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class MoveUnitsAction extends PostAction<MoveUnitsCommand> implements UnitListAction {
	@Autowired
	private SelectEvent selectEvent;
	@Autowired
	private ActionQueue actionQueue;

	private boolean launching = false;

	public MoveUnitsAction(List<UnitView> units, WorldSector targetSector) {
		super(new MoveUnitsCommand(units, targetSector));
		if (units.size() == 1 && units.get(0).isLaunchable()) {
			launching = true;
		}
		units.forEach(u -> u.setInActionQueue(true));
	}

	@Override
	public void postEvents() {
		super.postEvents();
		if (actionQueue.isEmpty()) {
			if (launching) {
				selectEvent.selectSectorCoordsNoFire(getCommand().getTarget(), Source.CANVAS_SELECT);
			} else {
				selectEvent.selectUnitsWithMobNoFire(Source.CANVAS_SELECT);
			}
		}
	}

	@Override
	public boolean containsUnitId(Integer unitId) {
		return getCommand().containsUnitId(unitId);
	}
}
