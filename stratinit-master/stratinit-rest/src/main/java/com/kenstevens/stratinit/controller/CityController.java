package com.kenstevens.stratinit.controller;

import com.kenstevens.stratinit.client.rest.SIRestPaths;
import com.kenstevens.stratinit.dto.SICityUpdate;
import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.CedeCityJson;
import com.kenstevens.stratinit.remote.request.UpdateCityJson;
import com.kenstevens.stratinit.server.rest.request.RequestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = SIRestPaths.BASE_PATH)
public class CityController {
    @Autowired
    private RequestFactory requestFactory;

    @PostMapping(path = SIRestPaths.UPDATE_CITY)
    public Result<SICityUpdate> updateCity(@RequestBody UpdateCityJson updateCityJson) {
        return requestFactory.getUpdateCityRequest(updateCityJson.sicity, updateCityJson.sicity.field).process();
    }

    @GetMapping(path = SIRestPaths.CITY)
    public Result<List<SICityUpdate>> getCities() {
        return requestFactory.getGetCitiesRequest().process();
    }

    @GetMapping(path = SIRestPaths.CITY_SEEN)
    public Result<List<SICityUpdate>> getSeenCities() {
        return requestFactory.getGetSeenCitiesRequest().process();
    }

    @PostMapping(path = SIRestPaths.CEDE_CITY)
    public Result<SIUpdate> cedeCity(@RequestBody CedeCityJson request) {
        return requestFactory.getCedeCityRequest(request.city, request.nationId).process();
    }
}
