package com.kenstevens.stratinit.server.remote.request.write;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.model.MoveCost;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.MoveService;
import com.kenstevens.stratinit.server.remote.helper.PlayerWorldViewUpdate;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.SectorCoords;

@Scope("prototype")
@Component
public class MoveUnitsRequest extends PlayerWriteRequest<SIUpdate> {
	@Autowired
	private MoveService moveService;
	@Autowired
	private PlayerWorldViewUpdate playerWorldViewUpdate;

	private final List<SIUnit> units;
	private final SectorCoords target;
	private int commandCost = Constants.COMMAND_COST;

	public MoveUnitsRequest(List<SIUnit> units, SectorCoords target) {
		this.units = units;
		this.target = target;
	}

	// Note that move commands are always successful.  We use MoveSuccess to indicate whether the move worked.
	@Override
	protected Result<SIUpdate> executeWrite() {
		Nation nation = getNation();
		Result<MoveCost> result = moveService.move(nation, units, target);
		setCommandCost(result);
		SIUpdate siupdate = playerWorldViewUpdate.getWorldViewUpdate(nation);
		return new Result<SIUpdate>(result.getMessages(), true,
				siupdate, result.getBattleLogs(), result.isSuccess());
	}

	private void setCommandCost(Result<MoveCost> result) {
		MoveCost moveCost = result.getValue();
		if (result != null && moveCost != null) {
			commandCost = moveCost.getCommandCost();
		}
	}

	@Override
	protected int getCommandCost() {
		return commandCost;
	}
}
