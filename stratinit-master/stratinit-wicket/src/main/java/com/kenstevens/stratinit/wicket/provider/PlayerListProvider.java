package com.kenstevens.stratinit.wicket.provider;

import com.kenstevens.stratinit.client.model.Player;
import com.kenstevens.stratinit.dao.PlayerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class PlayerListProvider {
	@Autowired
	PlayerDao playerDao;

	public List<Player> getPlayersByWon() {
		List<Player> players = playerDao.getAllPlayers();
		Comparator<Player> byWon = new Comparator<Player>() {

			@Override
			public int compare(Player player1, Player player2) {
				return Integer.valueOf(player2.getWins()).compareTo(
						player1.getWins());
			}

		};
		Collections.sort(players, byWon);
		return players;
	}

	public List<Player> getPlayersByDate() {
		List<Player> players = playerDao.getAllPlayers();
		Comparator<Player> byDate = new Comparator<Player>() {

			@Override
			public int compare(Player player1, Player player2) {
				return player2.getLastLogin().compareTo(player1.getLastLogin());
			}

		};
		Collections.sort(players, byDate);
		return players;
	}

}
