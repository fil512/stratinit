package com.kenstevens.stratinit.server.rest.request.write;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.CityDaoService;
import com.kenstevens.stratinit.server.daoservice.UnitDaoService;
import com.kenstevens.stratinit.server.rest.svc.PlayerWorldViewUpdate;
import com.kenstevens.stratinit.server.rest.svc.UnitSvc;
import com.kenstevens.stratinit.type.UnitType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public abstract class BuildRequest extends PlayerWriteRequest<SIUpdate> {
    private final List<SIUnit> siunits;
    // FIXME remove?
    @Autowired
    protected UnitDaoService unitDaoService;
    @Autowired
    protected CityDaoService cityDaoService;
    @Autowired
    protected UnitDao unitDao;
    @Autowired
    private PlayerWorldViewUpdate playerWorldViewUpdate;
    @Autowired
    private UnitSvc unitSvc;

    public BuildRequest(List<SIUnit> siunits) {
        this.siunits = siunits;
    }

    @Override
    protected Result<SIUpdate> executeWrite() {
        Nation nation = getNation();
        Result<None> result = Result.falseInstance();
        boolean tried = false;
        List<Unit> units = unitSvc.siunitToUnit(siunits);
        for (Unit unit : units) {
            if (canBuild(unit)) {
                tried = true;
                result.or(buildAction(nation, unit));
                if (result.isSuccess()) {
                    break;
                }
            }
        }
        if (!tried) {
            List<String> unitsSelected = Lists.newArrayList();
            for (Unit unit : units) {
                unitsSelected.add(unit.getType() + " " + unit.getMobility() + " mob");
            }
            String unitsSelectedString = StringUtils.join(unitsSelected, ", ");
            result.setMessage("No engineer selected with at least " + mobilityCost() + " mobility.  Units Selected: [" + unitsSelectedString + "]");
        }
        SIUpdate siupdate = playerWorldViewUpdate.getWorldViewUpdate(nation);

        return new Result<SIUpdate>(result.getMessages(), result.isSuccess(),
                siupdate);
    }

    private boolean canBuild(Unit unit) {
        return unit.getType() == UnitType.ENGINEER && unit.getMobility() >= mobilityCost();
    }

    protected Result<None> buildAction(Nation nation, Unit unit) {
        if (unit == null) {
            return new Result<None>("Unit does not exist.", false);
        }
        int unitId = unit.getId();
        if (!unit.getNation().equals(nation)) {
            return new Result<None>("You do not own unit #" + unitId, false);
        }
        if (!unit.isEngineer()) {
            return new Result<None>("Only engineers may build a new city.  Unit #" + unitId + " is a " + unit.getType(), false);
        }
        if (unit.getMobility() < mobilityCost()) {
            return new Result<None>("An engineer must have " + mobilityCost() + " to build a new city.  Engineer #" + unitId + " only has " + unit.getMobility(), false);
        }
        return buildIt(unit);
    }

    protected abstract Result<None> buildIt(Unit unit);

    protected abstract int mobilityCost();
}
