package com.kenstevens.stratinit.dto;

import com.kenstevens.stratinit.client.model.*;
import com.kenstevens.stratinit.type.NewsCategory;
import com.kenstevens.stratinit.type.SectorCoords;

import java.util.Date;
import java.util.List;


public class SIBattleLog implements StratInitDTO {
	private static final long serialVersionUID = 7730883345140043294L;
	public int id;
	public Date date;
	public SectorCoords coords;
	public List<String> messages;
	public int attackerId;
	public int defenderId = -1;
	public String attackerUnit;
	public String defenderUnit;
	public int damage;
	public int returnDamage;
	public boolean attackerDied;
	public boolean defenderDied;
	public NewsCategory type;
	public int flak;
	public boolean iAmAttacker = false;

	public SIBattleLog() {}

	private SIBattleLog(BattleLog log) {
		date = log.getDate();
		id = log.getId();
		attackerId = log.getAttacker().getNationId();
		if (log.getDefender() != null) {
			defenderId = log.getDefender().getNationId();
		}
		coords = log.getCoords();
		flak = log.getFlakDamage();
		type = log.getNewsCategory();
		if (log instanceof UnitAttackedBattleLog) {
			UnitAttackedBattleLog ualog = (UnitAttackedBattleLog) log;
			damage = ualog.getDamage();
			returnDamage = ualog.getReturnDamage();
			attackerDied = ualog.isAttackerDied();
			defenderDied = ualog.isDefenderDied();
		} else if (log instanceof FlakBattleLog) {
			FlakBattleLog flog = (FlakBattleLog)log;
			damage = flog.getFlakDamage();
			attackerDied = true;
		} else if (log instanceof CityCapturedBattleLog) {
			CityCapturedBattleLog clog = (CityCapturedBattleLog)log;
			attackerDied = clog.isAttackerDied();
		}
	}

	public SIBattleLog(Nation nation, BattleLog log) {
		this(log);
		addPrivateData(nation, log);
	}

	public final void addPrivateData(Nation nation, BattleLog log) {
		attackerUnit = log.getAttackerUnit().toString();
		if (log instanceof UnitAttackedBattleLog) {
			UnitAttackedBattleLog ualog = (UnitAttackedBattleLog) log;
			defenderUnit = ualog.getDefenderUnit().toString();
		}
		if (nation.equals(log.getAttacker())) {
			messages = log.getAttackerMessages();
			iAmAttacker = true;
		} else if (nation.equals(log.getDefender())){
			messages = log.getDefenderMessages();
		}
	}
}