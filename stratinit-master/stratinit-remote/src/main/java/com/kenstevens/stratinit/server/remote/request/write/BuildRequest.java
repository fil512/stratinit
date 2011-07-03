package com.kenstevens.stratinit.server.remote.request.write;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.UnitDaoService;
import com.kenstevens.stratinit.server.remote.helper.PlayerUnitList;
import com.kenstevens.stratinit.server.remote.helper.PlayerWorldViewUpdate;
import com.kenstevens.stratinit.type.UnitType;

public abstract class BuildRequest extends PlayerWriteRequest<SIUpdate> {
	@Autowired
	protected UnitDaoService unitDaoService;
	@Autowired
	protected UnitDao unitDao;
	@Autowired
	private PlayerWorldViewUpdate playerWorldViewUpdate;
	@Autowired
	private PlayerUnitList playerUnitList;

	private final List<SIUnit> siunits;

	public BuildRequest(List<SIUnit> siunits) {
		this.siunits = siunits;
	}

	@Override
	protected Result<SIUpdate> executeWrite() {
		Nation nation = getNation();
		Result<None> result = Result.falseInstance();
		boolean tried = false;
		List<Unit> units = playerUnitList.siunitToUnit(siunits);
		for (Unit unit : units) {
			if (canBuild(unit)) {
				tried = true;
				result.or(buildAction(nation, unit));
				if (result.isSuccess()) {
					break;
				}
			}
		}
		if (!tried) {
			List<String> unitsSelected = Lists.newArrayList();
			for (Unit unit : units) {
				unitsSelected.add(unit.getType() + " " +unit.getMobility()+" mob");
			}
			String unitsSelectedString = StringUtils.join(unitsSelected, ", ");
			result.setMessage("No engineer selected with at least "+mobilityCost()+" mobility.  Units Selected: ["+unitsSelectedString+"]");
		}
		SIUpdate siupdate = playerWorldViewUpdate.getWorldViewUpdate(nation);

		return new Result<SIUpdate>(result.getMessages(), result.isSuccess(),
				siupdate);
	}

	private boolean canBuild(Unit unit) {
		return unit.getType() == UnitType.ENGINEER && unit.getMobility() >= mobilityCost();
	}

	protected Result<None> buildAction(Nation nation, Unit unit) {
		if (unit == null) {
			return new Result<None>("Unit does not exist.", false);
		}
		int unitId = unit.getId();
		if (!unit.getNation().equals(nation)) {
			return new Result<None>("You do not own unit #"+unitId, false);
		}
		if (!unit.isEngineer()) {
			return new Result<None>("Only engineers may build a new city.  Unit #"+unitId+" is a "+unit.getType(), false);
		}
		if (unit.getMobility() < mobilityCost()) {
			return new Result<None>("An engineer must have "+mobilityCost()+" to build a new city.  Engineer #"+unitId+" only has "+unit.getMobility(), false);
		}
		return buildIt(unit);
	}
	protected abstract Result<None> buildIt(Unit unit);

	protected abstract int mobilityCost();
}
