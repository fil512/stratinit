package com.kenstevens.stratinit.balance;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.client.model.MoveCost;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.client.model.UnitBase;
import com.kenstevens.stratinit.client.server.rest.TwoPlayerBase;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.SectorType;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

@Disabled
public class ManualUnitBalancerRun extends TwoPlayerBase {
    public static final SectorCoords LAND_ATT_COORDS = new SectorCoords(2, 1);
    public static final SectorCoords SEA_ATT_COORDS = new SectorCoords(3, 1);
    public static final SectorCoords DEF_LAND = new SectorCoords(2, 0);
    public static final SectorCoords DEF_CITY = new SectorCoords(2, 2);
    public static final SectorCoords DEF_SEA = new SectorCoords(3, 0);
    private static final int NUM_ATTACKS = 100;

    @Test
    public void runUnitBalance() throws IOException {
        sectorDaoService.captureCity(nationThem, DEF_CITY);
        BalanceResultList balanceResultList = new BalanceResultList();
        declareWar();
        for (UnitType attackerUnitType : UnitBase.orderedUnitTypes()) {
            UnitBase attackerBase = UnitBase.getUnitBase(attackerUnitType);
            if (attackerIrrelevant(attackerBase)) {
                continue;
            }
            for (UnitType defenderUnitType : UnitBase.orderedUnitTypes()) {
//				UnitBase defenderBase = UnitBase.getUnitBase(defenderUnitType);
//				if (defenderIrrelevant(defenderBase)) {
//					continue;
//				}
                measureAttack(balanceResultList, attackerUnitType,
                        defenderUnitType, DEF_LAND);
                measureAttack(balanceResultList, attackerUnitType,
                        defenderUnitType, DEF_SEA);
                setBuild(DEF_CITY, UnitType.ZEPPELIN);
                measureAttack(balanceResultList, attackerUnitType,
                        defenderUnitType, DEF_CITY);
                setBuild(DEF_CITY, UnitType.INFANTRY);
                measureAttack(balanceResultList, attackerUnitType,
                        defenderUnitType, DEF_CITY);
                setBuild(DEF_CITY, UnitType.FIGHTER);
                measureAttack(balanceResultList, attackerUnitType,
                        defenderUnitType, DEF_CITY);
                setBuild(DEF_CITY, UnitType.DESTROYER);
                measureAttack(balanceResultList, attackerUnitType,
                        defenderUnitType, DEF_CITY);
            }
        }
        persist(balanceResultList);
    }

//	private boolean defenderIrrelevant(UnitBase defenderBase) {
//		return !defenderBase.isLand();
//	}

    private void measureAttack(BalanceResultList balanceResultList,
                               UnitType attackerUnitType, UnitType defenderUnitType,
                               SectorCoords defCoords) {
        if (UnitBase.isNotUnit(attackerUnitType)
                || UnitBase.isNotUnit(defenderUnitType)) {
            return;
        }
        List<AttackBalanceResult> results = Lists.newArrayList();
        for (int i = 0; i < NUM_ATTACKS; ++i) {
            measureOneAttack(results,
                    attackerUnitType, defenderUnitType, defCoords);
            if (results.isEmpty()) {
                return;
            }
        }
        BalanceResult balanceResult = aggregate(results, attackerUnitType, defenderUnitType, defCoords);
        balanceResultList.add(balanceResult);
    }

    private BalanceResult aggregate(List<AttackBalanceResult> results, UnitType attackerUnitType, UnitType defenderUnitType, SectorCoords defCoords) {
        String defSector;
        SectorType sectorType = testWorld.getSector(defCoords).getType();
        if (sectorType == SectorType.PLAYER_CITY) {
            defSector = sectorDao.getCity(nationThem, defCoords).getType().toString();
        } else {
            defSector = sectorType.toString();
        }
        int totNumAttacks = 0;
        int totAttHpRemaining = 0;
        int totDefHpRemaining = 0;
        int totAttackerDied = 0;
        for (AttackBalanceResult result : results) {
            totNumAttacks += result.numAttacks;
            if (result.isAttackerDied()) {
                totDefHpRemaining += result.defHpRemaining;
                ++totAttackerDied;
            } else {
                totAttHpRemaining += result.attHpRemaining;
            }
        }
        int totDefenderDied = NUM_ATTACKS - totAttackerDied;
        float avgNumAttacks = (float) totNumAttacks / NUM_ATTACKS;
        float avgAttHpRemaining = (float) totAttHpRemaining / totDefenderDied;
        float avgDefHpRemaining = (float) totDefHpRemaining / totAttackerDied;
        int percAttackerDied = 100 * totAttackerDied / NUM_ATTACKS;
        return new BalanceResult(attackerUnitType, defenderUnitType, avgNumAttacks, avgAttHpRemaining, avgDefHpRemaining, percAttackerDied, defSector);
    }

    private void persist(BalanceResultList balanceResultList)
            throws IOException {
        new BalanceResultPersister().save(balanceResultList);
    }

    private boolean attackerIrrelevant(UnitBase attackerBase) {
//		return !attackerBase.isLand();
        return attackerBase.isLaunchable()
                || (attackerBase.getAttack() == 0 && attackerBase
                .getBombPercentage() == 0);
    }

    private void measureOneAttack(
            List<AttackBalanceResult> results, UnitType attackerUnitType, UnitType defenderUnitType,
            SectorCoords defCoords) {
        UnitBase attBase = UnitBase.getUnitBase(attackerUnitType);
        Unit att;
        if (attBase.isNavy()) {
            att = unitDaoService.buildUnit(nationMe, SEA_ATT_COORDS,
                    attackerUnitType);
        } else {
            att = unitDaoService.buildUnit(nationMe, LAND_ATT_COORDS,
                    attackerUnitType);
        }
        Unit def = unitDaoService.buildUnit(nationThem, defCoords,
                defenderUnitType);
        AttackBalanceResult balanceResult = new AttackBalanceResult(att, def);
        do {
            nationMe.setCommandPoints(1024);
            att.resupply();
            att.addMobility();
            def.resupply();
            Result<MoveCost> result = moveUnits(makeUnitList(att),
                    defCoords);
            assertResult(result);
            balanceResult.updateResult(att, def);
        } while (balanceResult.hasDamage() && att.isAlive() && def.isAlive());

        if (balanceResult.hasDamage()) {
            results.add(balanceResult);
        }
        unitDaoService.killUnit(att);
        unitDaoService.killUnit(def);
    }
}
