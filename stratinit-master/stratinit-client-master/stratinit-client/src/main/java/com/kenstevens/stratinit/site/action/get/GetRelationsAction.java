package com.kenstevens.stratinit.site.action.get;

import com.kenstevens.stratinit.site.GetAction;
import com.kenstevens.stratinit.site.command.GetRelationsCommand;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class GetRelationsAction extends GetAction<GetRelationsCommand> {
	protected GetRelationsAction() {
		super(new GetRelationsCommand());
	}
}