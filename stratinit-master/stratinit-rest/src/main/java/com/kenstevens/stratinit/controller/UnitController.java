package com.kenstevens.stratinit.controller;

import com.kenstevens.stratinit.client.model.audit.UnitBuildAudit;
import com.kenstevens.stratinit.client.rest.SIRestPaths;
import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.dto.SIUnitBuilt;
import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.remote.request.CedeUnitsJson;
import com.kenstevens.stratinit.remote.request.MoveUnitsJson;
import com.kenstevens.stratinit.remote.request.SIUnitListJson;
import com.kenstevens.stratinit.server.rest.request.RequestProcessor;
import com.kenstevens.stratinit.server.rest.request.WriteProcessor;
import com.kenstevens.stratinit.server.rest.svc.UnitSvc;
import com.kenstevens.stratinit.type.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = SIRestPaths.BASE_PATH)
@Tag(name = "Units")
public class UnitController {
    @Autowired
    private RequestProcessor requestProcessor;
    @Autowired
    private WriteProcessor writeProcessor;
    @Autowired
    private UnitSvc unitSvc;
    @Autowired
    private UnitDao unitDao;

    @PostMapping(path = SIRestPaths.MOVE_UNITS)
    @Operation(summary = "Move units to a target sector")
    public SIUpdate moveUnits(@Valid @RequestBody MoveUnitsJson moveUnitsJson) {
        return writeProcessor.processDynamicCost(
                nation -> unitSvc.moveUnits(nation, moveUnitsJson.units, moveUnitsJson.target),
                Constants.COMMAND_COST);
    }

    @GetMapping(path = SIRestPaths.UNIT)
    @Operation(summary = "Get all units owned by the current player")
    public List<SIUnit> getUnits() {
        return requestProcessor.process(nation -> unitSvc.getUnits(nation));
    }

    @GetMapping(path = SIRestPaths.UNIT_SEEN)
    @Operation(summary = "Get all visible units from other nations")
    public List<SIUnit> getSeenUnits() {
        return requestProcessor.process(nation -> unitSvc.getSeenUnits(nation));
    }

    @PostMapping(path = SIRestPaths.DISBAND_UNITS)
    @Operation(summary = "Disband units")
    public SIUpdate disbandUnits(@Valid @RequestBody SIUnitListJson request) {
        return writeProcessor.process(nation -> unitSvc.disbandUnits(nation, request.siunits), Constants.COMMAND_COST);
    }

    @PostMapping(path = SIRestPaths.CANCEL_MOVE)
    @Operation(summary = "Cancel pending move orders for units")
    public SIUpdate cancelMove(@Valid @RequestBody SIUnitListJson request) {
        return writeProcessor.process(nation -> unitSvc.cancelMoveOrders(nation, request.siunits), Constants.COMMAND_COST);
    }

    @PostMapping(path = SIRestPaths.BUILD_CITY)
    @Operation(summary = "Build a city with engineer units")
    public SIUpdate buildCity(@Valid @RequestBody SIUnitListJson request) {
        return writeProcessor.process(nation -> unitSvc.buildCity(nation, request.siunits), Constants.COMMAND_COST_BUILD_CITY);
    }

    @PostMapping(path = SIRestPaths.SWITCH_TERRAIN)
    @Operation(summary = "Switch unit terrain type")
    public SIUpdate switchTerrain(@Valid @RequestBody SIUnitListJson request) {
        return writeProcessor.process(nation -> unitSvc.switchTerrain(nation, request.siunits), Constants.COMMAND_COST_SWITCH_TERRAIN);
    }

    @PostMapping(path = SIRestPaths.CEDE_UNITS)
    @Operation(summary = "Transfer units to an allied nation")
    public SIUpdate cedeUnits(@Valid @RequestBody CedeUnitsJson request) {
        return writeProcessor.process(nation -> unitSvc.cedeUnits(nation, request.siunits, request.nationId), Constants.COMMAND_COST);
    }

    @GetMapping(path = SIRestPaths.UNIT_BUILT)
    @Operation(summary = "Get unit build history for the current player")
    public List<SIUnitBuilt> getUnitsBuilt() {
        return requestProcessor.process(nation -> {
            List<UnitBuildAudit> builds = unitDao.getBuildAudits(nation.getGameId(), nation.toString());
            return builds.stream()
                    .map(SIUnitBuilt::new)
                    .collect(Collectors.toList());
        });
    }
}
