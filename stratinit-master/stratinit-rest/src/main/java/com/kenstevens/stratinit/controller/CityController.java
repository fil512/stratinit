package com.kenstevens.stratinit.controller;

import com.kenstevens.stratinit.client.rest.SIRestPaths;
import com.kenstevens.stratinit.dto.SICityUpdate;
import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.remote.request.CedeCityJson;
import com.kenstevens.stratinit.remote.request.UpdateCityJson;
import com.kenstevens.stratinit.server.rest.request.RequestProcessor;
import com.kenstevens.stratinit.server.rest.request.WriteProcessor;
import com.kenstevens.stratinit.server.rest.svc.CitySvc;
import com.kenstevens.stratinit.server.rest.svc.CityUpdater;
import com.kenstevens.stratinit.type.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = SIRestPaths.BASE_PATH)
@Tag(name = "Cities")
public class CityController {
    @Autowired
    private RequestProcessor requestProcessor;
    @Autowired
    private WriteProcessor writeProcessor;
    @Autowired
    private CitySvc citySvc;
    @Autowired
    private CityUpdater cityUpdater;

    @PostMapping(path = SIRestPaths.UPDATE_CITY)
    @Operation(summary = "Update city production or specialization")
    public SICityUpdate updateCity(@Valid @RequestBody UpdateCityJson updateCityJson) {
        return writeProcessor.process(
                nation -> cityUpdater.updateCity(nation, updateCityJson.sicity, updateCityJson.sicity.field),
                Constants.COMMAND_COST);
    }

    @GetMapping(path = SIRestPaths.CITY)
    @Operation(summary = "Get all cities owned by the current player")
    public List<SICityUpdate> getCities() {
        return requestProcessor.process(nation -> citySvc.getCities(nation));
    }

    @GetMapping(path = SIRestPaths.CITY_SEEN)
    @Operation(summary = "Get all visible cities from other nations")
    public List<SICityUpdate> getSeenCities() {
        return requestProcessor.process(nation -> citySvc.getSeenCities(nation));
    }

    @PostMapping(path = SIRestPaths.CEDE_CITY)
    @Operation(summary = "Transfer a city to an allied nation")
    public SIUpdate cedeCity(@Valid @RequestBody CedeCityJson request) {
        return writeProcessor.process(
                nation -> citySvc.cedeCity(nation, request.city, request.nationId, nation.getGame()),
                Constants.COMMAND_COST);
    }
}
