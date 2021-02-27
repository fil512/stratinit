package com.kenstevens.stratinit.site.action.get;

import com.kenstevens.stratinit.site.GetAction;
import com.kenstevens.stratinit.site.command.GetMyNationCommand;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class GetMyNationAction extends GetAction<GetMyNationCommand> {
	protected GetMyNationAction() {
		super(new GetMyNationCommand());
	}
}