package com.kenstevens.stratinit.util;

import com.kenstevens.stratinit.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExpungeSvc {
    @Autowired
    PlayerRepo playerRepo;
    @Autowired
    UnitAttackedBattleLogRepo unitAttackedBattleLog;
    @Autowired
    CityNukedBattleLogRepo cityNukedBattleLogRepo;
    @Autowired
    CityCapturedBattleLogRepo cityCapturedBattleLogRepo;
    @Autowired
    FlakBattleLogRepo flakBattleLogRepo;
    @Autowired
    CityMoveRepo cityMoveRepo;
    @Autowired
    CityRepo cityRepo;
    @Autowired
    GameBuildAuditRepo gameBuildAuditRepo;
    @Autowired
    GameRepo gameRepo;
    @Autowired
    MailRepo mailRepo;
    @Autowired
    NationRepo nationRepo;
    @Autowired
    PlayerRoleRepo playerRoleRepo;
    @Autowired
    RelationChangeAuditRepo relationChangeAuditRepo;
    @Autowired
    RelationRepo relationRepo;
    @Autowired
    SectorRepo sectorRepo;
    @Autowired
    SectorSeenRepo sectorSeenRepo;
    @Autowired
    UnitRepo unitRepo;
    @Autowired
    UnitSeenRepo unitSeenRepo;

    public void expungeAll() {
        unitAttackedBattleLog.deleteAll();
        cityCapturedBattleLogRepo.deleteAll();
        cityNukedBattleLogRepo.deleteAll();
        flakBattleLogRepo.deleteAll();
        cityMoveRepo.deleteAll();
        cityRepo.deleteAll();
        gameBuildAuditRepo.deleteAll();
        mailRepo.deleteAll();
        relationChangeAuditRepo.deleteAll();
        relationRepo.deleteAll();
        sectorRepo.deleteAll();
        sectorSeenRepo.deleteAll();
        unitSeenRepo.deleteAll();
        unitRepo.deleteAll();
        nationRepo.deleteAll();
        playerRoleRepo.deleteAll();
        playerRepo.deleteAll();
        gameRepo.deleteAll();
    }
}