package com.kenstevens.stratinit.site.action.get;

import com.kenstevens.stratinit.site.GetAction;
import com.kenstevens.stratinit.site.command.GetNewsCommand;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class GetNewsAction extends GetAction<GetNewsCommand> {
	protected GetNewsAction() {
		super(new GetNewsCommand());
	}
}