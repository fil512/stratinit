package com.kenstevens.stratinit.client.server.rest.request.write;

import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Relation;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.client.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.client.server.daoservice.UnitDaoService;
import com.kenstevens.stratinit.client.server.rest.svc.PlayerWorldViewUpdate;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.RelationDao;
import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.dto.SICity;
import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.type.SectorCoords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Scope("prototype")
@Component
public class CedeCityRequest extends PlayerWriteRequest<SIUpdate> {
	private final SICity sicity;
    private final int nationId;
    @Autowired
    private SectorDaoService sectorDaoService;
    @Autowired
    private UnitDaoService unitDaoService;
    @Autowired
    private SectorDao sectorDao;
    @Autowired
    private GameDao gameDao;
    @Autowired
    private RelationDao relationDao;
    @Autowired
    private UnitDao unitDao;
    @Autowired
    private PlayerWorldViewUpdate playerWorldViewUpdate;

    public CedeCityRequest(SICity sicity, int nationId) {
        this.sicity = sicity;
        this.nationId = nationId;
    }

    @Override
	protected Result<SIUpdate> executeWrite() {
		Nation nation = getNation();
		SectorCoords coords = sicity.coords;
        City city = sectorDao.getCity(nation, coords);
        if (city == null) {
            return new Result<SIUpdate>("There is no city at " + coords, false);
        }
        if (!city.getNation().equals(nation)) {
            return new Result<SIUpdate>("You do not own the city at " + coords, false);
        }
        int gameId = nation.getGameId();
        Nation target = gameDao.getNation(gameId, nationId);

        Relation relation = relationDao.findRelation(nation, target);
        Relation reverse = relationDao.findRelation(target, nation);
        if (relation.getType() != RelationType.ALLIED || reverse.getType() != RelationType.ALLIED) {
            return new Result<SIUpdate>("You may only cede cities to an ally.", false);
        }

        Result<None> result = Result.trueInstance();
        Collection<Unit> units = unitDao.getUnits(getGame(), coords);
        for (Unit unit : units) {
            result.or(unitDaoService.cedeUnit(unit, target));
        }
        result.or(sectorDaoService.cedeCity(city, target));
		SIUpdate siupdate = playerWorldViewUpdate.getWorldViewUpdate(nation);
		return new Result<SIUpdate>(result.getMessages(), true,
				siupdate, result.getBattleLogs(), result.isSuccess());
	}

	@Override
	protected int getCommandCost() {
		return Constants.COMMAND_COST;
	}


}
