package com.kenstevens.stratinit.server.remote.request.write;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.UnitDaoService;
import com.kenstevens.stratinit.server.remote.helper.PlayerWorldViewUpdate;
import com.kenstevens.stratinit.type.Constants;

@Scope("prototype")
@Component
public class DisbandUnitRequest extends PlayerWriteRequest<SIUpdate> {
	@Autowired
	private UnitDaoService unitDaoService;
	@Autowired
	private UnitDao unitDao;
	@Autowired
	private PlayerWorldViewUpdate playerWorldViewUpdate;

	private final List<SIUnit> siunits;

	public DisbandUnitRequest(List<SIUnit> siunits) {
		this.siunits = siunits;
	}

	@Override
	protected Result<SIUpdate> executeWrite() {
		Nation nation = getNation();
		Result<None> result = Result.falseInstance();
		for (SIUnit siunit : siunits) {
			result.or(disband(nation, siunit));
		}
		SIUpdate siupdate = playerWorldViewUpdate.getWorldViewUpdate(nation);

		return new Result<SIUpdate>(result.getMessages(), true,
				siupdate, result.getBattleLogs(), result.isSuccess());
	}

	private Result<None> disband(Nation nation, SIUnit siunit) {
		int unitId = siunit.id;
		Unit unit = unitDao.findUnit(unitId);
		if (unit == null) {
			return new Result<None>("Unit #"+unitId+" does not exist.", false);
		}
		if (!unit.getNation().equals(nation)) {
			return new Result<None>("You do not own unit #"+unitId, false);
		}
		return unitDaoService.disbandUnit(unit);
	}

	@Override
	protected int getCommandCost() {
		return Constants.COMMAND_COST;
	}




}
