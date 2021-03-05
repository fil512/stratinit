package com.kenstevens.stratinit.dto;

import com.kenstevens.stratinit.client.model.Game;

import java.util.Date;


public class SIGame implements StratInitDTO {
    private static final long serialVersionUID = 1L;
    public int id;
    public String name;
    public int players;
    public int noAlliancesVote;
    public boolean noAlliances;
    public boolean myNoAlliances;
    public int islands;
    public int size;
    public Date started;
	public Date mapped;
	public Date created;
	public Date ends;
	public boolean blitz;

	public SIGame() {}

	public SIGame(Game game, boolean myNoAlliances) {
		id = game.getId();
		name = game.getGamename();
		size = game.getGamesize();
		players = game.getPlayers();
		noAlliancesVote = game.getNoAlliancesVote();
		noAlliances = game.isNoAlliances();
		islands = game.getIslands();
		started = game.getStartTime();
		mapped = game.getMapped();
		created = game.getCreated();
		ends = game.getEnds();
		blitz = game.isBlitz();
		this.myNoAlliances = myNoAlliances;
	}
}
