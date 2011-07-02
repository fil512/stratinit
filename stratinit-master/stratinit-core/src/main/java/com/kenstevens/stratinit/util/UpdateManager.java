package com.kenstevens.stratinit.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.kenstevens.stratinit.model.Updatable;

public final class UpdateManager {
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("HH:mm dd");
	private static final SimpleDateFormat SHORT_FORMAT = new SimpleDateFormat("HH:mm");

	private final Updatable updatable;

	public UpdateManager(Updatable updatable) {
		this.updatable = updatable;
	}

	public String getETA() {
		if (updatable.getLastUpdated() == null) {
			return "";
		}
		return FORMAT.format(getNextFutureUpdate());
	}

	public String getShortETA() {
		if (updatable.getLastUpdated() == null) {
			return "";
		}
		return SHORT_FORMAT.format(getNextFutureUpdate());
	}

	public Date getNextFutureUpdate() {
		UpdateCalculator updateCalculator = getUpdateCalculator();
		return updateCalculator.nextFutureUpdate(updatable.getLastUpdated());
	}

	private UpdateCalculator getUpdateCalculator() {
		return new UpdateCalculator(updatable.isBlitz(), updatable.getUpdatePeriodMilliseconds());
	}


	public int missedUpdates() {
		return getUpdateCalculator().getMissedUpdates(updatable.getLastUpdated());
	}

	public Date getNextMissedBuildTime() {
		return getUpdateCalculator().getNextMissedBuildTime(updatable.getLastUpdated());
	}

	public String getPercentDone() {
		if (updatable.getLastUpdated() == null) {
			return "0";
		}
		long started = updatable.getLastUpdated().getTime();
		long now = new Date().getTime();
		long finished = getNextFutureUpdate().getTime();
		if (finished == started) {
			return "0";
		}
		int percent = (int)((int)100*(now - started) / (finished - started));
		return ""+percent;
	}
}
