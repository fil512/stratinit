package com.kenstevens.stratinit.remote.request;

import jakarta.validation.constraints.Positive;

public class SetGameJson implements IRestRequestJson {
    @Positive
    public int gameId;
    public boolean noAlliances;

    public SetGameJson() {
    }

    public SetGameJson(int gameId, boolean noAlliances) {
        this.gameId = gameId;
        this.noAlliances = noAlliances;
    }
}
