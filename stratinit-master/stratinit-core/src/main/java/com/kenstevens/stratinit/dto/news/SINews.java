package com.kenstevens.stratinit.dto.news;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SINews implements Serializable {
	private static final long serialVersionUID = 1L;
	private Map<Integer, SINewsLogsDay> newsMap = new HashMap<Integer, SINewsLogsDay>();

	public SINewsLogsDay get(int day) {
		SINewsLogsDay retval = newsMap.get(day);
		if (retval == null) {
			retval = new SINewsLogsDay(day);
			newsMap.put(day, retval);
		}
		return retval;
	}

}
