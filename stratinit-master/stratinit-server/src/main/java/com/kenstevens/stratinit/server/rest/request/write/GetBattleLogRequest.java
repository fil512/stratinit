package com.kenstevens.stratinit.server.rest.request.write;

import com.kenstevens.stratinit.client.model.CityCapturedBattleLog;
import com.kenstevens.stratinit.client.model.FlakBattleLog;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.UnitAttackedBattleLog;
import com.kenstevens.stratinit.dao.LogDao;
import com.kenstevens.stratinit.dto.SIBattleLog;
import com.kenstevens.stratinit.remote.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Scope("prototype")
@Component
public class GetBattleLogRequest extends PlayerWriteRequest<List<SIBattleLog>> {
    @Autowired
    private LogDao logDao;

    @Override
    protected Result<List<SIBattleLog>> executeWrite() {
        Nation nation = getNation();
        nation.setNewBattle(false);
        gameDaoService.merge(nation);
        List<SIBattleLog> retval = new ArrayList<SIBattleLog>();
        Iterable<CityCapturedBattleLog> cbattleLogs = logDao
                .getCityCapturedBattleLogs(nation);
        for (CityCapturedBattleLog log : cbattleLogs) {
            SIBattleLog silog = new SIBattleLog(nation, log);
            retval.add(silog);
        }
        List<UnitAttackedBattleLog> uabattleLogs = new ArrayList<>();
        logDao.getUnitAttackedBattleLogs(nation).forEach(uabattleLogs::add);
        for (UnitAttackedBattleLog log : uabattleLogs) {
            SIBattleLog silog = new SIBattleLog(nation, log);
            retval.add(silog);
        }
        List<FlakBattleLog> fbattleLogs = new ArrayList<>();
        logDao.getFlakBattleLogs(nation).forEach(fbattleLogs::add);
        for (FlakBattleLog log : fbattleLogs) {
            SIBattleLog silog = new SIBattleLog(nation, log);
            retval.add(silog);
        }
        return Result.make(retval);
    }

    @Override
    protected int getCommandCost() {
        return 0;
    }
}
