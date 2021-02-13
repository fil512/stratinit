package com.kenstevens.stratinit.dao;

import com.kenstevens.stratinit.model.*;

import java.util.List;

public interface LogDao {

	void save(BattleLog battleLog);

	List<CityCapturedBattleLog> getCityCapturedBattleLogs(
			Nation nation);

	List<UnitAttackedBattleLog> getUnitAttackedBattleLogs(
			Nation nation);

	List<FlakBattleLog> getFlakBattleLogs(Nation nation);

	void delete(CityCapturedBattleLog log);

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