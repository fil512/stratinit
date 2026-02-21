package com.kenstevens.stratinit.server.service;

import com.kenstevens.stratinit.client.model.AttackType;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.client.model.WorldSector;
import com.kenstevens.stratinit.move.Attack;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.server.rest.TwoPlayerBase;
import com.kenstevens.stratinit.type.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SectorServiceTwoTest extends TwoPlayerBase {
    private static final SectorCoords CITY = new SectorCoords(1, 4);
    private static final SectorCoords PORT = new SectorCoords(2, 2);
    private static final SectorCoords SEA = new SectorCoords(3, 2);
    private static final SectorCoords ESEA = new SectorCoords(4, 2);
    private static final SectorCoords ECITY = new SectorCoords(8, 4);
    private static final SectorCoords LAND = new SectorCoords(7, 4);
    private static final SectorCoords ELAND = new SectorCoords(8, 3);

    @Test
    public void world() {
        declareWar();
        Unit fight = unitService.buildUnit(nationMe, CITY,
                UnitType.FIGHTER);
        unitService.buildUnit(nationMe, SEA,
                UnitType.CARRIER);
        unitService.buildUnit(nationThem, ESEA,
                UnitType.CARRIER);
        unitService.buildUnit(nationThem, ELAND,
                UnitType.INFANTRY);
        cityService.captureCity(nationMe, PORT);
        setBuild(PORT, UnitType.TRANSPORT);

        WorldView WORLD = sectorService.getAllWorldView(nationMe);

        WorldSector city = WORLD.getWorldSector(CITY);
        WorldSector port = WORLD.getWorldSector(PORT);
        WorldSector sea = WORLD.getWorldSector(SEA);
        WorldSector esea = WORLD.getWorldSector(ESEA);
        WorldSector ecity = WORLD.getWorldSector(ECITY);
        WorldSector land = WORLD.getWorldSector(LAND);
        WorldSector eland = WORLD.getWorldSector(ELAND);

        assertTrue(ecity.airCanAttack());
        assertTrue(eland.airCanAttack());
        assertTrue(esea.airCanAttack());
        assertFalse(land.airCanAttack());

        assertTrue(land.airCanEnter());

        assertTrue(new Attack(ecity).canAttack(WORLD, AttackType.INITIAL_ATTACK, fight));
        assertTrue(new Attack(eland).canAttack(WORLD, AttackType.INITIAL_ATTACK, fight));
        assertTrue(new Attack(esea).canAttack(WORLD, AttackType.INITIAL_ATTACK, fight));
        assertFalse(new Attack(land).canAttack(WORLD, AttackType.INITIAL_ATTACK, fight));

        assertFalse(city.canRefuel(fight));
        assertFalse(port.canRefuel(fight));
        assertTrue(sea.canRefuel(fight));
        assertFalse(ecity.canRefuel(fight));

        assertEquals(CityType.FORT, city.getCityType());
        assertEquals(CityType.PORT, port.getCityType());
        assertNull(sea.getCityType());
        assertEquals(CityType.FORT, ecity.getCityType());

        assertEquals(RelationType.ME, city.getMyRelation());
        assertEquals(RelationType.ME, sea.getMyRelation());
        assertEquals(RelationType.WAR, ecity.getMyRelation());
        assertEquals(RelationType.WAR, esea.getMyRelation());
        assertEquals(RelationType.WAR, eland.getMyRelation());
        assertNull(land.getMyRelation());

        assertEquals(RelationType.ME, city.getTheirRelation());
        assertEquals(RelationType.ME, sea.getTheirRelation());
        assertEquals(RelationType.WAR, ecity.getTheirRelation());
        assertEquals(RelationType.WAR, esea.getTheirRelation());
        assertEquals(RelationType.WAR, eland.getTheirRelation());
        assertNull(land.getTheirRelation());

        assertEquals(SectorType.PLAYER_CITY, city.getType());
        assertEquals(SectorType.PLAYER_CITY, port.getType());
        assertEquals(SectorType.WATER, sea.getType());
        assertEquals(SectorType.PLAYER_CITY, ecity.getType());
        assertEquals(SectorType.LAND, land.getType());

        assertFalse(new Attack(city).isAttackable());
        assertFalse(new Attack(port).isAttackable());
        assertFalse(new Attack(sea).isAttackable());
        assertFalse(new Attack(land).isAttackable());
        assertTrue(new Attack(ecity).isAttackable());
        assertTrue(new Attack(esea).isAttackable());
        assertTrue(new Attack(eland).isAttackable());

        assertFalse(city.isHoldsFriendlyCarrier());
        assertFalse(port.isHoldsFriendlyCarrier());
        assertTrue(sea.isHoldsFriendlyCarrier());
        assertFalse(ecity.isHoldsFriendlyCarrier());
        assertFalse(eland.isHoldsFriendlyCarrier());
        assertFalse(esea.isHoldsFriendlyCarrier());
        assertFalse(land.isHoldsFriendlyCarrier());

        assertFalse(city.isHoldsMyCapital());
        assertTrue(sea.isHoldsMyCapital());
        assertFalse(ecity.isHoldsMyCapital());
        assertFalse(eland.isHoldsMyCapital());
        assertFalse(esea.isHoldsMyCapital());
        assertFalse(land.isHoldsMyCapital());

        // isHoldsMyTransportWithRoom is tested elsewhere

        assertFalse(city.isHoldsShipAtSea());
        assertFalse(port.isHoldsShipAtSea());
        assertTrue(sea.isHoldsShipAtSea());
        assertTrue(esea.isHoldsShipAtSea());
        assertFalse(ecity.isHoldsShipAtSea());
        assertFalse(eland.isHoldsShipAtSea());
        assertFalse(land.isHoldsShipAtSea());

        assertTrue(city.isHoldsUnits());
        assertFalse(port.isHoldsUnits());
        assertTrue(sea.isHoldsUnits());
        assertTrue(ecity.isHoldsUnits());
        assertTrue(eland.isHoldsUnits());
        assertTrue(esea.isHoldsUnits());
        assertFalse(land.isHoldsUnits());

        assertTrue(city.isPlayerCity());
        assertTrue(port.isPlayerCity());
        assertFalse(sea.isPlayerCity());
        assertTrue(ecity.isPlayerCity());
        assertFalse(eland.isPlayerCity());
        assertFalse(esea.isPlayerCity());
        assertFalse(land.isPlayerCity());

        assertTrue(city.isSuppliesLand());
        assertTrue(port.isSuppliesLand());
        assertFalse(sea.isSuppliesLand());
        assertFalse(ecity.isSuppliesLand());
        assertFalse(esea.isSuppliesLand());
        assertFalse(eland.isSuppliesLand());
        assertFalse(land.isSuppliesLand());

        assertFalse(city.isSuppliesNavy());
        assertTrue(port.isSuppliesNavy());
        assertFalse(sea.isSuppliesNavy());
        assertFalse(ecity.isSuppliesNavy());
        assertFalse(esea.isSuppliesNavy());
        assertFalse(eland.isSuppliesNavy());
        assertFalse(land.isSuppliesNavy());

        assertFalse(city.isWater());
        assertFalse(port.isWater());
        assertTrue(sea.isWater());
        assertFalse(ecity.isWater());
        assertTrue(esea.isWater());
        assertFalse(eland.isWater());
        assertFalse(land.isWater());

        assertTrue(city.landCanEnter());
        assertTrue(port.landCanEnter());
        assertFalse(sea.landCanEnter());
        assertTrue(ecity.landCanEnter());
        assertFalse(esea.landCanEnter());
        assertTrue(eland.landCanEnter());
        assertTrue(land.landCanEnter());

        assertFalse(city.navyCanEnter());
        assertTrue(port.navyCanEnter());
        assertFalse(sea.navyCanEnter());
        assertFalse(ecity.navyCanEnter());
        assertFalse(esea.navyCanEnter());
        assertFalse(eland.navyCanEnter());
        assertFalse(land.navyCanEnter());
    }

}
