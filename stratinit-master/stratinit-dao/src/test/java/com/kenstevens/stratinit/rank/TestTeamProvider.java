package com.kenstevens.stratinit.rank;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.dto.SINation;
import com.kenstevens.stratinit.dto.SITeam;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.GameHistory;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Player;

import java.util.Collection;
import java.util.List;

public class TestTeamProvider implements TeamProvider {
	public static Game game1 = new Game("game1");
	public static Game game2 = new Game("game2");
	public static Nation nation1a;
	public static Nation nation1b;
	public static Nation nation1c;
	public static Nation nation1d;
	public static Nation nation2a;
	public static Nation nation2b;
	public static Nation nation2c;
	public static Nation nation2e;
	public static Nation nation2f;

	static {
		game1.setId(1);
		game2.setId(2);
		
		nation1a = new Nation(game1, new Player("playera", 1));
		nation1b = new Nation(game1, new Player("playerb", 2));
		nation1c = new Nation(game1, new Player("playerc", 3));
		nation1d = new Nation(game1, new Player("playerd", 4));
		nation2a = new Nation(game2, new Player("playera", 5));
		nation2b = new Nation(game2, new Player("playerb", 6));
		nation2c = new Nation(game2, new Player("playerc", 7));
		nation2e = new Nation(game2, new Player("playere", 8));
		nation2f = new Nation(game2, new Player("playerf", 9));
	}

	@Override
	public Collection<Nation> getAllies(Nation nation) {
		if (nation.equals(nation1a)) {
			return Lists.newArrayList(nation1b);
		}
		if (nation.equals(nation1c)) {
			return Lists.newArrayList(nation1d);
		}
		if (nation.equals(nation2a)) {
			return Lists.newArrayList(nation2b);
		}
		if (nation.equals(nation2c)) {
			return Lists.newArrayList();
		}
		if (nation.equals(nation2e)) {
			return Lists.newArrayList(nation2f);
		}
		return Lists.newArrayList();
	}

	@Override
	public List<Nation> getNations(Game game) {
		List<Nation> nations = Lists.newArrayList();
		if (game.equals(game1)) {
			nations.add(nation1a);
			nations.add(nation1b);
			nations.add(nation1c);
			nations.add(nation1d);
		}
		if (game.equals(game2)) {
			nations.add(nation2a);
			nations.add(nation2b);
			nations.add(nation2c);
			nations.add(nation2e);
			nations.add(nation2f);
		}
		return nations;
	}

	@Override
	public int getNumberOfCities(Nation nation) {
		if (nation.equals(nation1a)) {
			return 15;
		}
		if (nation.equals(nation2c)) {
			return 50;
		}
		return 10;
	}

	@Override
	public List<SITeam> findTeams(GameHistory game) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void getTeamsAndNations(GameHistory gameHistory, List<SITeam> teams,
			List<SINation> nations) {
		throw new UnsupportedOperationException();
	}

}
