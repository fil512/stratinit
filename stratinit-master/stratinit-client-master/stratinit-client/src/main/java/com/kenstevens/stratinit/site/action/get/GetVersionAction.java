package com.kenstevens.stratinit.site.action.get;

import com.kenstevens.stratinit.site.GetAction;
import com.kenstevens.stratinit.site.command.GetVersionCommand;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class GetVersionAction extends GetAction<GetVersionCommand> {
    public GetVersionAction() {
        super(new GetVersionCommand());
    }
}