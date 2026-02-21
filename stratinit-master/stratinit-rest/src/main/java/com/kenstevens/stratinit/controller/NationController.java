package com.kenstevens.stratinit.controller;

import com.kenstevens.stratinit.client.rest.SIRestPaths;
import com.kenstevens.stratinit.dto.*;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.SetRelationJson;
import com.kenstevens.stratinit.server.rest.request.RequestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = SIRestPaths.BASE_PATH)
public class NationController {
    @Autowired
    private RequestFactory requestFactory;

    @GetMapping(path = SIRestPaths.NATION)
    public Result<List<SINation>> getNations() {
        return requestFactory.getGetNationsRequest().process();
    }

    @GetMapping(path = SIRestPaths.NATION_ME)
    public Result<SINation> getMyNation() {
        return requestFactory.getGetMyNationRequest().process();
    }

    @GetMapping(path = SIRestPaths.SECTOR)
    public Result<List<SISector>> getSectors() {
        return requestFactory.getGetSectorsRequest().process();
    }

    @GetMapping(path = SIRestPaths.RELATION)
    public Result<List<SIRelation>> getRelations() {
        return requestFactory.getGetRelationsRequest().process();
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
        return requestFactory.getGetTeamsRequest().process();
    }
}
