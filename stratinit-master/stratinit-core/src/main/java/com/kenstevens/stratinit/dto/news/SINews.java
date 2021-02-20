package com.kenstevens.stratinit.dto.news;

import com.kenstevens.stratinit.dto.StratInitDTO;

import java.util.HashMap;
import java.util.Map;

public class SINews implements StratInitDTO {
	private static final long serialVersionUID = 1L;
	private final Map<Integer, SINewsLogsDay> newsMap = new HashMap<Integer, SINewsLogsDay>();

	public SINewsLogsDay get(int day) {
		SINewsLogsDay retval = newsMap.get(day);
		if (retval == null) {
			retval = new SINewsLogsDay(day);
			newsMap.put(day, retval);
		}
		return retval;
	}

}
