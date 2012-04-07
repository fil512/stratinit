package com.kenstevens.stratinit.server.remote.move;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.model.AttackType;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.WorldSector;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.server.remote.TwoPlayerBase;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;

public class AttackUnitFinderHelperTest extends TwoPlayerBase {
	private static final SectorCoords ATT_LAND = new SectorCoords(0, 0);
	private static final SectorCoords DEF_LAND = new SectorCoords(0, 1);
	private static final SectorCoords ATT_WATER = new SectorCoords(3, 0);
	private static final SectorCoords DEF_WATER = new SectorCoords(4, 0);
	private WorldView worldView;
	private WorldSector landSector;
	private WorldSector waterSector;
	
	@Before
	public void getWorldView() {
		declareWar();
		worldView = sectorDaoService.getAllWorldView(nationMe);
		landSector = worldView.getWorldSector(DEF_LAND);
		waterSector = worldView.getWorldSector(DEF_WATER);
	}

	@Test
	public void infVsInf() {
		Unit att = unitDaoService.buildUnit(nationMe, ATT_LAND, UnitType.INFANTRY);
		Unit def = unitDaoService.buildUnit(nationThem, DEF_LAND, UnitType.INFANTRY);
		List<Unit> units = Lists.newArrayList(def);
		assertEquals(def,
				AttackingUnitFinderHelper.findUnitToAttack(AttackType.INITIAL_ATTACK, att, units, landSector, worldView));

	}
	
	@Test
	public void fighterInterceptFighter() {
		Unit att = unitDaoService.buildUnit(nationMe, ATT_LAND, UnitType.FIGHTER);
		Unit def = unitDaoService.buildUnit(nationThem, DEF_LAND, UnitType.FIGHTER);
		List<Unit> units = Lists.newArrayList(def);
		assertEquals(def,
				AttackingUnitFinderHelper.findUnitToAttack(AttackType.INTERCEPTION, att, units, landSector, worldView));
	}
	
	@Test
	public void fighterInterceptInterceptor() {
		Unit att = unitDaoService.buildUnit(nationMe, ATT_LAND, UnitType.FIGHTER);
		Unit def = unitDaoService.buildUnit(nationThem, DEF_LAND, UnitType.FIGHTER);
		def.setIntercepted(true);
		List<Unit> units = Lists.newArrayList(def);
		assertNull(
				AttackingUnitFinderHelper.findUnitToAttack(AttackType.INTERCEPTION, att, units, landSector, worldView));
	}
	
	@Test
	public void destVsLoadedXport() {
		Unit att = unitDaoService.buildUnit(nationMe, ATT_WATER, UnitType.DESTROYER);
		Unit def = unitDaoService.buildUnit(nationThem, DEF_WATER, UnitType.TRANSPORT);		
		Unit other = unitDaoService.buildUnit(nationThem, DEF_WATER, UnitType.INFANTRY);
		// This test only has meaning when other is first on the list
		List<Unit> units = Lists.newArrayList(other, def);
		assertEquals(def,
				AttackingUnitFinderHelper.findUnitToAttack(AttackType.INITIAL_ATTACK, att, units, waterSector, worldView));

	}
	
	@Test
	public void fighterVsFlock() {
		Unit att = unitDaoService.buildUnit(nationMe, ATT_WATER, UnitType.FIGHTER);
		Unit def = unitDaoService.buildUnit(nationThem, DEF_WATER, UnitType.FIGHTER);		
		Unit other = unitDaoService.buildUnit(nationThem, DEF_WATER, UnitType.CARGO_PLANE);
		// This test only has meaning when other is first on the list
		List<Unit> units = Lists.newArrayList(other, def);
		assertEquals(other,
				AttackingUnitFinderHelper.findUnitToAttack(AttackType.INITIAL_ATTACK, att, units, waterSector, worldView));

	}
	
	
	@Test
	public void fighterInterceptFlock() {
		Unit att = unitDaoService.buildUnit(nationMe, ATT_WATER, UnitType.FIGHTER);
		Unit def = unitDaoService.buildUnit(nationThem, DEF_WATER, UnitType.FIGHTER);		
		Unit other = unitDaoService.buildUnit(nationThem, DEF_WATER, UnitType.CARGO_PLANE);
		// This test only has meaning when other is first on the list
		List<Unit> units = Lists.newArrayList(other, def);
		assertEquals(def,
				AttackingUnitFinderHelper.findUnitToAttack(AttackType.INTERCEPTION, att, units, waterSector, worldView));

	}
	
	
	@Test
	public void fighterVsLandAir() {
		Unit att = unitDaoService.buildUnit(nationMe, ATT_WATER, UnitType.FIGHTER);
		Unit def = unitDaoService.buildUnit(nationThem, DEF_WATER, UnitType.HELICOPTER);		
		Unit other = unitDaoService.buildUnit(nationThem, DEF_WATER, UnitType.INFANTRY);
		// This test only has meaning when other is first on the list
		List<Unit> units = Lists.newArrayList(other, def);
		assertEquals(def,
				AttackingUnitFinderHelper.findUnitToAttack(AttackType.INITIAL_ATTACK, att, units, waterSector, worldView));

	}
	
	
	@Test
	public void fighterInterceptLandAir() {
		Unit att = unitDaoService.buildUnit(nationMe, ATT_WATER, UnitType.FIGHTER);
		Unit def = unitDaoService.buildUnit(nationThem, DEF_WATER, UnitType.HELICOPTER);		
		Unit other = unitDaoService.buildUnit(nationThem, DEF_WATER, UnitType.INFANTRY);
		// This test only has meaning when other is first on the list
		List<Unit> units = Lists.newArrayList(other, def);
		assertEquals(def,
				AttackingUnitFinderHelper.findUnitToAttack(AttackType.INTERCEPTION, att, units, waterSector, worldView));

	}
	

	// FIXME finish this test
}
