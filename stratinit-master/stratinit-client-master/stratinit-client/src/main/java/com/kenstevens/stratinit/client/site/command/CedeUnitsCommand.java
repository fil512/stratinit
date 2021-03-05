package com.kenstevens.stratinit.client.site.command;

import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.client.model.UnitView;
import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.CedeUnitsJson;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class CedeUnitsCommand extends CedeCommand {
	private final List<UnitView> units;

	public CedeUnitsCommand(List<UnitView> units, Nation nation) {
		super(nation);
		this.units = units;
	}

	@Override
	public Result<SIUpdate> execute() {
		List<SIUnit> siunits = UnitsToSIUnits.transform(units);
		CedeUnitsJson request = new CedeUnitsJson(siunits, nation.getNationId());
		return stratInitServer.cedeUnits(request);
	}

	@Override
	public String getDescription() {
		Unit unit = units.get(0);
		if (units.size() == 1) {
			return "Give "+unit.toMyString()+" at "+unit.getCoords()+" to "+nation;
		} else {
			return "Give units at "+unit.getCoords()+" to "+nation;
		}
	}
}
