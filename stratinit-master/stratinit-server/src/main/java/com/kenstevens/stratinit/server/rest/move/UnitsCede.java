package com.kenstevens.stratinit.server.rest.move;

import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Relation;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.client.model.WorldSector;
import com.kenstevens.stratinit.dao.NationDao;
import com.kenstevens.stratinit.dao.RelationDao;
import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.UnitDaoService;
import com.kenstevens.stratinit.server.rest.svc.PlayerWorldViewUpdate;
import com.kenstevens.stratinit.server.svc.FogService;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.type.SectorCoords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Scope("prototype")
@Component
public class UnitsCede {
    private final Nation nation;
    private final List<Unit> units;
    private final int nationId;
    private final WorldView worldView;
    @Autowired
    private NationDao nationDao;
    @Autowired
    private RelationDao relationDao;
    @Autowired
    private PlayerWorldViewUpdate playerWorldViewUpdate;
    @Autowired
    private UnitDaoService unitDaoService;
    @Autowired
    protected FogService fogService;

    public UnitsCede(Nation nation, List<Unit> units, int nationId,
                     WorldView worldView) {
        this.nation = nation;
        this.units = units;
        this.nationId = nationId;
        this.worldView = worldView;
    }

    public Result<SIUpdate> cede() {
        if (units.isEmpty()) {
            return new Result<SIUpdate>("Nothing to cede.", false);
        }

        int gameId = nation.getGameId();

        List<Unit> unitsToCede = new ArrayList<Unit>();
        Nation target = nationDao.getNation(gameId, nationId);

        if (!checkAllied(target)) {
            return new Result<SIUpdate>("You may only cede units to an ally.", false);
        }

        SectorCoords coords = units.get(0).getCoords();

        removeDistantUnits(unitsToCede, coords);

        if (unitsToCede.isEmpty()) {
            return new Result<SIUpdate>("Nothing to cede.", false);
        }

        Result<None> result = cedeUnits(unitsToCede, target);
        fogService.survey(nation);
        fogService.survey(target);
        SIUpdate siupdate = playerWorldViewUpdate.getWorldViewUpdate(nation);

        return new Result<SIUpdate>(result.getMessages(), true, siupdate,
                result.getBattleLogs(), result.isSuccess());
    }

    private Result<None> cedeUnits(List<Unit> unitsToCede, Nation target) {
        Result<None> result = Result.trueInstance();
        for (Unit unitToCede : unitsToCede) {
            result.or(unitDaoService.cedeUnit(unitToCede, target));
        }
        return result;
    }

    private void removeDistantUnits(List<Unit> unitsToCede, SectorCoords coords) {
        WorldSector worldSector = worldView.getWorldSector(coords);
        for (Unit unit : units) {
            // Exclude units not in the same sector as the first unit
            if (!unit.getCoords().equals(coords)) {
                continue;
            }
            unitsToCede.add(unit);
            if (unit.carriesUnits()) {
                unitsToCede.addAll(unitDaoService.getPassengers(unit,
                        worldSector));

            }
        }
    }

    private boolean checkAllied(Nation target) {
        Relation relation = relationDao.findRelation(nation, target);
        Relation reverse = relationDao.findRelation(target, nation);
        return relation.getType() == RelationType.ALLIED && reverse.getType() == RelationType.ALLIED;
    }
}
