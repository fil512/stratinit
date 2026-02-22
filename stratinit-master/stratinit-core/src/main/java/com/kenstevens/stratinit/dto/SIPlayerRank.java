package com.kenstevens.stratinit.dto;

import com.kenstevens.stratinit.client.model.Player;

public class SIPlayerRank implements StratInitDTO {
    private static final long serialVersionUID = 1L;
    public String username;
    public int wins;
    public int played;
    public int winPercentage;

    public SIPlayerRank() {}

    public SIPlayerRank(Player player) {
        this.username = player.getUsername();
        this.wins = player.getWins();
        this.played = player.getPlayed();
        this.winPercentage = player.getWinPerc();
    }
}
