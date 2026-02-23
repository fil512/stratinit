package com.kenstevens.stratinit.controller;

import com.kenstevens.stratinit.client.rest.SIRestPaths;
import com.kenstevens.stratinit.dto.*;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.SetRelationJson;
import com.kenstevens.stratinit.server.service.GameService;
import com.kenstevens.stratinit.server.rest.request.RequestProcessor;
import com.kenstevens.stratinit.server.rest.request.WriteProcessor;
import com.kenstevens.stratinit.server.rest.svc.NationSvc;
import com.kenstevens.stratinit.server.rest.svc.PlayerWorldView;
import com.kenstevens.stratinit.server.rest.svc.RelationSvc;
import com.kenstevens.stratinit.type.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = SIRestPaths.BASE_PATH)
@Tag(name = "Nations & Diplomacy")
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
    private GameService gameService;

    @GetMapping(path = SIRestPaths.NATION)
    @Operation(summary = "Get all nations in the current game")
    public List<SINation> getNations() {
        return requestProcessor.process(nation -> nationSvc.getNations(nation, true));
    }

    @GetMapping(path = SIRestPaths.NATION_ME)
    @Operation(summary = "Get the current player's nation")
    public SINation getMyNation() {
        return requestProcessor.process(nation -> nationSvc.getMyNation(nation));
    }

    @GetMapping(path = SIRestPaths.SECTOR)
    @Operation(summary = "Get all visible sectors in the current game")
    public List<SISector> getSectors() {
        return requestProcessor.process(nation -> playerWorldView.getWorldViewSectors(nation));
    }

    @GetMapping(path = SIRestPaths.RELATION)
    @Operation(summary = "Get diplomatic relations with all nations")
    public List<SIRelation> getRelations() {
        return requestProcessor.process(nation -> relationSvc.getRelations(nation));
    }

    @PostMapping(path = SIRestPaths.SET_RELATION)
    @Operation(summary = "Set diplomatic relation with another nation")
    public SIRelation setRelation(@Valid @RequestBody SetRelationJson request) {
        return writeProcessor.process(
                nation -> relationSvc.setRelation(nation, request.nationId, request.relationType),
                Constants.COMMAND_COST);
    }

    @GetMapping(path = SIRestPaths.BATTLE_LOG)
    @Operation(summary = "Get battle log history")
    public List<SIBattleLog> getBattleLog() {
        return writeProcessor.process(nation -> {
            nation.setNewBattle(false);
            gameService.merge(nation);
            return Result.make(nationSvc.getBattleLogs(nation));
        }, 0);
    }

    @GetMapping(path = SIRestPaths.TEAM)
    @Operation(summary = "Get team information for the current game")
    public List<SITeam> getTeams() {
        return requestProcessor.processWithGame(game -> gameService.getTeams(game));
    }
}
