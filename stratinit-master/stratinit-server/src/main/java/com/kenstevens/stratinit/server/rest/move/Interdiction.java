package com.kenstevens.stratinit.server.rest.move;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.RelationDao;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.model.AttackType;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.util.AttackHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Scope("prototype")
@Component
public class Interdiction {
    private final Unit targetUnit;
    private final SectorCoords excludeCoords;
    @Autowired
    private UnitDao unitDao;
    @Autowired
    private GameDao gameDao;
    @Autowired
    private RelationDao relationDao;
    @Autowired
    private SectorDaoService sectorDaoService;
    @Autowired
    private UnitMoveFactory unitMoveFactory;

    public Interdiction(Unit targetUnit, SectorCoords excludeCoords) {
        this.targetUnit = targetUnit;
        this.excludeCoords = excludeCoords;
    }

    /**
     * @return success means no interdiction
     */
    public Result<None> interdict() {
        Result<None> retval = Result.trueInstance();
        Collection<Nation> nations = unitDao
                .getOtherNationsThatSeeThisUnit(targetUnit);
        Map<Nation, RelationType> theirRelations = relationDao
                .getTheirRelationTypesAsMap(targetUnit.getNation());
        nationsInterdict(retval, nations, theirRelations);
        return retval;
    }

    private void nationsInterdict(Result<None> retval,
                                  Collection<Nation> nations, Map<Nation, RelationType> theirRelations) {
        for (Nation nation : nations) {
            if (!AttackHelper.canAttack(AttackType.INTERDICTION,
                    theirRelations.get(nation))) {
                continue;
            }
            WorldView worldView = sectorDaoService.getInterdictionWorldView(
                    targetUnit, nation);
            Collection<Unit> units;
            if (excludeCoords != null) {
                units = Lists.newArrayList(unitDao
                        .getUnitsThatCanCounterFireThisUnit(worldView, nation,
                                targetUnit, excludeCoords));
            } else {
                units = Lists.newArrayList(unitDao
                        .getUnitsThatCanInterdictThisUnit(worldView, nation,
                                targetUnit));
            }
            UnitsInterceptor unitsInterceptor = unitMoveFactory.getUnitsInterceptor(nation, worldView, targetUnit, excludeCoords, units);
            unitsInterceptor.unitsIntercept(retval);
        }
    }
}
