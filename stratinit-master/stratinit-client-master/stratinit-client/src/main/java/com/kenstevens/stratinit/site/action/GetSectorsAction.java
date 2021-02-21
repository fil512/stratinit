package com.kenstevens.stratinit.site.action;

import com.kenstevens.stratinit.shell.StatusReporter;
import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.command.GetSectorsCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class GetSectorsAction extends Action<GetSectorsCommand> {
	@Autowired
	private StatusReporter statusReporter;

	protected GetSectorsCommand buildCommand() {
		return new GetSectorsCommand();
	}

	@Override
	public void postRequest() {
		statusReporter.reportResult("Map updated.");
	}

	@Override
	public boolean canRepeat() {
		return false;
	}

}