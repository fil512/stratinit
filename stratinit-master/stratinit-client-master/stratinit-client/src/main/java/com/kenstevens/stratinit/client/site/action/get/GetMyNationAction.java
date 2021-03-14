package com.kenstevens.stratinit.client.site.action.get;

import com.kenstevens.stratinit.client.site.GetAction;
import com.kenstevens.stratinit.client.site.command.get.GetMyNationCommand;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class GetMyNationAction extends GetAction<GetMyNationCommand> {
	protected GetMyNationAction() {
		super(new GetMyNationCommand());
	}
}