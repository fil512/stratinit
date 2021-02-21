package com.kenstevens.stratinit.site.command;

import com.kenstevens.stratinit.dto.SINation;
import com.kenstevens.stratinit.remote.SIResponseEntity;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.processor.NationListProcessor;
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
	public SIResponseEntity<List<SINation>> execute() {
		return stratInit.getNations();
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
