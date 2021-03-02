package com.kenstevens.stratinit.remote.request;

import com.kenstevens.stratinit.type.RelationType;

public class SetRelationJson implements IRestRequestJson {
    public int nationId;
    public RelationType relationType;

    public SetRelationJson() {
    }

    public SetRelationJson(int nationId, RelationType relationType) {
        this.nationId = nationId;
        this.relationType = relationType;
    }
}
