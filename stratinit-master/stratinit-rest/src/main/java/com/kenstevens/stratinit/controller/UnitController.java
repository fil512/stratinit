package com.kenstevens.stratinit.controller;

import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.audit.UnitBuildAudit;
import com.kenstevens.stratinit.client.rest.SIRestPaths;
import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.dto.SIUnitBuilt;
import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.CedeUnitsJson;
import com.kenstevens.stratinit.remote.request.MoveUnitsJson;
import com.kenstevens.stratinit.remote.request.SIUnitListJson;
import com.kenstevens.stratinit.server.rest.request.RequestFactory;
import com.kenstevens.stratinit.server.rest.request.RequestProcessor;
import com.kenstevens.stratinit.server.rest.svc.UnitSvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = SIRestPaths.BASE_PATH)
public class UnitController {
    @Autowired
    private RequestFactory requestFactory;
    @Autowired
    private RequestProcessor requestProcessor;
    @Autowired
    private UnitSvc unitSvc;
    @Autowired
    private UnitDao unitDao;

    @PostMapping(path = SIRestPaths.MOVE_UNITS)
    public Result<SIUpdate> moveUnits(@RequestBody MoveUnitsJson moveUnitsJson) {
        return requestFactory.getMoveUnitsRequest(moveUnitsJson.units, moveUnitsJson.target).process();
    }

    @GetMapping(path = SIRestPaths.UNIT)
    public Result<List<SIUnit>> getUnits() {
        return requestProcessor.process(nation -> unitSvc.getUnits(nation));
    }

    @GetMapping(path = SIRestPaths.UNIT_SEEN)
    public Result<List<SIUnit>> getSeenUnits() {
        return requestProcessor.process(nation -> unitSvc.getSeenUnits(nation));
    }

    @PostMapping(path = SIRestPaths.DISBAND_UNITS)
    public Result<SIUpdate> disbandUnits(@RequestBody SIUnitListJson request) {
        return requestFactory.getDisbandUnitRequest(request.siunits).process();
    }

    @PostMapping(path = SIRestPaths.CANCEL_MOVE)
    public Result<SIUpdate> cancelMove(@RequestBody SIUnitListJson request) {
        return requestFactory.getCancelMoveOrderRequest(request.siunits).process();
    }

    @PostMapping(path = SIRestPaths.BUILD_CITY)
    public Result<SIUpdate> buildCity(@RequestBody SIUnitListJson request) {
        return requestFactory.getBuildCityRequest(request.siunits).process();
    }

    @PostMapping(path = SIRestPaths.SWITCH_TERRAIN)
    public Result<SIUpdate> switchTerrain(@RequestBody SIUnitListJson request) {
        return requestFactory.getSwitchTerrainRequest(request.siunits).process();
    }

    @PostMapping(path = SIRestPaths.CEDE_UNITS)
    public Result<SIUpdate> cedeUnits(@RequestBody CedeUnitsJson request) {
        return requestFactory.getCedeUnitsRequest(request.siunits, request.nationId).process();
    }

    @GetMapping(path = SIRestPaths.UNIT_BUILT)
    public Result<List<SIUnitBuilt>> getUnitsBuilt() {
        return requestProcessor.process(nation -> {
            List<UnitBuildAudit> builds = unitDao.getBuildAudits(nation.getGameId(), nation.toString());
            return builds.stream()
                    .map(SIUnitBuilt::new)
                    .collect(Collectors.toList());
        });
    }
}
