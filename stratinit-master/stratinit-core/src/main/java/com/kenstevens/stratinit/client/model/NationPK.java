package com.kenstevens.stratinit.client.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;
import java.io.Serializable;


@Embeddable
public class NationPK implements Serializable {

	private static final long serialVersionUID = 2587125480008234892L;
	@ManyToOne
	private Game game;
	@ManyToOne
	private Player player;

	public NationPK() {}

	public NationPK(Game game, Player player) {
		this.game = game;
		this.player = player;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((game == null) ? 0 : game.hashCode());
		result = prime * result + ((player == null) ? 0 : player.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NationPK other = (NationPK) obj;
		if (game == null) {
			if (other.game != null)
				return false;
		} else if (!game.equals(other.game))
			return false;
		if (player == null) {
			return other.player == null;
		} else return player.equals(other.player);
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public Game getGame() {
		return game;
	}

}
