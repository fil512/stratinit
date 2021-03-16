package com.kenstevens.stratinit.client.site.action.get;

import com.kenstevens.stratinit.client.site.GetAction;
import com.kenstevens.stratinit.client.site.command.get.GetNewsCommand;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class GetNewsAction extends GetAction<GetNewsCommand> {
	protected GetNewsAction() {
		super(new GetNewsCommand());
	}
}