package com.kenstevens.stratinit.client.site.command;

import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.site.Command;
import com.kenstevens.stratinit.client.site.processor.UpdateProcessor;
import com.kenstevens.stratinit.dto.SIUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public abstract class CedeCommand extends Command<SIUpdate> {
	@Autowired
	private UpdateProcessor updateProcessor;
	
	protected final Nation nation;
	
	public CedeCommand(Nation nation) {
		this.nation = nation;
	}

	@Override
	public void handleSuccess(SIUpdate siupdate) {
		updateProcessor.process(siupdate, false);
	}
}
