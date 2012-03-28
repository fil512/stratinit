package com.kenstevens.stratinit.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

@Entity
public class GameHistoryTeam implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(name="game_history_team_id_seq", sequenceName="game_history_team_id_sequence", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="game_history_team_id_seq")
	private Integer id;
	@ManyToOne
	private GameHistory gameHistory;
	
    public GameHistoryTeam() {}

    public GameHistoryTeam(GameHistory gameHistory) {
    	this.setGameHistory(gameHistory);
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		GameHistoryTeam other = (GameHistoryTeam) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public void setGameHistory(GameHistory gameHistory) {
		this.gameHistory = gameHistory;
	}

	public GameHistory getGameHistory() {
		return gameHistory;
	}
}
