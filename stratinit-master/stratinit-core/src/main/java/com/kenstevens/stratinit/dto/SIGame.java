package com.kenstevens.stratinit.dto;

import java.io.Serializable;
import java.util.Date;

import com.kenstevens.stratinit.model.Game;


public class SIGame implements Serializable {
	private static final long serialVersionUID = 1L;
	public int id;
	public String name;
	public int players;
	public int noAlliancesVote;
	public int islands;
	public int size;
	public Date started;
	public Date mapped;
	public Date created;
	public Date ends;
	public boolean blitz;

	public SIGame() {}

	public SIGame(Game game) {
		id = game.getId();
		name = game.getName();
		size = game.getSize();
		players = game.getPlayers();
		noAlliancesVote = game.getNoAlliancesVote();
		islands = game.getIslands();
		started = game.getStartTime();
		mapped = game.getMapped();
		created = game.getCreated();
		ends = game.getEnds();
		blitz = game.isBlitz();
	}
}
