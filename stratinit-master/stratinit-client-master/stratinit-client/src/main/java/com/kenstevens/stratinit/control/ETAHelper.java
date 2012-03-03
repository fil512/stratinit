package com.kenstevens.stratinit.control;

import java.util.Date;

import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.model.Updatable;
import com.kenstevens.stratinit.util.UpdateManager;

public class ETAHelper {

	private boolean hasStarted = false;

	public ETAHelper(Data db) {
		Date now = new Date();
		if (db == null) {
			return;
		}
		Date gameStarts = db.getGameStarts();
		if (gameStarts == null) {
			return;
		}
		hasStarted = now.after(gameStarts);
	}

	public String getETA(Updatable updatable) {
		if (hasStarted) {
			return new UpdateManager(updatable).getETA();
		} else {
			return "-";
		}
	}

	public String getPercentDone(Updatable updatable) {
		if (hasStarted) {
			return "" + new UpdateManager(updatable).getPercentDone() + "%";
		} else {
			return "-";
		}
	}

}
