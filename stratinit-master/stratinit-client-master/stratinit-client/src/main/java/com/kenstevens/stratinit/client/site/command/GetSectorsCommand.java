package com.kenstevens.stratinit.client.site.command;

import com.kenstevens.stratinit.client.event.WorldArrivedEvent;
import com.kenstevens.stratinit.client.model.Account;
import com.kenstevens.stratinit.client.site.Command;
import com.kenstevens.stratinit.client.site.processor.FogOfWar;
import com.kenstevens.stratinit.client.site.processor.SectorListProcessor;
import com.kenstevens.stratinit.dto.SISector;
import com.kenstevens.stratinit.remote.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

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
        return stratInitServer.getSectors();
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
