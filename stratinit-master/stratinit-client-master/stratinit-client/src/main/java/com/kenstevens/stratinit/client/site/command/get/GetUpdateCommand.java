package com.kenstevens.stratinit.client.site.command.get;

import com.kenstevens.stratinit.client.site.GetCommand;
import com.kenstevens.stratinit.client.site.processor.UpdateProcessor;
import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.remote.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class GetUpdateCommand extends GetCommand<SIUpdate> {
    @Autowired
    private UpdateProcessor updateProcessor;
    private final boolean firstTime;

    public GetUpdateCommand(boolean firstTime) {
        this.firstTime = firstTime;
    }

    @Override
    public Result<SIUpdate> execute() {
        return stratInitServer.getUpdate();
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
