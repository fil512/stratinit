package com.kenstevens.stratinit.site.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.processor.UpdateProcessor;

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
