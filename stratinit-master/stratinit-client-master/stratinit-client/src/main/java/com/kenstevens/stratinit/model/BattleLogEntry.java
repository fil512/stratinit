package com.kenstevens.stratinit.model;

import com.kenstevens.stratinit.dto.SIBattleLog;
import com.kenstevens.stratinit.type.NewsCategory;
import com.kenstevens.stratinit.type.SectorCoords;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;

import java.util.List;

public class BattleLogEntry extends DatedItem {
	public static final String CITY = "city";

	public NewsCategory getType() {
		return type;
	}

	public SectorCoords getCoords() {
		return coords;
	}

	public int getColor() {
		return color;
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
	protected int color;
	protected String event;
	private final boolean iAmAttacker;

	public String getEvent() {
		return event;
	}

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
		setEvent();
		setColour();
	}

	private void setColour() {
		if (attackerDied) {
			if (iAmAttacker) {
				color = SWT.COLOR_RED;
			} else {
				color = SWT.COLOR_GREEN;
			}
		} else if (defenderDied) {
			if (iAmAttacker) {
				color = SWT.COLOR_GREEN;
			} else {
				color = SWT.COLOR_RED;
			}
		}
	}

	private void setEvent() {
		if (type == NewsCategory.CONQUEST) {
			if (iAmAttacker) {
				color = SWT.COLOR_DARK_GREEN;
				event = "lost";
			} else {
				event = "took";
				color = SWT.COLOR_RED;
			}
		} else if (type == NewsCategory.AIR_DEFENCE) {
			if (iAmAttacker) {
				color = SWT.COLOR_DARK_RED;
				event = "flak";
			} else {
				color = SWT.COLOR_DARK_GREEN;
				event = "flak";
			}
		} else if (type == NewsCategory.NEWS_FROM_THE_FRONT) {
			if (attackerDied || defenderDied) {
				event = "killed";
			} else {
				event = "hit";
			}
			if (iAmAttacker) {
				event = event + " by";
				color = SWT.COLOR_DARK_GREEN;
			} else {
				color = SWT.COLOR_DARK_RED;
			}
		} else {
			color = SWT.COLOR_BLACK;
			event = "unknown";
		}
		if (!iAmAttacker) {
			event = "* " + event;
		}
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
