package com.kenstevens.stratinit.rank;

import java.util.Collection;
import java.util.List;

import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Nation;

public interface TeamProvider {

	Collection<Nation> getAllies(Nation nation);

	List<Nation> getNations(Game game);

	int getNumberOfCities(Nation nation);

}
