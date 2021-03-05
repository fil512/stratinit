package com.kenstevens.stratinit.client.site.action.get;

import com.kenstevens.stratinit.client.site.GetAction;
import com.kenstevens.stratinit.client.site.command.ConcedeCommand;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class ConcedeAction extends GetAction<ConcedeCommand> {
	public ConcedeAction() {
		super(new ConcedeCommand());
	}
}