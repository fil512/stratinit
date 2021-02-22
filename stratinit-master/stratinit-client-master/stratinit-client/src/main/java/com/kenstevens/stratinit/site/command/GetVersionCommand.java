package com.kenstevens.stratinit.site.command;

import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.shell.StatusReporter;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.type.Constants;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
		DefaultArtifactVersion clientVersion = new DefaultArtifactVersion(version);
		DefaultArtifactVersion serverVersion = new DefaultArtifactVersion(Constants.SERVER_VERSION);
		if (clientVersion.compareTo(serverVersion) > 0) {
			statusReporter
					.reportError("WARNING: Expected server version "
							+ Constants.SERVER_VERSION + " got "
							+ db.getServerVersion());
		}
	}
}
