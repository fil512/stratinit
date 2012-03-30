package com.kenstevens.stratinit.wicket.game;

import java.util.List;

import com.kenstevens.stratinit.dto.SINation;

public interface GameListProvider {

	List<GameTable> getGameTableList();

	List<SINation> getNations(int gameId);

}
