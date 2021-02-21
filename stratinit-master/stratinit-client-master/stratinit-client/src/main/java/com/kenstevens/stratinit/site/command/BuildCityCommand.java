package com.kenstevens.stratinit.site.command;

import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.model.UnitView;
import com.kenstevens.stratinit.remote.SIResponseEntity;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.processor.UpdateProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class BuildCityCommand extends Command<SIUpdate> {
	@Autowired
	private UpdateProcessor updateProcessor;

	private final List<UnitView> units;

	public BuildCityCommand(List<UnitView> units) {
		this.units = units;
	}

	@Override
	public SIResponseEntity<SIUpdate> execute() {
		List<SIUnit> siunits = UnitsToSIUnits.transform(units);
		return stratInit.buildCity(siunits);
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
			return "New city built at "+units.get(0).getCoords(); 
		}
	}
}
