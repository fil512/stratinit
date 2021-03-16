package com.kenstevens.stratinit.client.model;

import com.kenstevens.stratinit.dto.SIBattleLog;
import com.kenstevens.stratinit.type.NewsCategory;
import com.kenstevens.stratinit.type.SectorCoords;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class BattleLogEntry extends DatedItem {
	public static final String CITY = "city";

	public NewsCategory getType() {
		return type;
	}

	public SectorCoords getCoords() {
		return coords;
	}

	protected final int id;
	protected final SectorCoords coords;
	protected final List<String> messages;
	protected final int attackerId;
	protected final int defenderId;
	protected final String attackerUnit;
	protected final String defenderUnit;
	protected final int damage;
	protected final int returnDamage;
	private final boolean attackerDied;
	private final boolean defenderDied;
	protected final NewsCategory type;
	protected final int flak;
	private final boolean iAmAttacker;

	protected String opponent;

	public BattleLogEntry(SIBattleLog silog) {
		id = silog.id;
		setDate(silog.date);
		coords = silog.coords;
		messages = silog.messages;
		attackerId = silog.attackerId;
		defenderId = silog.defenderId;
		attackerUnit = silog.attackerUnit;
		defenderUnit = silog.defenderUnit;
		damage = silog.damage;
		returnDamage = silog.returnDamage;
		attackerDied = silog.attackerDied;
		defenderDied = silog.defenderDied;
		type = silog.type;
		flak = silog.flak;
		iAmAttacker = silog.iAmAttacker;
	}


	public String getMessage() {
		return StringUtils.join(messages, "\n");
	}

	public List<String> getMessages() {
		return messages;
	}

	public String getOpponent() {
		return opponent == null ? "world" : opponent;
	}

	public int getX() {
		return coords.x;
	}

	public int getY() {
		return coords.y;
	}

	public void setOpponent(String opponent) {
		this.opponent = opponent;
	}

	public Integer getId() {
		return id;
	}

	public String getMyUnit() {
		if (iAmAttacker()) {
			return attackerUnit;
		}
		return defenderUnit == null ? CITY : defenderUnit;
	}

	public String getOpponentUnit() {
		if (iAmAttacker()) {
			return defenderUnit == null ? CITY : defenderUnit;
		}
		return attackerUnit;
	}

	public boolean isAttackerDied() {
		return attackerDied;
	}

	public boolean isDefenderDied() {
		return defenderDied;
	}

	public boolean iAmAttacker() {
		return iAmAttacker;
	}

	public boolean isIDied() {
		if (iAmAttacker) {
			return attackerDied;
		} else {
			return defenderDied;
		}
	}

	public boolean isOtherDied() {
		if (iAmAttacker) {
			return defenderDied;
		} else {
			return attackerDied;
		}
	}

	public int getAttackerId() {
		return attackerId;
	}

	public int getDefenderId() {
		return defenderId;
	}

	public String getAttackerUnit() {
		return attackerUnit;
	}

	public String getDefenderUnit() {
		return defenderUnit;
	}

	public int getDamage() {
		return damage;
	}

	public int getReturnDamage() {
		return returnDamage;
	}

	public int getFlak() {
		return flak;
	}

}
