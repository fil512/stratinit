package com.kenstevens.stratinit.dao.impl;

import com.kenstevens.stratinit.dao.LogDao;
import com.kenstevens.stratinit.model.*;
import com.kenstevens.stratinit.repo.CityCapturedBattleLogRepo;
import com.kenstevens.stratinit.repo.CityNukedBattleLogRepo;
import com.kenstevens.stratinit.repo.FlakBattleLogRepo;
import com.kenstevens.stratinit.repo.UnitAttackedBattleLogRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
@Service
public class LogDaoImpl implements LogDao {
    // FIXME remove all entity managers
    @PersistenceContext
    protected EntityManager entityManager;
    @Autowired
    UnitAttackedBattleLogRepo unitAttackedBattleLogRepo;
    @Autowired
    CityCapturedBattleLogRepo cityCapturedBattleLogRepo;
    @Autowired
    CityNukedBattleLogRepo cityNukedBattleLogRepo;
    @Autowired
    FlakBattleLogRepo flakBattleLogRepo;

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
    public List<CityCapturedBattleLog> getCityCapturedBattleLogs(Nation nation) {
        return entityManager
                .createQuery(
                        "from CityCapturedBattleLog b WHERE b.attacker = :nation or b.defender = :nation")
                .setParameter("nation", nation).getResultList();
    }

    @Override
    public List<UnitAttackedBattleLog> getUnitAttackedBattleLogs(Nation nation) {
        return entityManager
                .createQuery(
                        "from UnitAttackedBattleLog b WHERE b.attacker = :nation or b.defender = :nation")
                .setParameter("nation", nation).getResultList();
    }

    @Override
    public List<FlakBattleLog> getFlakBattleLogs(Nation nation) {
        return entityManager
                .createQuery(
                        "from FlakBattleLog b WHERE b.attacker = :nation or b.defender = :nation")
                .setParameter("nation", nation).getResultList();
    }

    @Override
    public void delete(CityCapturedBattleLog cityCapturedBattleLog) {
        cityCapturedBattleLogRepo.delete(cityCapturedBattleLog);
    }

    @Override
    public void remove(UnitAttackedBattleLog log) {
        if (log == null || log.getId() == null) {
            return;
        }
        Integer battleLogId = log.getId();
        UnitAttackedBattleLog foundLog = entityManager.find(UnitAttackedBattleLog.class, battleLogId);
        if (foundLog != null) {
            entityManager.remove(foundLog);
            return;
        }
    }


    @Override
    public void remove(FlakBattleLog log) {
        if (log == null || log.getId() == null) {
            return;
        }
        Integer battleLogId = log.getId();
        FlakBattleLog foundLog = entityManager.find(FlakBattleLog.class, battleLogId);
        if (foundLog != null) {
            entityManager.remove(foundLog);
            return;
        }
    }

    @Override
    public void remove(CityNukedBattleLog log) {
        if (log == null || log.getId() == null) {
            return;
        }
        Integer battleLogId = log.getId();
        CityNukedBattleLog foundLog = entityManager.find(CityNukedBattleLog.class, battleLogId);
        if (foundLog != null) {
            entityManager.remove(foundLog);
            return;
        }
    }

    @Override
    public void removeLogs(Game game) {
        List<BattleLog> logs = getBattleLogs(game);
        for (BattleLog log : logs) {
            entityManager.remove(log);
        }
    }

    @Override
    public List<BattleLog> getBattleLogs(Game game) {
        List<BattleLog> logs = new ArrayList<BattleLog>();
        logs.addAll(getCityCapturedBattleLogs(game));
        logs.addAll(getUnitAttackedBattleLogs(game));
        logs.addAll(getFlakBattleLogs(game));
        logs.addAll(getCityNukedBattleLogs(game));
        return logs;
    }

    @Override
    public List<CityCapturedBattleLog> getCityCapturedBattleLogs(Game game) {
        return entityManager
                .createQuery(
                        "from CityCapturedBattleLog b WHERE b.attacker.nationPK.game = :game")
                .setParameter("game", game).getResultList();
    }

    @Override
    public List<UnitAttackedBattleLog> getUnitAttackedBattleLogs(Game game) {
        return entityManager
                .createQuery(
                        "from UnitAttackedBattleLog b WHERE b.attacker.nationPK.game = :game")
                .setParameter("game", game).getResultList();
    }

    @Override
    public List<FlakBattleLog> getFlakBattleLogs(Game game) {
        return entityManager
                .createQuery(
                        "from FlakBattleLog b WHERE b.attacker.nationPK.game = :game")
                .setParameter("game", game).getResultList();
    }

    @Override
    public List<CityNukedBattleLog> getCityNukedBattleLogs(Game game) {
        return entityManager
                .createQuery(
                        "from CityNukedBattleLog b WHERE b.attacker.nationPK.game = :game")
                .setParameter("game", game).getResultList();
    }

    @Override
    public void persist(ErrorLog errorLog) {
        entityManager.persist(errorLog);
    }
}
