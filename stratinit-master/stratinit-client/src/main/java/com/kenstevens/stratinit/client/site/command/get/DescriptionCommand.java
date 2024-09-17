package com.kenstevens.stratinit.client.site.command.get;

import com.kenstevens.stratinit.client.site.GetCommand;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class DescriptionCommand extends GetCommand<None> {
	private final String description;

	public DescriptionCommand(String description) {
		this.description = description;
	}

	@Override
	public String getDescription() {
		return description;
	}


	@Override
	public Result<None> execute() {
		return Result.trueInstance();
	}


	@Override
	public void handleSuccess(None result) {
	}

}
