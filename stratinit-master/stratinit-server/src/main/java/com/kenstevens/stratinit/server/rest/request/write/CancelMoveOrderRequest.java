package com.kenstevens.stratinit.server.rest.request.write;

import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.UnitDaoService;
import com.kenstevens.stratinit.server.rest.svc.PlayerWorldViewUpdate;
import com.kenstevens.stratinit.type.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class CancelMoveOrderRequest extends PlayerWriteRequest<SIUpdate> {
	private final List<SIUnit> siunits;
	@Autowired
	private UnitDaoService unitDaoService;
	@Autowired
	private UnitDao unitDao;
	@Autowired
	private PlayerWorldViewUpdate playerWorldViewUpdate;

	public CancelMoveOrderRequest(List<SIUnit> siunits) {
		this.siunits = siunits;
	}

	@Override
	protected Result<SIUpdate> executeWrite() {
		Nation nation = getNation();
		Result<None> result = Result.falseInstance();
		for (SIUnit siunit : siunits) {
			result.or(cancelMoveOrder(nation, siunit));
		}
		SIUpdate siupdate = playerWorldViewUpdate.getWorldViewUpdate(nation);

		return new Result<SIUpdate>(result.getMessages(), true,
				siupdate, result.getBattleLogs(), result.isSuccess());
	}

	private Result<None> cancelMoveOrder(Nation nation, SIUnit siunit) {
		int unitId = siunit.id;
		Unit unit = unitDao.findUnit(unitId);
		if (unit == null) {
			return new Result<None>("Unit #" + unitId + " does not exist.", false);
		}
		if (!unit.getNation().equals(nation)) {
			return new Result<None>("You do not own unit #" + unitId, false);
		}
		if (unit.getUnitMove() == null) {
			return new Result<None>("Unit #" + unitId + " does not have a move order.", false);
		}
		return unitDaoService.cancelMoveOrder(unit);
	}

	@Override
	protected int getCommandCost() {
		return Constants.COMMAND_COST;
	}


}
