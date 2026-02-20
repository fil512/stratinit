package com.kenstevens.stratinit.client.model;

import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import com.querydsl.core.annotations.QueryInit;
import org.apache.commons.lang3.time.DateUtils;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Unit extends GameUpdatable {
	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(name = "unit_id_seq", sequenceName = "unit_id_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "unit_id_seq")
	private Integer id;
	@ManyToOne
	@QueryInit("nationPK.game")
	private Nation nation;
	@Embedded
	private SectorCoords coords;
	private UnitType type;
	private int mobility;
	private int hp;
	private int ammo;
	private int fuel;
	private int sight;
	private boolean canSeeSubs;
	private boolean alive;
	private Date created;
	@Transient
	private UnitMove unitMove;
	@Transient
	private UnitBase unitBase;
	@Transient
	private boolean intercepted = false;
	@Transient
	private boolean moved = false;

	public Unit() {
	}

	public Unit(Nation nation, UnitType type, SectorCoords coords, Date date) {
		this.nation = nation;
		this.setType(type);
		this.coords = coords;
		this.mobility = unitBase.getMobility();
		if (this.type == UnitType.CARRIER) {
			this.sight = nation.getRadarRadius();
		} else {
			this.sight = this.unitBase.getSight();
		}
		this.hp = unitBase.getHp();
		this.resupply();
		this.alive = true;
		if (getParentGame().hasStarted()) {
			this.setLastUpdated(new Date());
		} else {
			this.setLastUpdated(this.getParentGame().getStartTime());
		}
		this.created = date;
	}

	public Unit(Nation nation, UnitType type, SectorCoords coords) {
		this(nation, type, coords, new Date());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Unit other = (Unit) obj;
		if (id == null) {
			return other.id == null;
		} else return id.equals(other.id);
	}

	public void setId(Integer unitId) {
		this.id = unitId;
	}

	public Integer getId() {
		return id;
	}

	public void setNation(Nation nation) {
		this.nation = nation;
	}

	public Nation getNation() {
		return nation;
	}

	public void setType(UnitType type) {
		this.type = type;
		this.unitBase = UnitBase.getUnitBase(type);
		this.canSeeSubs = this.unitBase.isCanSeeSubs();
	}

	public UnitType getType() {
		return type;
	}

	public UnitBase getUnitBase() {
		if (unitBase == null) {
			unitBase = UnitBase.getUnitBase(type);
		}
		return unitBase;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public boolean isAlive() {
		return alive;
	}

	@Override
	public long getUpdatePeriodMilliseconds() {
		return Constants.HOURS_BETWEEN_UNIT_UPDATES * DateUtils.MILLIS_PER_HOUR;
	}

	public void setMobility(int mobility) {
		this.mobility = mobility;
	}

	public int getMobility() {
		return mobility;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public int getHp() {
		return hp;
	}

	public final void resupply() {
		this.ammo = getUnitBase().getAmmo();
		if (requiresFuel()) {
			this.fuel = getUnitBase().getMaxFuel();
		}
	}

	public int getAmmo() {
		return ammo;
	}

	public void setAmmo(int ammo) {
		this.ammo = ammo;
	}

	public void setFuel(int fuel) {
		this.fuel = fuel;
	}

	public int getFuel() {
		return fuel;
	}
	
	public void healPercent(int percent) {
		if (atMaxHP()) {
			return;
		}
		
		int maxHP = getUnitBase().getHp();
		int hps = percent * maxHP / 100;
		hp += hps;

		if (hp > maxHP) {
			hp = maxHP;
		}
	}

	public void addMobility() {
		mobility += getUnitBase().getMobility();
		if (mobility > getMaxMobility()) {
			mobility = getMaxMobility();
		}
	}

	public int getMaxMobility() {
		return getUnitBase().getMaxMobility();
	}

	public boolean atMaxMobility() {
		return mobility == getMaxMobility();
	}

	public boolean isAir() {
		return getUnitBase().isAir();
	}

	public boolean isLand() {
		return getUnitBase().isLand();
	}

	public boolean isNavy() {
		return getUnitBase().isNavy();
	}

	public boolean isLightAir() {
		return getUnitBase().isLightAir();
	}

	public String toMyString() {
		return "your " + getType().toString().toLowerCase();
	}

	public String toString() {
		return getType().toString().toLowerCase();
	}

	public String toEnemyString() {
		return getNation().getName() + "'s " + getType();
	}

	public void damage(int damage) {
		hp -= damage;
		if (hp <= 0) {
			hp = 0;
			alive = false;
		}
	}

	public void kill() {
		hp = 0;
		alive = false;
	}

	public void decreaseMobility(int cost) {
		mobility -= cost;
		if (mobility < 0) {
			throw new IllegalStateException("Mobility went below zero");
		}
	}

	public void decreaseAmmo() {
		ammo -= 1;
		if (ammo < 0) {
			throw new IllegalStateException("Ammo went below zero");
		}
	}

	public void setCoords(SectorCoords coords) {
		this.coords = coords;
	}

	public SectorCoords getCoords() {
		return coords;
	}

	public Game getParentGame() {
		return nation.getNationPK().getGame();
	}

	public int getGameId() {
		return getParentGame().getId();
	}

	public void decreaseFuel() {
		fuel -= 1;
		if (fuel < 0) {
			throw new IllegalStateException("Fuel went below zero");
		}
	}

	public void setSight(int sight) {
		this.sight = sight;
	}

	public int getSight() {
		return sight;
	}

	public void setCanSeeSubs(boolean canSeeSubs) {
		this.canSeeSubs = canSeeSubs;
	}

	public boolean isCanSeeSubs() {
		return canSeeSubs;
	}

	public final boolean requiresFuel() {
		return getUnitBase().isRequiresFuel();
	}

	public int getWeight() {
		return getUnitBase().getWeight();
	}

	public int getCapacity() {
		return getUnitBase().getCapacity();
	}

	public boolean carriesUnits() {
		return getCapacity() > 0;
	}



	public boolean isSubmarine() {
		return type == UnitType.SUBMARINE;
	}

	public boolean isBomber() {
		return getUnitBase().isBomber();
	}

	public int getAttack() {
		return getUnitBase().getAttack();
	}

	public int getBombPercentage() {
		return getUnitBase().getBombPercentage();
	}

	public int getFlak() {
		return getUnitBase().getFlak();
	}

	public boolean isCapital() {
		if (!isNavy()) {
			return false;
		}
		return getUnitBase().isNavyCapital();
	}

	public boolean isLandCanAttackShips() {
		if (!isLand()) {
			return false;
		}
		return getUnitBase().isLandCanAttackShips();
	}

	public boolean isNavyCanAttackLand() {
		if (!isNavy()) {
			return false;
		}
		return getUnitBase().isNavyCanAttackLand();
	}

	public int getBlastRadius() {
		return getUnitBase().getBlastRadius();
	}

	public boolean isTech() {
		return getUnitBase().isTech();
	}

	public boolean atMaxHP() {
		return hp == getUnitBase().getHp();
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getCreated() {
		return created;
	}

	public String getFuelString() {
		return "" + fuel;
	}

	public String getBlastRadiusString() {
		return "" + getBlastRadius();
	}

	public String getHpString() {
		return "" + hp;
	}

	public int getX() {
		return getCoords().x;
	}

	public int getY() {
		return getCoords().y;
	}

	public boolean isDamaged() {
		return !atMaxHP();
	}

	public boolean isHurt() {
		return isDamaged() && hp <= 2;
	}

	public boolean isLaunchable() {
		return getUnitBase().isLaunchable();
	}

	public boolean requiresSupply() {
		return getUnitBase().isRequiresSupply();
	}

	@Override
	public Object getKey() {
		return id;
	}

	public String getAmmoString() {
		return ammo > 0 ? "" + ammo : "";
	}

	public int getProductionTime() {
		return getUnitBase().getProductionTime();
	}

	public boolean devastates() {
		return getUnitBase().getDevastates();
	}

	public boolean isScratched() {
		int percentHurt = 100 * hp / unitBase.getHp();
		return percentHurt < 70;
	}

	public void setIntercepted(boolean intercepted) {
		this.intercepted = intercepted;
	}

	public boolean isIntercepted() {
		return intercepted;
	}

	public boolean isTransport() {
		return type == UnitType.TRANSPORT;
	}

	public boolean isSupply() {
		return type == UnitType.SUPPLY;
	}

	public boolean isZeppelin() {
		return type == UnitType.ZEPPELIN;
	}

	public boolean atMaxAmmo() {
		return ammo == getUnitBase().getAmmo();
	}

	public void addAmmo() {
		ammo += 1;
		if (ammo > getUnitBase().getAmmo()) {
			ammo = getUnitBase().getAmmo();
		}
	}

	public boolean isSuppliesLand() {
		return getUnitBase().isSuppliesLand();
	}

	public boolean isSuppliesNavy() {
		return getUnitBase().isSuppliesNavy();
	}

	public boolean supplies(Unit targetUnit) {
		if (targetUnit.isLand()) {
			return isSuppliesLand();
		} else if (targetUnit.isNavy()) {
			return isSuppliesNavy();
		}
		return false;
	}

	public int getSubSight() {
		return getUnitBase().getSubSight();
	}

	public boolean isEngineer() {
		return type == UnitType.ENGINEER;
	}
	
	@Override
	public boolean isKeyUnique() {
		return true;
	}

	public boolean isInfantry() {
		return type == UnitType.INFANTRY;
	}

	public boolean isCarrier() {
		return type == UnitType.CARRIER;
	}

	public boolean isExplorer() {
		return type == UnitType.ZEPPELIN || type == UnitType.PATROL;
	}

	public void setUnitMove(UnitMove unitMove) {
		this.unitMove = unitMove;
	}

	public UnitMove getUnitMove() {
		return unitMove;
	}

	public void setMoved(boolean moved) {
		this.moved = moved;
	}

	public boolean isMoved() {
		return moved;
	}

	public boolean isEscort() {
		return getUnitBase().isEscort();
	}
}
