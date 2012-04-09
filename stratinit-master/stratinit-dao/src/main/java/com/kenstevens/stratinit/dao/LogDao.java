package com.kenstevens.stratinit.dao;

import java.util.List;

import com.kenstevens.stratinit.model.BattleLog;
import com.kenstevens.stratinit.model.CityCapturedBattleLog;
import com.kenstevens.stratinit.model.CityNukedBattleLog;
import com.kenstevens.stratinit.model.ErrorLog;
import com.kenstevens.stratinit.model.FlakBattleLog;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.UnitAttackedBattleLog;

public interface LogDao {

	void persist(BattleLog battleLog);

	List<CityCapturedBattleLog> getCityCapturedBattleLogs(
			Nation nation);

	List<UnitAttackedBattleLog> getUnitAttackedBattleLogs(
			Nation nation);

	List<FlakBattleLog> getFlakBattleLogs(Nation nation);

	void remove(CityCapturedBattleLog log);

	void remove(UnitAttackedBattleLog log);

	void remove(FlakBattleLog log);

	void remove(CityNukedBattleLog log);

	void removeLogs(Game game);

	List<BattleLog> getBattleLogs(Game game);

	List<CityCapturedBattleLog> getCityCapturedBattleLogs(
			Game game);

	List<UnitAttackedBattleLog> getUnitAttackedBattleLogs(
			Game game);

	List<FlakBattleLog> getFlakBattleLogs(Game game);

	List<CityNukedBattleLog> getCityNukedBattleLogs(Game game);

	void persist(ErrorLog errorLog);

}