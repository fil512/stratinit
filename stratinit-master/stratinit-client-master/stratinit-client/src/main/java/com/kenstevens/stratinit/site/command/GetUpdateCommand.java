package com.kenstevens.stratinit.site.command;

import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.remote.SIResponseEntity;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.processor.UpdateProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class GetUpdateCommand extends Command<SIUpdate> {
	@Autowired
	private UpdateProcessor updateProcessor;
	private final boolean firstTime;

	public GetUpdateCommand(boolean firstTime) {
		this.firstTime = firstTime;
	}

    @Override
    public SIResponseEntity<SIUpdate> execute() {
        return stratInit.getUpdate();
    }

	@Override
	public String getDescription() {
		return "Get Update";
	}

	@Override
	public void handleSuccess(SIUpdate update) {
		updateProcessor.process(update, firstTime);
	}

}
