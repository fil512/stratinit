package com.kenstevens.stratinit.wicket.unit;

import java.util.Collections;
import java.util.List;

import org.apache.wicket.model.LoadableDetachableModel;

import com.kenstevens.stratinit.type.UnitBaseType;

@SuppressWarnings("serial")
public class DayUnitsModel extends LoadableDetachableModel<List<DayUnitsListRow>> {

	private final PlayerUnitsProvider playerUnitsProvider;
	private final int gameId;
	private final String username;
	private final UnitBaseType unitBaseType;

	public DayUnitsModel(PlayerUnitsProvider playerUnitsProvider, int gameId,
			String username, UnitBaseType unitBaseType) {
				this.playerUnitsProvider = playerUnitsProvider;
				this.gameId = gameId;
				this.username = username;
				this.unitBaseType = unitBaseType;
	}

	@Override
	protected List<DayUnitsListRow> load() {
		List<DayUnitsListRow> rows = playerUnitsProvider.getDayUnitsListRows(gameId, username, unitBaseType);
		Collections.reverse(rows);
		return rows;
	}

}
