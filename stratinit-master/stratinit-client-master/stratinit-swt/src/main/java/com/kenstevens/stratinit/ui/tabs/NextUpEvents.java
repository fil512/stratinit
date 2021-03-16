package com.kenstevens.stratinit.ui.tabs;

import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.client.model.Data;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.client.model.UnitBase;
import com.kenstevens.stratinit.client.util.UpdateManager;

import java.util.*;

public class NextUpEvents {
	private final Data db;
	private final Map<Date, List<UnitEvent>> eventMap = new TreeMap<Date, List<UnitEvent>>();
	private final Map<Date, Integer> createMap = new TreeMap<Date, Integer>();
	private final Map<Date, Integer> moveMap = new TreeMap<Date, Integer>();
	private int maxCount = 0;

	public NextUpEvents(Data db) {
		this.db = db;
		populateMap();
	}

	private void populateMap() {
		if (db.getCityList() == null) {
			return;
		}
		for (City city : db.getCityList()) {
			UpdateManager updateManager = new UpdateManager(city);
			Date buildTime = roundTime(updateManager.getNextFutureUpdate());
			if (UnitBase.isNotUnit(city.getBuild())) {
				continue;
			}
			UnitEvent unitEvent = new UnitEvent(updateManager.getNextFutureUpdate(), city.getCoords(), city.getBuild(), UnitEvent.Type.CREATE);
			addUnitEvent(buildTime, unitEvent);
		}
		for (Unit unit : db.getUnitList()) {
			UpdateManager updateManager = new UpdateManager(unit);
			Date moveTime = roundTime(updateManager.getNextFutureUpdate());
			UnitEvent unitEvent = new UnitEvent(updateManager.getNextFutureUpdate(), unit.getCoords(), unit.getType(), UnitEvent.Type.MOVE);
			unitEvent.setMove(unit.getMobility());
			addUnitEvent(moveTime, unitEvent);
		}
		for (Date date : eventMap.keySet()) {
			List<UnitEvent> unitEventList = eventMap.get(date);
			int count = unitEventList.size();
			if (count > maxCount) {
				maxCount = count;
			}
			int moveEventCount = 0;
			int createEventCount = 0;
			for (UnitEvent unitEvent : unitEventList) {
				if (unitEvent.getEventType() == UnitEvent.Type.CREATE) {
					++createEventCount;
				} else if (unitEvent.getEventType() == UnitEvent.Type.MOVE) {
					++moveEventCount;
				}
			}
			createMap.put(date, createEventCount);
			moveMap.put(date, moveEventCount);
		}
	}

	public int getMaxCount() {
		return maxCount;
	}

	public int getCreateEventCount(Date date) {
		return createMap.get(date);
	}

	public int getMoveEventCount(Date date) {
		return moveMap.get(date);
	}

	public List<UnitEvent>getUnitEventList(Date date) {
		return eventMap.get(date);
	}

	private void addUnitEvent(Date buildTime, UnitEvent unitEvent) {
		List<UnitEvent>unitEventList = eventMap.get(buildTime);
		if (unitEventList == null) {
			unitEventList = new ArrayList<UnitEvent>();
			eventMap.put(buildTime, unitEventList);
		}
		unitEventList.add(unitEvent);
	}



	public Set<Date> getTimes() {
		return eventMap.keySet();
	}

	private Date roundTime(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int minute = 30 * (calendar.get(Calendar.MINUTE) / 30);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}
}
