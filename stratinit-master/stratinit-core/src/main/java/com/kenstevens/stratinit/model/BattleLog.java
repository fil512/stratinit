package com.kenstevens.stratinit.model;

import com.kenstevens.stratinit.news.NewsWorthy;
import com.kenstevens.stratinit.type.SectorCoords;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@MappedSuperclass
public abstract class BattleLog implements NewsWorthy {
	public static final int NO_DAMAGE = 0;
	@Id
	@SequenceGenerator(name="battle_id_seq", sequenceName="battle_id_sequence", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="battle_id_seq")
	private Integer id;
	protected AttackType attackType;
	@ManyToOne
	private Nation attacker;
	@ManyToOne(optional=true)
	protected Nation defender;
	@ManyToOne
	protected Unit attackerUnit;
	@Embedded
	protected SectorCoords coords;
	private int flakDamage = NO_DAMAGE;

	private Date date = new Date();

	public BattleLog() {
	}

	public BattleLog(AttackType attackType, Nation attacker, Nation defender, Unit attackerUnit,
			SectorCoords coords, int flakDamage) {
		this.attackType =attackType;
		this.attacker = attacker;
		this.defender =defender;
		this.attackerUnit = attackerUnit;
		this.coords = coords;
		this.flakDamage = flakDamage;
	}

	public BattleLog(AttackType attackType, Nation attacker, Nation defender, Unit attackerUnit,
			SectorCoords coords) {
		this(attackType, attacker, defender, attackerUnit, coords, NO_DAMAGE);
	}

	public Nation getAttacker() {
		return attacker;
	}

	public void setAttacker(Nation attacker) {
		this.attacker = attacker;
	}

	public Nation getDefender() {
		return defender;
	}

	public void setDefender(Nation defender) {
		this.defender = defender;
	}

	public Unit getAttackerUnit() {
		return attackerUnit;
	}

	public void setAttackerUnit(Unit attackerUnit) {
		this.attackerUnit = attackerUnit;
	}


	public Integer getId() {
		return id;
	}

	public void setId(Integer battleLogId) {
		this.id = battleLogId;
	}

	public abstract List<String> getAttackerMessages();
	public abstract List<String> getDefenderMessages();

	public String getAttackerMessage() {
		return StringUtils.join(getAttackerMessages().iterator(), ".  ")+".";
	}

	public String getDefenderMessage() {
		return StringUtils.join(getAttackerMessages().iterator(), ".  ")+".";
	}


	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setCoords(SectorCoords coords) {
		this.coords = coords;
	}

	public SectorCoords getCoords() {
		return coords;
	}

	public void setFlakDamage(int flakDamage) {
		this.flakDamage = flakDamage;
	}

	public int getFlakDamage() {
		return flakDamage;
	}

	public void setAttackType(AttackType attackType) {
		this.attackType = attackType;
	}

	public AttackType getAttackType() {
		return attackType;
	}

}
