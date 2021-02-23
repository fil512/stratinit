package com.kenstevens.stratinit.remote.request;

public class SetGameRequest implements RestRequest {
    public int gameId;
    public boolean noAlliances;

    public SetGameRequest() {
    }

    public SetGameRequest(int gameId, boolean noAlliances) {
        this.gameId = gameId;
        this.noAlliances = noAlliances;
    }
}
