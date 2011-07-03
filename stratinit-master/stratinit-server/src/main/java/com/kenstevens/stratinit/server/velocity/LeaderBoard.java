package com.kenstevens.stratinit.server.velocity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.kenstevens.stratinit.model.Player;

public class LeaderBoard extends SpringAware {
	private static final int LEADER_GAMES = 5;
	private static final Predicate<Player> experiencedPlayer = new Predicate<Player>() {

		@Override
		public boolean apply(Player player) {
			return player.getPlayed() >= LEADER_GAMES;
		}
		
	};
	private static final Comparator<Player> byWinPercentage = new Comparator<Player>() {

		@Override
		public int compare(Player p1, Player p2) {
			return Integer.valueOf(p2.getWins()).compareTo(p1.getWins());
		}
		
	};

	public List<Player> getPlayers() {
		ArrayList<Player> filteredPlayers = Lists.newArrayList(Collections2.filter(getPlayerDao().getAllPlayers(), experiencedPlayer));
		Collections.sort(filteredPlayers, byWinPercentage);
		return filteredPlayers;
	}
}
