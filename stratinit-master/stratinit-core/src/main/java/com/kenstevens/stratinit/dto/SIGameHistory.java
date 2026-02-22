package com.kenstevens.stratinit.dto;

import com.kenstevens.stratinit.client.model.GameHistory;

import java.util.Date;

public class SIGameHistory implements StratInitDTO {
    public int gameId;
    public String gamename;
    public int gamesize;
    public Date startTime;
    public Date ends;

    public SIGameHistory() {
    }

    public SIGameHistory(GameHistory gameHistory) {
        this.gameId = gameHistory.getGameId();
        this.gamename = gameHistory.getGamename();
        this.gamesize = gameHistory.getGamesize();
        this.startTime = gameHistory.getStartTime();
        this.ends = gameHistory.getEnds();
    }
}
