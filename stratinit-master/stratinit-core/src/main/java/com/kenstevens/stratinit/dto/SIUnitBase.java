package com.kenstevens.stratinit.dto;

import java.io.Serializable;

import com.kenstevens.stratinit.model.UnitBase;
import com.kenstevens.stratinit.type.CityType;
import com.kenstevens.stratinit.type.UnitType;


public class SIUnitBase implements Serializable {
	private static final long serialVersionUID = 1L;
	public CityType builtIn;
	public UnitType type;
	public int attack;
	public int hp;
	public int mobility;
	public int sight;
	public int productionTime; // hours
	public int tech;
	public int ammo;
	public int flak = 0;
	public boolean lightAir;
	public boolean canSeeSubs;
	public boolean requiresFuel;
	public int bombPercentage = 0;
	public int weight = 0;
	public int capacity = 0;
	public boolean navyCapital;
	public boolean navyCanAttackLand;
	public boolean landCanAttackShips;
	public boolean launchable;
	public boolean requiresSupply;
	public int blastRadius = 0;
	
	public SIUnitBase() {}

	public SIUnitBase(UnitBase unitBase) {
		ammo = unitBase.getAmmo();
		attack = unitBase.getAttack();
		bombPercentage = unitBase.getBombPercentage();
		builtIn = unitBase.getBuiltIn();
		canSeeSubs = unitBase.isCanSeeSubs();
		capacity = unitBase.getCapacity();
		flak = unitBase.getFlak();
		hp = unitBase.getFlak();
		lightAir = unitBase.isLightAir();
		mobility = unitBase.getMobility();
		productionTime = unitBase.getProductionTime();
		requiresFuel = unitBase.isRequiresFuel();
		sight = unitBase.getSight();
		tech = unitBase.getTech();
		type = unitBase.getType();
		weight = unitBase.getWeight();
		navyCanAttackLand = unitBase.isNavyCanAttackLand();
		navyCapital = unitBase.isNavyCapital();
		landCanAttackShips = unitBase.isLandCanAttackShips();
		launchable = unitBase.isLaunchable();
		blastRadius = unitBase.getBlastRadius();
		requiresSupply = unitBase.isRequiresSupply();
	}
}
