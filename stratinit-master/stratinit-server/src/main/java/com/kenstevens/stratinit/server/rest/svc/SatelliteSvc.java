package com.kenstevens.stratinit.server.rest.svc;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.dto.SILaunchedSatellite;
import com.kenstevens.stratinit.model.LaunchedSatellite;
import com.kenstevens.stratinit.model.Nation;
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
    private GameDao gameDao;

    public List<SILaunchedSatellite> getLaunchedSatellites(Nation nation) {
        Collection<LaunchedSatellite> sats = Lists.newArrayList(unitDao.getSatellites(nation));
        for (Nation ally : gameDao.getAllies(nation)) {
            sats.addAll(unitDao.getSatellites(ally));
        }
        List<SILaunchedSatellite> retval = new ArrayList<SILaunchedSatellite>();
        for (LaunchedSatellite sat : sats) {
            retval.add(new SILaunchedSatellite(sat));
        }
        return retval;
    }
}
