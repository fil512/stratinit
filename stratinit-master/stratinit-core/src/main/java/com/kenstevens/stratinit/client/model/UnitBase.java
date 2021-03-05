package com.kenstevens.stratinit.client.model;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.type.CityType;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.UnitBaseType;
import com.kenstevens.stratinit.type.UnitType;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class UnitBase {
	private static int LARGEST_INTERDICTS_SIGHT = -1;
	private static int MAX_TECH = -1;
	private CityType builtIn;
	private final UnitType type;
	private final int attack;
	private final int hp;
	private final int mobility;
	private final int sight;
	private final int productionTime; // hours
	private final int tech;
	private final int ammo;
	private int flak = 0;
	private boolean lightAir = false;
	private boolean canSeeSubs = false;
	private boolean requiresFuel = false;
	private int bombPercentage = 0;
	private int weight = 0;
	private int capacity = 0;
	private boolean navyCapital = false;
	private boolean navyCanAttackLand = false;
	private boolean landCanAttackShips = false;
	private boolean isLaunchable = false;
	private boolean requiresSupply = true;
	// TODO * add new attributes to downloaded unit base
	private boolean suppliesLand = false;
	private boolean suppliesNavy = false;
	private int blastRadius = 0;
	private boolean devastates = false;
	private boolean base = false;
	private final UnitBaseType unitBaseType;
	private static final List<UnitType> ORDERED_UNIT_TYPES = Lists
			.newArrayList();

	private static final Map<UnitType, UnitBase> UNIT_BASE_MAP = new HashMap<UnitType, UnitBase>();
	// DOC heavy and naval bombers have ammo
	static {
		// t c a
		// e o m s a m
		// c s o e t m h
		// h t b e t o p
		//
		LandUnitBase infantry = new LandUnitBase(UnitType.INFANTRY, 0, 8, 2, 1,
				2, 1, 5);
		LandUnitBase tank = new LandUnitBase(UnitType.TANK, 6, 10, 3, 1, 3, 1,
				9);
		NavalUnitBase transport = new NavalUnitBase(UnitType.TRANSPORT, 0, 12,
				6, 1, 0, 6, 8);
		NavalUnitBase supply = new NavalUnitBase(UnitType.SUPPLY, 0, 6, 6, 1,
				0, 10, 10);
		NavalUnitBase patrol = new NavalUnitBase(UnitType.PATROL, 0, 8, 8, 2,
				1, 1, 4);
		NavalUnitBase destroyer = new NavalUnitBase(UnitType.DESTROYER, 3, 14,
				8, 2, 5, 2, 14);
		NavalUnitBase battleship = new NavalUnitBase(UnitType.BATTLESHIP, 4,
				20, 6, 2, 12, 4, 40);
		NavalUnitBase submarine = new NavalUnitBase(UnitType.SUBMARINE, 6, 16,
				10, 1, 5, 8, 12);
		NavalUnitBase carrier = new NavalUnitBase(UnitType.CARRIER, 7, 20, 6,
				1, 4, 2, 24);
		NavalUnitBase cruiser = new NavalUnitBase(UnitType.CRUISER, 9, 20, 8,
				2, 8, 6, 30);
		// t c a
		// e o m s a m
		// c s o e t m h
		// h t b e t o p
		//
		AirUnitBase fighter = new AirUnitBase(UnitType.FIGHTER, 6, 8, 16, 1, 2,
				2, 4);
		AirUnitBase cargoPlane = new AirUnitBase(UnitType.CARGO_PLANE, 7, 14,
				30, 1, 0, 0, 8);
		AirUnitBase heavyBomber = new AirUnitBase(UnitType.HEAVY_BOMBER, 7, 18,
				30, 1, 0, 2, 8);
		AirUnitBase navalBomber = new AirUnitBase(UnitType.NAVAL_BOMBER, 8, 12,
				20, 1, 8, 1, 7);
		AirUnitBase helicopter = new AirUnitBase(UnitType.HELICOPTER, 9, 6, 16,
				2, 5, 2, 5);
		LandUnitBase engineer = new LandUnitBase(UnitType.ENGINEER, 0, 16, 3,
				1, 0, 10, 4);
		AirUnitBase zeppelin = new AirUnitBase(UnitType.ZEPPELIN, 0, 12, 5, 3,
				0, 4, 2);
		TechUnitBase satellite = new TechUnitBase(UnitType.SATELLITE, 10, 24,
				100, 0, 0, 0, 4);
		TechUnitBase icbm1 = new TechUnitBase(UnitType.ICBM_1, 12, 36, 30, 0,
				0, 0, 4);
		TechUnitBase icbm2 = new TechUnitBase(UnitType.ICBM_2, 14, 30, 40, 0,
				0, 0, 4);
		TechUnitBase icbm3 = new TechUnitBase(UnitType.ICBM_3, 16, 24, 50, 0,
				0, 0, 4);
		BaseUnitBase base = new BaseUnitBase(UnitType.BASE, 0, 24, 0, 0, 0, 0,
				1);
		TechUnitBase research = new TechUnitBase(UnitType.RESEARCH, 0, 24, 0,
				0, 0, 0, 1);
		// Weight
		engineer.setWeight(1);
		infantry.setWeight(1);
		tank.setWeight(1);
		fighter.setWeight(1);
		helicopter.setWeight(1);
		navalBomber.setWeight(1);
		icbm1.setWeight(1);
		icbm2.setWeight(1);
		icbm3.setWeight(1);
		// Supply
		engineer.setSuppliesLand(true);
		transport.setSuppliesLand(true);
		supply.setSuppliesLand(true);
		supply.setSuppliesNavy(true);
		// Carries Units
		transport.setCapacity(6);
		cargoPlane.setCapacity(4);
		carrier.setCapacity(8);
		helicopter.setCapacity(2);
		engineer.setCapacity(1);
		submarine.setCapacity(1);
		// Can See Subs
		destroyer.setCanSeeSubs(true);
		submarine.setCanSeeSubs(true);
		cruiser.setCanSeeSubs(true);
		navalBomber.setCanSeeSubs(true);
		helicopter.setCanSeeSubs(true);
		// Bomber
		zeppelin.setBombPercentage(10);
		navalBomber.setBombPercentage(25);
		heavyBomber.setBombPercentage(50);
		// Capital
		carrier.setNavyCapital(true);
		battleship.setNavyCapital(true);
		// Flak
		carrier.setFlak(4);
		battleship.setFlak(4);
		cruiser.setFlak(6);
		// Blast radius
		icbm2.setBlastRadius(1);
		icbm3.setBlastRadius(2);
		// Light Air
		fighter.setLightAir(true);
		navalBomber.setLightAir(true);
		helicopter.setLightAir(true);
		// Can Attack Land
		battleship.setNavyCanAttackLand(true);
		// Can Attack Ships
		tank.setLandCanAttackShips(true);
		// Launchable
		satellite.setLaunchable(true);
		icbm1.setLaunchable(true);
		icbm2.setLaunchable(true);
		icbm3.setLaunchable(true);
		// Devastates
		icbm1.setDevastates(true);
		icbm2.setDevastates(true);
		icbm3.setDevastates(true);
		// Exceptions
		zeppelin.setBuiltIn(CityType.TECH);
		engineer.setBuiltIn(CityType.TECH);
		// Patrol boat exception
		patrol.setRequiresSupply(false);
		supply.setRequiresSupply(false);
		// Base
		base.setBase(true);

		UNIT_BASE_MAP.put(UnitType.INFANTRY, infantry);
		UNIT_BASE_MAP.put(UnitType.TANK, tank);
		UNIT_BASE_MAP.put(UnitType.TRANSPORT, transport);
		UNIT_BASE_MAP.put(UnitType.SUPPLY, supply);
		UNIT_BASE_MAP.put(UnitType.PATROL, patrol);
		UNIT_BASE_MAP.put(UnitType.DESTROYER, destroyer);
		UNIT_BASE_MAP.put(UnitType.BATTLESHIP, battleship);
		UNIT_BASE_MAP.put(UnitType.SUBMARINE, submarine);
		UNIT_BASE_MAP.put(UnitType.CARRIER, carrier);
		UNIT_BASE_MAP.put(UnitType.CRUISER, cruiser);
		UNIT_BASE_MAP.put(UnitType.FIGHTER, fighter);
		UNIT_BASE_MAP.put(UnitType.CARGO_PLANE, cargoPlane);
		UNIT_BASE_MAP.put(UnitType.HEAVY_BOMBER, heavyBomber);
		UNIT_BASE_MAP.put(UnitType.NAVAL_BOMBER, navalBomber);
		UNIT_BASE_MAP.put(UnitType.HELICOPTER, helicopter);
		UNIT_BASE_MAP.put(UnitType.ENGINEER, engineer);
		UNIT_BASE_MAP.put(UnitType.ZEPPELIN, zeppelin);
		UNIT_BASE_MAP.put(UnitType.SATELLITE, satellite);
		UNIT_BASE_MAP.put(UnitType.ICBM_1, icbm1);
		UNIT_BASE_MAP.put(UnitType.ICBM_2, icbm2);
		UNIT_BASE_MAP.put(UnitType.ICBM_3, icbm3);
		UNIT_BASE_MAP.put(UnitType.BASE, base);
		UNIT_BASE_MAP.put(UnitType.RESEARCH, research);

		ORDERED_UNIT_TYPES.add(UnitType.INFANTRY);
		ORDERED_UNIT_TYPES.add(UnitType.TANK);
		ORDERED_UNIT_TYPES.add(UnitType.TRANSPORT);
		ORDERED_UNIT_TYPES.add(UnitType.SUPPLY);
		ORDERED_UNIT_TYPES.add(UnitType.PATROL);
		ORDERED_UNIT_TYPES.add(UnitType.DESTROYER);
		ORDERED_UNIT_TYPES.add(UnitType.BATTLESHIP);
		ORDERED_UNIT_TYPES.add(UnitType.SUBMARINE);
		ORDERED_UNIT_TYPES.add(UnitType.CARRIER);
		ORDERED_UNIT_TYPES.add(UnitType.CRUISER);
		ORDERED_UNIT_TYPES.add(UnitType.FIGHTER);
		ORDERED_UNIT_TYPES.add(UnitType.CARGO_PLANE);
		ORDERED_UNIT_TYPES.add(UnitType.HEAVY_BOMBER);
		ORDERED_UNIT_TYPES.add(UnitType.NAVAL_BOMBER);
		ORDERED_UNIT_TYPES.add(UnitType.HELICOPTER);
		ORDERED_UNIT_TYPES.add(UnitType.ENGINEER);
		ORDERED_UNIT_TYPES.add(UnitType.ZEPPELIN);
		ORDERED_UNIT_TYPES.add(UnitType.SATELLITE);
		ORDERED_UNIT_TYPES.add(UnitType.ICBM_1);
		ORDERED_UNIT_TYPES.add(UnitType.ICBM_2);
		ORDERED_UNIT_TYPES.add(UnitType.ICBM_3);
		ORDERED_UNIT_TYPES.add(UnitType.BASE);
		ORDERED_UNIT_TYPES.add(UnitType.RESEARCH);

		for (UnitType type : UnitType.values()) {
			UnitBase unitBase = getUnitBase(type);
			if (unitBase.tech > MAX_TECH) {
				MAX_TECH = unitBase.tech;
			}
			if (!unitBase.canInterdict()) {
				continue;
			}
			if (unitBase.sight > LARGEST_INTERDICTS_SIGHT) {
				LARGEST_INTERDICTS_SIGHT = unitBase.sight;
			}
		}
	}

	public static UnitBase getUnitBase(UnitType type) {
		return UNIT_BASE_MAP.get(type);
	}

	public UnitBase(UnitType type, CityType builtIn, int tech,
			int productionTime, int mobility, int sight, int attack, int ammo,
			int hp) {
		this.builtIn = builtIn;
		this.type = type;
		this.attack = attack;
		this.hp = hp;
		this.mobility = mobility;
		this.sight = sight;
		this.productionTime = productionTime;
		this.tech = tech;
		this.ammo = ammo;
		this.unitBaseType = getUnitBaseType();
	}

	public abstract UnitBaseType getUnitBaseType();

	public UnitType getType() {
		return type;
	}

	public int getAttack() {
		return attack;
	}

	public int getHp() {
		return hp;
	}

	public int getMobility() {
		return mobility;
	}

	public int getSight() {
		return sight;
	}

	public int getProductionTime() {
		return productionTime;
	}

	public final boolean isLand() {
		return unitBaseType == UnitBaseType.LAND;
	}

	public final boolean isNavy() {
		return unitBaseType == UnitBaseType.NAVY;
	}

	public final boolean isTech() {
		return unitBaseType == UnitBaseType.TECH;
	}

	public final boolean isAir() {
		return unitBaseType == UnitBaseType.AIR;
	}

	public CityType getBuiltIn() {
		return builtIn;
	}

	public int getTech() {
		return tech;
	}

	public int getAmmo() {
		return ammo;
	}

	public int getFlak() {
		return flak;
	}

	public boolean isLightAir() {
		return lightAir;
	}

	public boolean isEscort() {
		return type == UnitType.DESTROYER || type == UnitType.CRUISER;
	}

	public static int getMultiplier(UnitBase attacker, UnitBase defender) {
		// TODO * remove this multiplier and just double naval bomber damage?
		if (attacker.type == UnitType.NAVAL_BOMBER) {
			if (defender.isNavy()) {
				return 2;
			}
		} else if (attacker.type == UnitType.HELICOPTER) {
			if (defender.isLand() || defender.isSubmarine()) {
				return 2;
			}
		} else if (attacker.type == UnitType.FIGHTER) {
			if (defender.isAir() && defender.type != UnitType.FIGHTER) {
				return 2;
			}
		} else if (attacker.type == UnitType.DESTROYER
				|| attacker.type == UnitType.CRUISER) {
			if (defender.isSubmarine()) {
				return 3;
			}
		} else if (attacker.isSubmarine() && defender.isNavy()) {
			if (defender.isNavyCapital()) {
				return 4;
			} else {
				return 3;
			}
		}
		return 1;
	}

	public boolean isSubmarine() {
		return type == UnitType.SUBMARINE;
	}

	public void setCanSeeSubs(boolean canSeeSubs) {
		this.canSeeSubs = canSeeSubs;
	}

	public boolean isCanSeeSubs() {
		return canSeeSubs;
	}

	public void setLightAir(boolean lightAir) {
		this.lightAir = lightAir;
	}

	public void setFlak(int flak) {
		this.flak = flak;
	}

	public void setRequiresFuel(boolean requiresFuel) {
		this.requiresFuel = requiresFuel;
	}

	public boolean isRequiresFuel() {
		return requiresFuel;
	}

	public void setBuiltIn(CityType builtIn) {
		this.builtIn = builtIn;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getWeight() {
		return weight;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int getCapacity() {
		return capacity;
	}

	public boolean isBomber() {
		return bombPercentage > 0;
	}

	public void setBombPercentage(int bombPercentage) {
		this.bombPercentage = bombPercentage;
	}

	public int getBombPercentage() {
		return bombPercentage;
	}

	public void setNavyCapital(boolean navyCapital) {
		this.navyCapital = navyCapital;
	}

	public boolean isNavyCapital() {
		return navyCapital;
	}

	public void setNavyCanAttackLand(boolean navyCanAttackLand) {
		this.navyCanAttackLand = navyCanAttackLand;
	}

	public boolean isNavyCanAttackLand() {
		return navyCanAttackLand;
	}

	public void setLandCanAttackShips(boolean landCanAttackShips) {
		this.landCanAttackShips = landCanAttackShips;
	}

	public boolean isLandCanAttackShips() {
		return landCanAttackShips;
	}

	public void setBlastRadius(int blastRadius) {
		this.blastRadius = blastRadius;
	}

	public int getBlastRadius() {
		return blastRadius;
	}

	public void setLaunchable(boolean isLaunchable) {
		this.isLaunchable = isLaunchable;
	}

	public boolean isLaunchable() {
		return isLaunchable;
	}

	public void setRequiresSupply(boolean requiresSupply) {
		this.requiresSupply = requiresSupply;
	}

	public boolean isRequiresSupply() {
		return requiresSupply;
	}

	// TODO REF split up this class. It's got way too much stuff in it
	public String getNotes() {
		List<String> notes = new ArrayList<String>();
		if (lightAir) {
			notes.add("light");
		}
		if (canSeeSubs) {
			notes.add("sees subs");
		}
		if (navyCapital) {
			notes.add("capital");
		}
		if (navyCanAttackLand) {
			notes.add("attacks land");
		}
		if (landCanAttackShips) {
			notes.add("attacks ships");
		}
		if (isLaunchable) {
			notes.add("launches");
		}
		if (!requiresSupply) {
			notes.add("no supply");
		}
		if (blastRadius > 0) {
			notes.add("blast " + blastRadius);
		}
		if (capacity > 0) {
			notes.add("holds " + capacity);
		}
		if (weight > 0) {
			notes.add("weighs " + weight);
		}
		if (isAir() && !requiresFuel) {
			notes.add("no fuel");
		}
		if (bombPercentage > 0) {
			notes.add("bombs " + bombPercentage + "%");
		}
		if (isDevastates()) {
			notes.add("devastates");
		}
		if (isSuppliesLand()) {
			notes.add("supplies land");
		}
		if (isSuppliesNavy()) {
			notes.add("supplies navy");
		}
		if (isEscort()) {
			notes.add("escort");
		}

		return StringUtils.join(notes.iterator(), ", ");
	}

	public boolean getDevastates() {
		return isDevastates();
	}

	public void setDevastates(boolean devastates) {
		this.devastates = devastates;
	}

	public boolean isDevastates() {
		return devastates;
	}

	public void setBase(boolean base) {
		this.base = base;
	}

	public boolean isBase() {
		return base;
	}

	public boolean canInterdict() {
		if (isNavy()) {
			return type != UnitType.SUBMARINE;
		} else if (isLand()) {
			return isLandCanAttackShips();
		} else if (isAir()) {
			return type == UnitType.NAVAL_BOMBER;
		}
		return false;
	}

	public void setSuppliesLand(boolean suppliesLand) {
		this.suppliesLand = suppliesLand;
	}

	public boolean isSuppliesLand() {
		return suppliesLand;
	}

	public void setSuppliesNavy(boolean suppliesNavy) {
		this.suppliesNavy = suppliesNavy;
	}

	public boolean isSuppliesNavy() {
		return suppliesNavy;
	}

	public int getMaxMobility() {
		return mobility * Constants.MAX_MOBILITY_MULTIPLIER;
	}

	public int getSubSight() {
		if (!isCanSeeSubs()) {
			return 0;
		}
		if (type == UnitType.DESTROYER || type == UnitType.NAVAL_BOMBER) {
			return 1;
		}
		return getSight();
	}

	public static List<UnitType> orderedUnitTypes() {
		return ORDERED_UNIT_TYPES;
	}

	public static boolean isNotUnit(UnitType unitType) {
		return UnitType.BASE == unitType || UnitType.RESEARCH == unitType;
	}

	public static boolean isUnit(UnitType unitType) {
		return !isNotUnit(unitType);
	}

	public int getMaxFuel() {
		if (type == UnitType.ZEPPELIN) {
			return mobility * 6;
		} else {
			return mobility;
		}
	}

	public static List<UnitType> orderedUnitTypes(UnitBaseType unitBaseType) {
		List<UnitType> retval = Lists.newArrayList();
		for (UnitType unitType : ORDERED_UNIT_TYPES) {
			if (unitBaseType != UnitBase.getUnitBase(unitType)
					.getUnitBaseType()) {
				continue;
			}
			if (unitType == UnitType.RESEARCH) {
				continue;
			}
			retval.add(unitType);
		}
		return retval;
	}

	public static int getLargestInterdictsSight() {
		return LARGEST_INTERDICTS_SIGHT;
	}
	
	public static int getMaxTech() {
		return MAX_TECH;
	}
}
