package com.kenstevens.stratinit.dao.impl;

import com.kenstevens.stratinit.dao.LogDao;
import com.kenstevens.stratinit.model.*;
import com.kenstevens.stratinit.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
@Service
public class LogDaoImpl implements LogDao {
    // FIXME remove all entity managers
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

    @Override
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

    @Override
    public Iterable<CityCapturedBattleLog> getCityCapturedBattleLogs(Nation nation) {
        QCityCapturedBattleLog log = QCityCapturedBattleLog.cityCapturedBattleLog;
        return cityCapturedBattleLogRepo.findAll(log.attacker.eq(nation).or(log.defender.eq(nation)));
    }

    @Override
    public Iterable<UnitAttackedBattleLog> getUnitAttackedBattleLogs(Nation nation) {
        QUnitAttackedBattleLog log = QUnitAttackedBattleLog.unitAttackedBattleLog;
        return unitAttackedBattleLogRepo.findAll(log.attacker.eq(nation).or(log.defender.eq(nation)));
    }

    @Override
    public Iterable<FlakBattleLog> getFlakBattleLogs(Nation nation) {
        QFlakBattleLog log = QFlakBattleLog.flakBattleLog;
        return flakBattleLogRepo.findAll(log.attacker.eq(nation).or(log.defender.eq(nation)));
    }

    @Override
    public void delete(@Nonnull CityCapturedBattleLog cityCapturedBattleLog) {
        cityCapturedBattleLogRepo.delete(cityCapturedBattleLog);
    }

    @Override
    public void remove(@Nonnull UnitAttackedBattleLog log) {
        unitAttackedBattleLogRepo.deleteById(log.getId());
    }


    @Override
    public void remove(@Nonnull FlakBattleLog log) {
        flakBattleLogRepo.deleteById(log.getId());
    }

    @Override
    public void remove(@Nonnull CityNukedBattleLog log) {
        cityNukedBattleLogRepo.deleteById(log.getId());
    }

    @Override
    public void removeLogs(Game game) {
        getCityCapturedBattleLogs(game).forEach(cityCapturedBattleLogRepo::delete);
        getUnitAttackedBattleLogs(game).forEach(unitAttackedBattleLogRepo::delete);
        getFlakBattleLogs(game).forEach(flakBattleLogRepo::delete);
        getCityNukedBattleLogs(game).forEach(cityNukedBattleLogRepo::delete);
    }

    @Override
    public List<BattleLog> getBattleLogs(Game game) {
        List<BattleLog> logs = new ArrayList<BattleLog>();
        getCityCapturedBattleLogs(game).forEach(logs::add);
        getUnitAttackedBattleLogs(game).forEach(logs::add);
        getFlakBattleLogs(game).forEach(logs::add);
        getCityNukedBattleLogs(game).forEach(logs::add);
        return logs;
    }

    @Override
    public Iterable<CityCapturedBattleLog> getCityCapturedBattleLogs(Game game) {
        return cityCapturedBattleLogRepo.findAll(QCityCapturedBattleLog.cityCapturedBattleLog.attacker.nationPK.game.eq(game));
    }

    @Override
    public Iterable<UnitAttackedBattleLog> getUnitAttackedBattleLogs(Game game) {
        return unitAttackedBattleLogRepo.findAll(QUnitAttackedBattleLog.unitAttackedBattleLog.attacker.nationPK.game.eq(game));
    }

    @Override
    public Iterable<FlakBattleLog> getFlakBattleLogs(Game game) {
        return flakBattleLogRepo.findAll(QFlakBattleLog.flakBattleLog.attacker.nationPK.game.eq(game));
    }

    @Override
    public Iterable<CityNukedBattleLog> getCityNukedBattleLogs(Game game) {
        return cityNukedBattleLogRepo.findAll(QCityNukedBattleLog.cityNukedBattleLog.attacker.nationPK.game.eq(game));
    }

    @Override
    public void save(ErrorLog errorLog) {
        errorLogRepo.save(errorLog);
    }
}
