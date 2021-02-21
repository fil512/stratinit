package com.kenstevens.stratinit.site.action;

import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.command.ConcedeCommand;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class ConcedeAction extends Action<ConcedeCommand> {
	protected ConcedeCommand buildCommand() {
		return new ConcedeCommand();
	}
}