package com.kenstevens.stratinit.controller;

import com.kenstevens.stratinit.client.model.CityCapturedBattleLog;
import com.kenstevens.stratinit.client.model.FlakBattleLog;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.UnitAttackedBattleLog;
import com.kenstevens.stratinit.client.rest.SIRestPaths;
import com.kenstevens.stratinit.dao.LogDao;
import com.kenstevens.stratinit.dto.*;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.SetRelationJson;
import com.kenstevens.stratinit.server.daoservice.GameDaoService;
import com.kenstevens.stratinit.server.rest.request.RequestProcessor;
import com.kenstevens.stratinit.server.rest.request.WriteProcessor;
import com.kenstevens.stratinit.server.rest.svc.NationSvc;
import com.kenstevens.stratinit.server.rest.svc.PlayerWorldView;
import com.kenstevens.stratinit.server.rest.svc.RelationSvc;
import com.kenstevens.stratinit.type.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = SIRestPaths.BASE_PATH)
public class NationController {
    @Autowired
    private RequestProcessor requestProcessor;
    @Autowired
    private WriteProcessor writeProcessor;
    @Autowired
    private NationSvc nationSvc;
    @Autowired
    private PlayerWorldView playerWorldView;
    @Autowired
    private RelationSvc relationSvc;
    @Autowired
    private GameDaoService gameDaoService;
    @Autowired
    private LogDao logDao;

    @GetMapping(path = SIRestPaths.NATION)
    public Result<List<SINation>> getNations() {
        return requestProcessor.process(nation -> nationSvc.getNations(nation, true));
    }

    @GetMapping(path = SIRestPaths.NATION_ME)
    public Result<SINation> getMyNation() {
        return requestProcessor.process(nation -> nationSvc.getMyNation(nation));
    }

    @GetMapping(path = SIRestPaths.SECTOR)
    public Result<List<SISector>> getSectors() {
        return requestProcessor.process(nation -> playerWorldView.getWorldViewSectors(nation));
    }

    @GetMapping(path = SIRestPaths.RELATION)
    public Result<List<SIRelation>> getRelations() {
        return requestProcessor.process(nation -> relationSvc.getRelations(nation));
    }

    @PostMapping(path = SIRestPaths.SET_RELATION)
    public Result<SIRelation> setRelation(@RequestBody SetRelationJson request) {
        return writeProcessor.process(
                nation -> relationSvc.setRelation(nation, request.nationId, request.relationType),
                Constants.COMMAND_COST);
    }

    @GetMapping(path = SIRestPaths.BATTLE_LOG)
    public Result<List<SIBattleLog>> getBattleLog() {
        return writeProcessor.process(nation -> {
            nation.setNewBattle(false);
            gameDaoService.merge(nation);
            List<SIBattleLog> retval = new ArrayList<>();
            for (CityCapturedBattleLog log : logDao.getCityCapturedBattleLogs(nation)) {
                retval.add(new SIBattleLog(nation, log));
            }
            List<UnitAttackedBattleLog> uabattleLogs = new ArrayList<>();
            logDao.getUnitAttackedBattleLogs(nation).forEach(uabattleLogs::add);
            for (UnitAttackedBattleLog log : uabattleLogs) {
                retval.add(new SIBattleLog(nation, log));
            }
            List<FlakBattleLog> fbattleLogs = new ArrayList<>();
            logDao.getFlakBattleLogs(nation).forEach(fbattleLogs::add);
            for (FlakBattleLog log : fbattleLogs) {
                retval.add(new SIBattleLog(nation, log));
            }
            return Result.make(retval);
        }, 0);
    }

    @GetMapping(path = SIRestPaths.TEAM)
    public Result<List<SITeam>> getTeams() {
        return requestProcessor.processWithGame(game -> gameDaoService.getTeams(game));
    }
}
