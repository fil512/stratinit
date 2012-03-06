package com.kenstevens.stratinit.site.command;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.dto.SISector;
import com.kenstevens.stratinit.event.WorldArrivedEvent;
import com.kenstevens.stratinit.model.Account;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.processor.FogOfWar;
import com.kenstevens.stratinit.site.processor.SectorListProcessor;

@Scope("prototype")
@Component
public class GetSectorsCommand extends Command<List<SISector>> {
	@Autowired
	private SectorListProcessor sectorListProcessor;
	@Autowired
	private Account account;
	@Autowired
	private FogOfWar fogOfWar;

	@Override
	public Result<List<SISector>> execute() {
		return stratInit.getSectors();
	}


	@Override
	public String getDescription() {
		return "Get map.";
	}

	@Override
	public void handleSuccess(List<SISector> sisectors) {
		sectorListProcessor.process(sisectors);
		if (account.getPreferences().isShowFOW()) {
			fogOfWar.setFogOfWar();
		}
		arrivedDataEventAccumulator.addEvent(new WorldArrivedEvent());
	}
}