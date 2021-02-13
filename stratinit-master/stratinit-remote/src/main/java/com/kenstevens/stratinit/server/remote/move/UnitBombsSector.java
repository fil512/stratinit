package com.kenstevens.stratinit.server.remote.move;

import com.kenstevens.stratinit.dto.SIBattleLog;
import com.kenstevens.stratinit.model.*;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.LogDaoService;
import com.kenstevens.stratinit.server.daoservice.UnitDaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Scope("prototype")
@Component
public class UnitBombsSector {
	@Autowired
	private LogDaoService logDaoService;
	@Autowired
	private UnitDaoService unitDaoService;

	private final Unit bomber;
	private final WorldSector targetSector;
	private final Collection<Unit> units;
	private final AttackReadiness attackReadiness;
	private final Nation actor;

	public UnitBombsSector(Nation actor, Unit bomber, WorldSector targetSector, Collection<Unit> units, AttackReadiness attackReadiness) {
		this.actor = actor;
		this.bomber = bomber;
		this.targetSector = targetSector;
		this.units = units;
		this.attackReadiness = attackReadiness;
	}

	public Result<None> bomb(int flakDamageIn) {
		bomber.decreaseAmmo();
		bomber.decreaseMobility(attackReadiness.costToAttack());

		double percent = (double)bomber.getBombPercentage() / 100;
		Result<None> retval = Result.trueInstance();
		int flakDamage = flakDamageIn;
		for (Unit target : units) {
			Result<None> bombResult = bombUnit(percent, target, flakDamage);
			retval.and(bombResult);
			flakDamage = BattleLog.NO_DAMAGE;
		}
		return retval;
	}


	private Result<None> bombUnit(double percent, Unit target, int flakDamage) {
		int damage = (int) Math.ceil(percent * target.getHp());
		unitDaoService.damage(target, damage);
		UnitAttackedBattleLog unitAttackedBattleLog = new UnitAttackedBattleLog(
				AttackType.BOMB, bomber, target, targetSector.getCoords(), flakDamage);
		unitAttackedBattleLog.setDamage(damage);
		if (!target.isAlive()) {
			unitAttackedBattleLog.setDefenderDied(true);
		}
		logDaoService.save(unitAttackedBattleLog);
		return new Result<None>(new SIBattleLog(actor, unitAttackedBattleLog));
	}
}
