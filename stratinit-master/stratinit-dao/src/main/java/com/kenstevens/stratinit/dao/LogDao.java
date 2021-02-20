package com.kenstevens.stratinit.dao;

import com.kenstevens.stratinit.model.*;

import java.util.List;

public interface LogDao {

    void save(BattleLog BattleLog);

    Iterable<CityCapturedBattleLog> getCityCapturedBattleLogs(
			Nation nation);

	Iterable<UnitAttackedBattleLog> getUnitAttackedBattleLogs(
			Nation nation);

	Iterable<FlakBattleLog> getFlakBattleLogs(Nation nation);

    void delete(CityCapturedBattleLog log);

	void remove(UnitAttackedBattleLog log);

	void remove(FlakBattleLog log);

	void remove(CityNukedBattleLog log);

	void removeLogs(Game game);

	List<BattleLog> getBattleLogs(Game game);

	Iterable<CityCapturedBattleLog> getCityCapturedBattleLogs(
			Game game);

	Iterable<UnitAttackedBattleLog> getUnitAttackedBattleLogs(
			Game game);

	Iterable<FlakBattleLog> getFlakBattleLogs(Game game);

	Iterable<CityNukedBattleLog> getCityNukedBattleLogs(Game game);

	void save(ErrorLog errorLog);

}