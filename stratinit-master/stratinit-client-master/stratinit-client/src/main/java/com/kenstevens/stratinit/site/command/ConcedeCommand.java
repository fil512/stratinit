package com.kenstevens.stratinit.site.command;

import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.model.NationView;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.processor.UpdateProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class ConcedeCommand extends Command<SIUpdate> {
	@Autowired
	private UpdateProcessor updateProcessor;
	@Autowired
	private Data db;

	public ConcedeCommand() {
	}

	@Override
	public Result<SIUpdate> execute() {
		return stratInit.concede();
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
