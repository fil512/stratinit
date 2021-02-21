package com.kenstevens.stratinit.site.command;

import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.SIResponseEntity;
import com.kenstevens.stratinit.site.Command;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class DescriptionCommand extends Command<None> {
	private final String description;

	public DescriptionCommand(String description) {
		this.description = description;
	}


	@Override
	public String getDescription() {
		return description;
	}


	@Override
	public SIResponseEntity<None> execute() {
		return SIResponseEntity.trueInstance();
	}


	@Override
	public void handleSuccess(None result) {
	}

}
