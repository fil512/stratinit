package com.kenstevens.stratinit.wicket.provider;

import com.kenstevens.stratinit.dto.SINation;

import java.util.List;

public interface GameListProvider {

	List<GameTable> getGameTableList();

	List<SINation> getNations(int gameId);

}
