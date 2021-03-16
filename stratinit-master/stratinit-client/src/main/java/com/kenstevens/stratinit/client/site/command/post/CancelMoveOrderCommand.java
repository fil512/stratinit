package com.kenstevens.stratinit.client.site.command.post;

import com.kenstevens.stratinit.client.model.UnitView;
import com.kenstevens.stratinit.client.site.PostCommand;
import com.kenstevens.stratinit.client.site.command.get.UnitsToSIUnits;
import com.kenstevens.stratinit.client.site.processor.UpdateProcessor;
import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.SIUnitListJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class CancelMoveOrderCommand extends PostCommand<SIUpdate, SIUnitListJson> {
	@Autowired
	private UpdateProcessor updateProcessor;

	public CancelMoveOrderCommand(List<UnitView> units) {
		super(new SIUnitListJson(UnitsToSIUnits.transform(units)), buildDescription(units));
	}

	@Override
	public Result<SIUpdate> executePost(SIUnitListJson request) {
		return stratInitServer.cancelMove(request);
	}

	@Override
	public void handleSuccess(SIUpdate siupdate) {
		updateProcessor.process(siupdate, false);
	}

	public static String buildDescription(List<UnitView> units) {
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
