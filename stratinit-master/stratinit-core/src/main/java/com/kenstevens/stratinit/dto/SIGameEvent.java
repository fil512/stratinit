package com.kenstevens.stratinit.dto;

import java.util.Date;

public class SIGameEvent implements StratInitDTO {
	public int gameId;
	public String nationName;
	public Date eventTime;
	public String source;
	public String eventType;
	public String description;
	public Integer x;
	public Integer y;
	public String detail;

	public SIGameEvent() {
	}

	public SIGameEvent(int gameId, String nationName, Date eventTime, String source,
					   String eventType, String description, Integer x, Integer y, String detail) {
		this.gameId = gameId;
		this.nationName = nationName;
		this.eventTime = eventTime;
		this.source = source;
		this.eventType = eventType;
		this.description = description;
		this.x = x;
		this.y = y;
		this.detail = detail;
	}
}
