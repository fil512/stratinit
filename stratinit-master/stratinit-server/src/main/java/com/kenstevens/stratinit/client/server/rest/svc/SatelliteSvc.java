package com.kenstevens.stratinit.client.server.rest.svc;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.client.model.LaunchedSatellite;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.dao.RelationDao;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.dto.SILaunchedSatellite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class SatelliteSvc {
    @Autowired
    private UnitDao unitDao;
    @Autowired
    private RelationDao relationDao;

    public List<SILaunchedSatellite> getLaunchedSatellites(Nation nation) {
        Collection<LaunchedSatellite> sats = Lists.newArrayList(unitDao.getSatellites(nation));
        for (Nation ally : relationDao.getAllies(nation)) {
            sats.addAll(unitDao.getSatellites(ally));
        }
        List<SILaunchedSatellite> retval = new ArrayList<SILaunchedSatellite>();
        for (LaunchedSatellite sat : sats) {
            retval.add(new SILaunchedSatellite(sat));
        }
        return retval;
    }
}
