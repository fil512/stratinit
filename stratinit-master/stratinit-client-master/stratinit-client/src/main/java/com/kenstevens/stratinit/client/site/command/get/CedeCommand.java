package com.kenstevens.stratinit.client.site.command.get;

import com.kenstevens.stratinit.client.site.PostCommand;
import com.kenstevens.stratinit.client.site.processor.UpdateProcessor;
import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.remote.request.IRestRequestJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public abstract class CedeCommand<R extends IRestRequestJson> extends PostCommand<SIUpdate, R> {
	@Autowired
	private UpdateProcessor updateProcessor;

	public CedeCommand(R request, String description) {
		super(request, description);
	}

	@Override
	public void handleSuccess(SIUpdate siupdate) {
		updateProcessor.process(siupdate, false);
	}
}
