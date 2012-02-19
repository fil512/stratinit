package com.kenstevens.stratinit.client.gwt.model;

import java.util.Date;

public class GameListGridRecord extends StratInitListGridRecord<Integer, GWTGame> {
	// TODO use the one in Constants
	private static final int MIN_PLAYERS_TO_SCHEDULE = 4;
	private static final int MAX_PLAYERS_PER_GAME = 10;

	public GameListGridRecord(GWTGame game) {
		setValues(game);
	}

	@Override
	public final void setValues(GWTGame game) {
		setId(game.getId());
		setName(game.getName());
		setSize(game.getSize());
		setPlayers(getPlayersString(game));
		setCreated(game.getCreated());
		setStarted(game.getStarted());
		setEnds(game.getEnds());
	}

	public final String getPlayersString(GWTGame game) {
		String retval = "";
		if (game.getIslands() > 0) {
			retval = "" + game.getPlayers() + "/"
				+ game.getIslands();
		} else if (game.getStarted() == null) {
			retval = "" + game.getPlayers() + "/"
			+ MIN_PLAYERS_TO_SCHEDULE;
		} else {
			retval = "" + game.getPlayers() + "/"
			+ MAX_PLAYERS_PER_GAME;
		}
		return retval;
	}

	public final void setId(int id) {
        setAttribute("id", id);
    }
	public final void setName(String name) {
        setAttribute("name", name);
    }
	public final void setSize(int size) {
        setAttribute("size", size);
    }
	public final void setPlayers(String players) {
        setAttribute("players", players);
    }
	public final void setCreated(Date started) {
		setAttribute("created", started);
	}
	public final void setStarted(Date started) {
		setAttribute("started", started);
	}
	public final void setEnds(Date ends) {
		setAttribute("ends", ends);
	}
}
