package com.kenstevens.stratinit.client.site.action.post;

import com.kenstevens.stratinit.client.site.GetAction;
import com.kenstevens.stratinit.client.site.command.get.GetUpdateCommand;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class GetUpdateAction extends GetAction<GetUpdateCommand> {
	public GetUpdateAction(boolean firstTime) {
		super(new GetUpdateCommand(firstTime));
	}

	@Override
	public boolean canRepeat() {
		return false;
	}
}