package com.kenstevens.stratinit.client.util;

import com.kenstevens.stratinit.remote.Result;


public final class BuildHelper {
	private BuildHelper() {}

	public static Result<Boolean> powerLimitReached(long cityCount, long power) {
		long limit = powerLimit(cityCount);
		Result<Boolean> result = new Result<Boolean>(
				"Power = "+power+
				"\nCity Count = "+cityCount+
				"\nLimit = "+limit, power >= limit, power >= limit);
		return result;
	}

	public static long powerLimit(long cityCount) {
		return cityCount * 5;
	}
}
