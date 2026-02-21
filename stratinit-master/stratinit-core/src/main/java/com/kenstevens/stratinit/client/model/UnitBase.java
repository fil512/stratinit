package com.kenstevens.stratinit.client.model;

import com.kenstevens.stratinit.type.CityType;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.UnitBaseType;
import com.kenstevens.stratinit.type.UnitType;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
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
	private static final List<UnitType> ORDERED_UNIT_TYPES;

	private static final Map<UnitType, UnitBase> UNIT_BASE_MAP;

	static {
		UNIT_BASE_MAP = UnitBaseLoader.load();
		ORDERED_UNIT_TYPES = new ArrayList<>(UNIT_BASE_MAP.keySet());

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
		List<UnitType> retval = new ArrayList<>();
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
