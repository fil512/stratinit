package com.kenstevens.stratinit.controller;

import com.kenstevens.stratinit.client.rest.SIRestPaths;
import com.kenstevens.stratinit.dto.*;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.SetRelationJson;
import com.kenstevens.stratinit.server.rest.request.RequestFactory;
import com.kenstevens.stratinit.server.rest.request.RequestProcessor;
import com.kenstevens.stratinit.server.rest.svc.NationSvc;
import com.kenstevens.stratinit.server.rest.svc.PlayerWorldView;
import com.kenstevens.stratinit.server.rest.svc.RelationSvc;
import com.kenstevens.stratinit.server.daoservice.GameDaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = SIRestPaths.BASE_PATH)
public class NationController {
    @Autowired
    private RequestFactory requestFactory;
    @Autowired
    private RequestProcessor requestProcessor;
    @Autowired
    private NationSvc nationSvc;
    @Autowired
    private PlayerWorldView playerWorldView;
    @Autowired
    private RelationSvc relationSvc;
    @Autowired
    private GameDaoService gameDaoService;

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
        return requestFactory.getSetRelationRequest(request.nationId, request.relationType)
                .process();
    }

    @GetMapping(path = SIRestPaths.BATTLE_LOG)
    public Result<List<SIBattleLog>> getBattleLog() {
        return requestFactory.getGetBattleLogRequest().process();
    }

    @GetMapping(path = SIRestPaths.TEAM)
    public Result<List<SITeam>> getTeams() {
        return requestProcessor.processWithGame(game -> gameDaoService.getTeams(game));
    }
}
