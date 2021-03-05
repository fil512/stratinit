package com.kenstevens.stratinit.client.control;

import com.kenstevens.stratinit.client.model.Data;
import com.kenstevens.stratinit.client.model.Updatable;
import com.kenstevens.stratinit.client.util.UpdateManager;

import java.util.Date;

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
