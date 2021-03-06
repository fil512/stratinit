package com.kenstevens.stratinit.client.site.mover;

import com.kenstevens.stratinit.client.model.Data;
import com.kenstevens.stratinit.client.model.SelectedUnits;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.client.model.UnitView;
import com.kenstevens.stratinit.client.shell.StatusReporter;
import com.kenstevens.stratinit.client.site.action.post.ActionFactory;
import com.kenstevens.stratinit.client.util.UnitHelper;
import com.kenstevens.stratinit.move.Movement;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.type.SectorCoords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class UnitMover {
	@Autowired
	private StatusReporter statusReporter;
	@Autowired
	private SelectedUnits selectedUnits;

	@Autowired
	private Data db;
	@Autowired
	private ActionFactory actionFactory;

	public void moveSelectedUnits(final SectorCoords target) {
		if (selectedUnits.isEmpty()) {
			return;
		}
		if (selectedUnits.getCoords().equals(target)) {
			return;
		}
		// Safe copy
		List<UnitView> temp = new ArrayList<UnitView>();
		selectedUnits.copyTo(temp);
		final List<UnitView> units = Collections.unmodifiableList(temp);

		Result<None> canMove = inRange(target, units);
		if (!canMove.isSuccess()) {
			statusReporter.reportError(canMove.toString());
			return;
		}

		actionFactory.moveUnits(units, target);
	}

	protected Result<None> inRange(final SectorCoords target, final List<UnitView> units) {
		Unit longest = getMaxRange(units);
		Movement movement = new Movement(longest, db.getWorld());
		return movement.inMaxRange(longest, target);
	}

	public static UnitView getMaxRange(Iterable<UnitView> units) {
		int range = Integer.MIN_VALUE;
		UnitView longest = null;
		for (UnitView unit : units) {
			if (UnitHelper.maxRange(unit) >= range) {
				range = UnitHelper.maxRange(unit);
				longest = unit;
			}
		}
		return longest;
	}


}
