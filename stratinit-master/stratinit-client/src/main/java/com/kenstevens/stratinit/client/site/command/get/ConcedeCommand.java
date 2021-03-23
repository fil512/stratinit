package com.kenstevens.stratinit.client.site.command.get;

import com.kenstevens.stratinit.client.model.Data;
import com.kenstevens.stratinit.client.model.NationView;
import com.kenstevens.stratinit.client.site.GetCommand;
import com.kenstevens.stratinit.client.site.processor.UpdateProcessor;
import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.remote.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class ConcedeCommand extends GetCommand<SIUpdate> {
	@Autowired
	private UpdateProcessor updateProcessor;
	@Autowired
	private Data db;

	public ConcedeCommand() {
	}

	@Override
	public Result<SIUpdate> execute() {
        return stratInitServer.concede();
    }

	@Override
	public void handleSuccess(SIUpdate siupdate) {
		updateProcessor.process(siupdate, false);
	}

	@Override
	public String getDescription() {
		NationView ally = db.getAlly();
		if (ally == null) {
			return "Concede victory.  Destroy all units and cities.";
		} else {
			return "Concede victory.  Cede all units and cities to "+ally.getName()+".";
		}
	}
}
