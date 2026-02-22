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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = SIRestPaths.BASE_PATH)
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
    public SIUpdate moveUnits(@RequestBody MoveUnitsJson moveUnitsJson) {
        return writeProcessor.processDynamicCost(
                nation -> unitSvc.moveUnits(nation, moveUnitsJson.units, moveUnitsJson.target),
                Constants.COMMAND_COST);
    }

    @GetMapping(path = SIRestPaths.UNIT)
    public List<SIUnit> getUnits() {
        return requestProcessor.process(nation -> unitSvc.getUnits(nation));
    }

    @GetMapping(path = SIRestPaths.UNIT_SEEN)
    public List<SIUnit> getSeenUnits() {
        return requestProcessor.process(nation -> unitSvc.getSeenUnits(nation));
    }

    @PostMapping(path = SIRestPaths.DISBAND_UNITS)
    public SIUpdate disbandUnits(@RequestBody SIUnitListJson request) {
        return writeProcessor.process(nation -> unitSvc.disbandUnits(nation, request.siunits), Constants.COMMAND_COST);
    }

    @PostMapping(path = SIRestPaths.CANCEL_MOVE)
    public SIUpdate cancelMove(@RequestBody SIUnitListJson request) {
        return writeProcessor.process(nation -> unitSvc.cancelMoveOrders(nation, request.siunits), Constants.COMMAND_COST);
    }

    @PostMapping(path = SIRestPaths.BUILD_CITY)
    public SIUpdate buildCity(@RequestBody SIUnitListJson request) {
        return writeProcessor.process(nation -> unitSvc.buildCity(nation, request.siunits), Constants.COMMAND_COST_BUILD_CITY);
    }

    @PostMapping(path = SIRestPaths.SWITCH_TERRAIN)
    public SIUpdate switchTerrain(@RequestBody SIUnitListJson request) {
        return writeProcessor.process(nation -> unitSvc.switchTerrain(nation, request.siunits), Constants.COMMAND_COST_SWITCH_TERRAIN);
    }

    @PostMapping(path = SIRestPaths.CEDE_UNITS)
    public SIUpdate cedeUnits(@RequestBody CedeUnitsJson request) {
        return writeProcessor.process(nation -> unitSvc.cedeUnits(nation, request.siunits, request.nationId), Constants.COMMAND_COST);
    }

    @GetMapping(path = SIRestPaths.UNIT_BUILT)
    public List<SIUnitBuilt> getUnitsBuilt() {
        return requestProcessor.process(nation -> {
            List<UnitBuildAudit> builds = unitDao.getBuildAudits(nation.getGameId(), nation.toString());
            return builds.stream()
                    .map(SIUnitBuilt::new)
                    .collect(Collectors.toList());
        });
    }
}
