package com.kenstevens.stratinit.site.command;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.model.UnitView;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.processor.UpdateProcessor;

@Scope("prototype")
@Component
public class DisbandCommand extends Command<SIUpdate>{
	@Autowired
	private UpdateProcessor updateProcessor;

	private final List<UnitView> units;

	public DisbandCommand(List<UnitView> units) {
		this.units = units;
	}

	@Override
	public Result<SIUpdate> execute() {
		List<SIUnit> siunits = UnitsToSIUnits.transform(units);
		return stratInit.disbandUnits(siunits);
	}

	@Override
	public void handleSuccess(SIUpdate siupdate) {
		updateProcessor.process(siupdate, false);
	}

	@Override
	public String getDescription() {
		if (units.size() == 0) {
			return "Nothing disbanded";
		} else if (units.size() == 1) {
			UnitView unit = units.get(0);
			if (unit.isLand()) {
				return "Disband "+unit.toMyString()+" at "+unit.getCoords();
			} else {
				return "Scuttle "+unit.toMyString()+" at "+unit.getCoords();
			}
		} else {
			return units.size() + " units disbanded at "+units.get(0).getCoords(); 
		}
	}
}
