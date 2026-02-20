package com.kenstevens.stratinit.dao;

import com.kenstevens.stratinit.client.model.*;
import com.kenstevens.stratinit.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
@Service
public class LogDao {
	@Autowired
	UnitAttackedBattleLogRepo unitAttackedBattleLogRepo;
	@Autowired
	CityCapturedBattleLogRepo cityCapturedBattleLogRepo;
	@Autowired
	CityNukedBattleLogRepo cityNukedBattleLogRepo;
	@Autowired
	FlakBattleLogRepo flakBattleLogRepo;
	@Autowired
	ErrorLogRepo errorLogRepo;

	public void save(BattleLog battleLog) {
		if (battleLog instanceof UnitAttackedBattleLog) {
			unitAttackedBattleLogRepo.save((UnitAttackedBattleLog) battleLog);
		} else if (battleLog instanceof CityCapturedBattleLog) {
			cityCapturedBattleLogRepo.save((CityCapturedBattleLog) battleLog);
		} else if (battleLog instanceof CityNukedBattleLog) {
			cityNukedBattleLogRepo.save((CityNukedBattleLog) battleLog);
		} else if (battleLog instanceof FlakBattleLog) {
			flakBattleLogRepo.save((FlakBattleLog) battleLog);
		}
	}

	public void save(CityCapturedBattleLog cityCapturedBattleLog) {
		cityCapturedBattleLogRepo.save(cityCapturedBattleLog);
	}

	public void save(CityNukedBattleLog cityNukedBattleLog) {
		cityNukedBattleLogRepo.save(cityNukedBattleLog);
	}

	public void save(FlakBattleLog flakBattleLog) {
		flakBattleLogRepo.save(flakBattleLog);
	}

	public Iterable<CityCapturedBattleLog> getCityCapturedBattleLogs(Nation nation) {
		QCityCapturedBattleLog log = QCityCapturedBattleLog.cityCapturedBattleLog;
		return cityCapturedBattleLogRepo.findAll(log.attacker.eq(nation).or(log.defender.eq(nation)));
	}

	public Iterable<UnitAttackedBattleLog> getUnitAttackedBattleLogs(Nation nation) {
		QUnitAttackedBattleLog log = QUnitAttackedBattleLog.unitAttackedBattleLog;
		return unitAttackedBattleLogRepo.findAll(log.attacker.eq(nation).or(log.defender.eq(nation)));
	}

	public Iterable<FlakBattleLog> getFlakBattleLogs(Nation nation) {
		QFlakBattleLog log = QFlakBattleLog.flakBattleLog;
		return flakBattleLogRepo.findAll(log.attacker.eq(nation).or(log.defender.eq(nation)));
	}

	public void delete(@Nonnull CityCapturedBattleLog cityCapturedBattleLog) {
		cityCapturedBattleLogRepo.delete(cityCapturedBattleLog);
	}

	public void remove(@Nonnull UnitAttackedBattleLog log) {
		unitAttackedBattleLogRepo.deleteById(log.getId());
	}


	public void remove(@Nonnull FlakBattleLog log) {
		flakBattleLogRepo.deleteById(log.getId());
	}

	public void remove(@Nonnull CityNukedBattleLog log) {
		cityNukedBattleLogRepo.deleteById(log.getId());
	}

	public void removeLogs(Game game) {
		getCityCapturedBattleLogs(game).forEach(cityCapturedBattleLogRepo::delete);
		getUnitAttackedBattleLogs(game).forEach(unitAttackedBattleLogRepo::delete);
		getFlakBattleLogs(game).forEach(flakBattleLogRepo::delete);
		getCityNukedBattleLogs(game).forEach(cityNukedBattleLogRepo::delete);
	}

	public List<BattleLog> getBattleLogs(Game game) {
		List<BattleLog> logs = new ArrayList<BattleLog>();
		getCityCapturedBattleLogs(game).forEach(logs::add);
		getUnitAttackedBattleLogs(game).forEach(logs::add);
		getFlakBattleLogs(game).forEach(logs::add);
		getCityNukedBattleLogs(game).forEach(logs::add);
		return logs;
	}

	public Iterable<CityCapturedBattleLog> getCityCapturedBattleLogs(Game game) {
		return cityCapturedBattleLogRepo.findAll(QCityCapturedBattleLog.cityCapturedBattleLog.attacker.nationPK.game.eq(game));
	}

	public Iterable<UnitAttackedBattleLog> getUnitAttackedBattleLogs(Game game) {
		return unitAttackedBattleLogRepo.findAll(QUnitAttackedBattleLog.unitAttackedBattleLog.attacker.nationPK.game.eq(game));
	}

	public Iterable<FlakBattleLog> getFlakBattleLogs(Game game) {
		return flakBattleLogRepo.findAll(QFlakBattleLog.flakBattleLog.attacker.nationPK.game.eq(game));
	}

	public Iterable<CityNukedBattleLog> getCityNukedBattleLogs(Game game) {
		return cityNukedBattleLogRepo.findAll(QCityNukedBattleLog.cityNukedBattleLog.attacker.nationPK.game.eq(game));
	}

	public void save(ErrorLog errorLog) {
		errorLogRepo.save(errorLog);
	}
}
