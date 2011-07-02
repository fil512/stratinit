package com.kenstevens.stratinit.site.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.ui.shell.StatusReporter;

@Scope("prototype")
@Component
public class GetVersionCommand extends Command<String> {
	@Autowired
	private Data db;
	@Autowired
	private StatusReporter statusReporter;

	@Override
	public Result<String> execute() {
		return stratInit.getVersion();
	}

	@Override
	public String getDescription() {
		return "Get nations";
	}

	@Override
	public void handleSuccess(String version) {
		db.setServerVersion(version);
		if (!db.getServerVersion().equals(
				Constants.SERVER_VERSION)) {
			statusReporter
					.reportError("WARNING: Expected server version "
							+ Constants.SERVER_VERSION + " got "
							+ db.getServerVersion());
		}
	}
}
