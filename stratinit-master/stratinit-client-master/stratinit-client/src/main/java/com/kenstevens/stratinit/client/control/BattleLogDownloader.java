package com.kenstevens.stratinit.client.control;

import com.kenstevens.stratinit.client.model.BattleLogEntry;
import com.kenstevens.stratinit.client.model.BattleLogList;
import com.kenstevens.stratinit.client.model.Data;
import com.kenstevens.stratinit.type.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BattleLogDownloader {
	@Autowired
	private Data db;

	public char[] getText() {
		if (noData()) {
			return "".toCharArray();
		}
		StringBuilder builder = new StringBuilder();
		BattleLogList list = db.getBattleLogList();
		writeHeader(builder);
		for (BattleLogEntry entry : list) {
			builder.append(entry.getId()+"\t");
			builder.append(entry.getDateString()+"\t");
			builder.append(entry.getX()+"\t");
			builder.append(entry.getY()+"\t");
			builder.append(entry.getEvent()+"\t");
			builder.append(db.getNation(entry.getAttackerId())+"\t");
			if (entry.getDefenderId() == Constants.UNASSIGNED) {
				builder.append("\t");
			} else {
				builder.append(db.getNation(entry.getDefenderId())+"\t");
			}
			builder.append(entry.getOpponent()+"\t");
			builder.append(entry.getAttackerUnit()+"\t");
			builder.append(entry.getDefenderUnit()+"\t");
			builder.append(entry.getMyUnit()+"\t");
			builder.append(entry.getOpponentUnit()+"\t");
			builder.append(entry.getFlak()+"\t");
			builder.append(entry.getDamage()+"\t");
			builder.append(entry.getReturnDamage()+"\t");
			builder.append(entry.getType()+"\t");
			builder.append(entry.iAmAttacker()+"\t");
			builder.append(entry.isAttackerDied()+"\t");
			builder.append(entry.isDefenderDied()+"\t");
			builder.append(entry.isIDied()+"\t");
			builder.append(entry.isOtherDied()+"\t");
			builder.append(StringUtils.join(entry.getMessages(), "\t"));
			builder.append("\n");
		}
		return builder.toString().toCharArray();
	}

	private void writeHeader(StringBuilder builder) {
		builder.append("Id\t");
		builder.append("Date\t");
		builder.append("X\t");
		builder.append("Y\t");
		builder.append("Event\t");
		builder.append("Attacker\t");
		builder.append("Defender\t");
		builder.append("Opponent\t");
		builder.append("Attacker Unit\t");
		builder.append("Defender Unit\t");
		builder.append("My Unit\t");
		builder.append("Opponent Unit\t");
		builder.append("Flak\t");
		builder.append("Damage\t");
		builder.append("Return Fire\t");
		builder.append("Type\t");
		builder.append("I Am Attacker\t");
		builder.append("Attacker Died\t");
		builder.append("Defender Died\t");
		builder.append("I Died\t");
		builder.append("Other Died\t");
		builder.append("Messages");
		builder.append("\n");
	}

	public boolean noData() {
		return db == null || db.getBattleLogList() == null || db.getBattleLogList().isEmpty();
	}

}
