package com.kenstevens.stratinit.remote.request;

public class SetGameJson implements IRestRequestJson {
    public int gameId;
    public boolean noAlliances;

    public SetGameJson() {
    }

    public SetGameJson(int gameId, boolean noAlliances) {
        this.gameId = gameId;
        this.noAlliances = noAlliances;
    }
}
