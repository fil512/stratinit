package com.kenstevens.stratinit.site.command;

import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.model.UnitView;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.processor.UpdateProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class CancelMoveOrderCommand extends Command<SIUpdate>{
	@Autowired
	private UpdateProcessor updateProcessor;

	private final List<UnitView> units;

	public CancelMoveOrderCommand(List<UnitView> units) {
		this.units = units;
	}

	@Override
	public Result<SIUpdate> execute() {
        List<SIUnit> siunits = UnitsToSIUnits.transform(units);
        return stratInitServer.cancelMoveOrder(siunits);
    }

	@Override
	public void handleSuccess(SIUpdate siupdate) {
		updateProcessor.process(siupdate, false);
	}

	@Override
	public String getDescription() {
		if (units.size() == 0) {
			return "No units to cancel move on";
		} else if (units.size() == 1) {
			UnitView unit = units.get(0);
				return "Cancelled move order on "+unit.toMyString()+" at "+unit.getCoords();
		} else {
			return "Cancelled move order on "+units.size() + " units at "+units.get(0).getCoords(); 
		}
	}
}
