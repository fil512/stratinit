package com.kenstevens.stratinit.site.action.get;

import com.kenstevens.stratinit.site.GetAction;
import com.kenstevens.stratinit.site.command.GetNationsCommand;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class GetNationsAction extends GetAction<GetNationsCommand> {
	protected GetNationsAction() {
		super(new GetNationsCommand());
	}
}