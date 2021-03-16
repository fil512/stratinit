package com.kenstevens.stratinit.client.site.command.post;

import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.client.model.UnitView;
import com.kenstevens.stratinit.client.site.command.get.CedeCommand;
import com.kenstevens.stratinit.client.site.command.get.UnitsToSIUnits;
import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.CedeUnitsJson;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class CedeUnitsCommand extends CedeCommand<CedeUnitsJson> {
	public CedeUnitsCommand(List<UnitView> units, Nation nation) {
		super(new CedeUnitsJson(UnitsToSIUnits.transform(units), nation.getNationId()), buildDescription(units, nation));
	}

	@Override
	public Result<SIUpdate> executePost(CedeUnitsJson request) {
		return stratInitServer.cedeUnits(request);
	}

	public static String buildDescription(List<UnitView> units, Nation nation) {
		Unit unit = units.get(0);
		if (units.size() == 1) {
			return "Give "+unit.toMyString()+" at "+unit.getCoords()+" to "+nation;
		} else {
			return "Give units at "+unit.getCoords()+" to "+nation;
		}
	}
}
