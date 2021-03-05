package com.kenstevens.stratinit.client.model;

import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.MoveType;

import java.io.Serializable;

public class MoveCost implements Serializable {
	private static final long serialVersionUID = 1L;
	private final MoveType moveType;

	public MoveCost(MoveType moveType) {
		this.moveType = moveType;
	}
	
	public int getCommandCost() {
		// TODO REF set to MoveType.FAILED when no units are moved
		if (getMoveType() == MoveType.FAILED) {
			return 0;
		} else if (getMoveType() == MoveType.ATTACK) {
			return Constants.COMMAND_COST_ATTACK;
		} else if (getMoveType() == MoveType.LAUNCH_ICBM) {
			return Constants.COMMAND_COST_LAUNCH_SATELLITE;
		} else if (getMoveType() == MoveType.LAUNCH_SATELLITE) {
			return Constants.COMMAND_COST_LAUNCH_ICBM;
		} else if (getMoveType() == MoveType.TAKE_CITY) {
			return Constants.COMMAND_COST_TAKE_CITY;
		} else {
			return Constants.COMMAND_COST;
		}
	}

	public MoveType getMoveType() {
		return moveType;
	}
}
