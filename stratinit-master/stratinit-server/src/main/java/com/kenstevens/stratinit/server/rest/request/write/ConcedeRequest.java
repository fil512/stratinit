package com.kenstevens.stratinit.server.rest.request.write;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.MessageDaoService;
import com.kenstevens.stratinit.server.daoservice.RelationDaoService;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.daoservice.UnitDaoService;
import com.kenstevens.stratinit.server.rest.svc.PlayerWorldViewUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Scope("prototype")
@Component
public class ConcedeRequest extends PlayerWriteRequest<SIUpdate> {
	@Autowired
    private UnitDaoService unitDaoService;
    @Autowired
    private UnitDao unitDao;
    @Autowired
    private SectorDao sectorDao;
    @Autowired
    private SectorDaoService sectorDaoService;
    @Autowired
    private MessageDaoService messageDaoService;
    @Autowired
    private RelationDaoService relationDaoService;
    @Autowired
    private GameDao gameDao;
    @Autowired
    private PlayerWorldViewUpdate playerWorldViewUpdate;

    public ConcedeRequest() {
    }

    @Override
    protected Result<SIUpdate> executeWrite() {
        Nation nation = getNation();

        List<City> cities = Lists.newArrayList(sectorDao.getCities(nation));
        List<Unit> units = Lists.newArrayList(unitDao.getUnits(nation));

        Collection<Nation> allies = relationDaoService.getAllies(nation);
        Nation ally = null;
        int allyCityCount = 0;
        if (allies.size() > 0) {
            ally = allies.iterator().next();
            allyCityCount = sectorDao.getNumberOfCities(ally);
        }

        Result<None> result = Result.trueInstance();
        if (ally != null && allyCityCount > 0) {
            for (Unit unit : units) {
				result.or(unitDaoService.cedeUnit(unit, ally));
			}
			for (City city : cities) {
				result.or(sectorDaoService.cedeCity(city, ally));
			}
			messageDaoService.postBulletin(nation.getGame(), nation + " conceeded.  All cities and units transferred to " + ally + ".", null);
		} else {
			for (Unit unit : units) {
				result.or(unitDaoService.disbandUnit(unit));
			}
			for (City city : cities) {
				result.or(sectorDaoService.destroyCity(city));
			}
			messageDaoService.postBulletin(nation.getGame(), nation + " conceeded.  All cities and units destroyed.", null);
		}
		sectorDaoService.survey(nation);
		if (ally != null) {
			sectorDaoService.survey(ally);
		}
		SIUpdate siupdate = playerWorldViewUpdate.getWorldViewUpdate(nation);

		return new Result<SIUpdate>(result.getMessages(), true, siupdate,
				result.getBattleLogs(), result.isSuccess());
	}

	@Override
	protected int getCommandCost() {
		return 0;
	}


}
