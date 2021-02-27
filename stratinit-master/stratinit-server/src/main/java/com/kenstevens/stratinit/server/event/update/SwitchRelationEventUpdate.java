package com.kenstevens.stratinit.server.event.update;

import com.kenstevens.stratinit.model.RelationPK;
import com.kenstevens.stratinit.server.daoservice.GameDaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class SwitchRelationEventUpdate extends EventUpdate {
    private final RelationPK relationPK;
    @Autowired
    private GameDaoService gameDaoService;

    public SwitchRelationEventUpdate(RelationPK relationPK) {
        this.relationPK = relationPK;
    }

    @Override
    protected void executeWrite() {
        gameDaoService.switchRelation(relationPK);
    }
}