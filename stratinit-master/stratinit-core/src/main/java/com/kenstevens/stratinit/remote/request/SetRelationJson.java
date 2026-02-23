package com.kenstevens.stratinit.remote.request;

import com.kenstevens.stratinit.type.RelationType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class SetRelationJson implements IRestRequestJson {
    @Positive
    public int nationId;
    @NotNull
    public RelationType relationType;

    public SetRelationJson() {
    }

    public SetRelationJson(int nationId, RelationType relationType) {
        this.nationId = nationId;
        this.relationType = relationType;
    }
}
