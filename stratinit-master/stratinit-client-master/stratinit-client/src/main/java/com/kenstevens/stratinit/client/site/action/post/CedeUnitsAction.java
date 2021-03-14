package com.kenstevens.stratinit.client.site.action.post;

import com.kenstevens.stratinit.client.model.NationView;
import com.kenstevens.stratinit.client.model.UnitView;
import com.kenstevens.stratinit.client.site.PostAction;
import com.kenstevens.stratinit.client.site.command.post.CedeUnitsCommand;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class CedeUnitsAction extends PostAction<CedeUnitsCommand> {
    public CedeUnitsAction(List<UnitView> units, NationView nation) {
        super(new CedeUnitsCommand(units, nation));
    }
}