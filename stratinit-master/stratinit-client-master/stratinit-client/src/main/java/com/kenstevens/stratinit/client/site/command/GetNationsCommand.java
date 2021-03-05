package com.kenstevens.stratinit.client.site.command;

import com.kenstevens.stratinit.client.site.Command;
import com.kenstevens.stratinit.client.site.processor.NationListProcessor;
import com.kenstevens.stratinit.dto.SINation;
import com.kenstevens.stratinit.remote.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class GetNationsCommand extends Command<List<SINation>> {
	@Autowired
	private NationListProcessor nationListProcessor;

	@Override
	public Result<List<SINation>> execute() {
        return stratInitServer.getNations();
    }

	@Override
	public String getDescription() {
		return "Get nations";
	}

	@Override
	public void handleSuccess(List<SINation> sinations) {
		nationListProcessor.process(sinations);
	}
}
