package com.kenstevens.stratinit.dao.impl;

import com.kenstevens.stratinit.dao.LogDao;
import com.kenstevens.stratinit.model.*;
import com.kenstevens.stratinit.repo.BattleLogRepo;
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
    BattleLogRepo battleLogRepo;

    @Override
    public void save(BattleLog battleLog) {
        battleLogRepo.save(battleLog);
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
    public void delete(CityCapturedBattleLog log) {
        battleLogRepo.delete(log);
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
