package com.kenstevens.stratinit.dao;

import java.util.List;

import com.kenstevens.stratinit.model.BattleLog;
import com.kenstevens.stratinit.model.CityCapturedBattleLog;
import com.kenstevens.stratinit.model.CityNukedBattleLog;
import com.kenstevens.stratinit.model.FlakBattleLog;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.UnitAttackedBattleLog;

public interface LogDao {

	public abstract void persist(BattleLog battleLog);

	public abstract List<CityCapturedBattleLog> getCityCapturedBattleLogs(
			Nation nation);

	public abstract List<UnitAttackedBattleLog> getUnitAttackedBattleLogs(
			Nation nation);

	public abstract List<FlakBattleLog> getFlakBattleLogs(Nation nation);

	public abstract void remove(CityCapturedBattleLog log);

	public abstract void remove(UnitAttackedBattleLog log);

	public abstract void remove(FlakBattleLog log);

	public abstract void remove(CityNukedBattleLog log);

	public abstract void removeLogs(Game game);

	public abstract List<BattleLog> getBattleLogs(Game game);

	public abstract List<CityCapturedBattleLog> getCityCapturedBattleLogs(
			Game game);

	public abstract List<UnitAttackedBattleLog> getUnitAttackedBattleLogs(
			Game game);

	public abstract List<FlakBattleLog> getFlakBattleLogs(Game game);

	public abstract List<CityNukedBattleLog> getCityNukedBattleLogs(Game game);

}