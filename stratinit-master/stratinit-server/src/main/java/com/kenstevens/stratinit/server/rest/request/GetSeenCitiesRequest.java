package com.kenstevens.stratinit.server.rest.request;

import com.kenstevens.stratinit.dto.SICityUpdate;
import com.kenstevens.stratinit.server.rest.svc.CitySvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class GetSeenCitiesRequest extends PlayerRequest<List<SICityUpdate>> {
    @Autowired
    private CitySvc citySvc;

    @Override
    protected List<SICityUpdate> execute() {
        return citySvc.getSeenCities(getNation());
    }
}
