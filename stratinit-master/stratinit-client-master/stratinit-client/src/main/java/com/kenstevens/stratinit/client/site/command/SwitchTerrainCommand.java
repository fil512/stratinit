package com.kenstevens.stratinit.client.site.command;

import com.kenstevens.stratinit.client.model.UnitView;
import com.kenstevens.stratinit.client.site.Command;
import com.kenstevens.stratinit.client.site.processor.UpdateProcessor;
import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.SIUnitListJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class SwitchTerrainCommand extends Command<SIUpdate>{
	@Autowired
	private UpdateProcessor updateProcessor;

	private final List<UnitView> units;

	public SwitchTerrainCommand(List<UnitView> units) {
		this.units = units;
	}

	@Override
	public Result<SIUpdate> execute() {
		List<SIUnit> siunits = UnitsToSIUnits.transform(units);
		SIUnitListJson request = new SIUnitListJson(siunits);
		return stratInitServer.switchTerrain(request);
	}

	@Override
	public void handleSuccess(SIUpdate siupdate) {
		updateProcessor.process(siupdate, false);
	}

	@Override
	public String getDescription() {
		if (units.size() == 0) {
			return "No unit selected to build with.";
		} else {
			return "Terrain changed at "+units.get(0).getCoords(); 
		}
	}
}
