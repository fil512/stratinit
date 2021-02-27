package com.kenstevens.stratinit.site.action.get;

import com.kenstevens.stratinit.shell.StatusReporter;
import com.kenstevens.stratinit.site.GetAction;
import com.kenstevens.stratinit.site.command.GetSectorsCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class GetSectorsAction extends GetAction<GetSectorsCommand> {
	@Autowired
	private StatusReporter statusReporter;

	public GetSectorsAction() {
		super(new GetSectorsCommand());
	}

	@Override
	public void postRequest() {
		statusReporter.reportResult("Map updated.");
	}
}