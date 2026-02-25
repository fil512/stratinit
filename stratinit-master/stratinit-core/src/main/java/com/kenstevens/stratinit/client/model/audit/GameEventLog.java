package com.kenstevens.stratinit.client.model.audit;

import com.kenstevens.stratinit.type.EventSource;
import com.kenstevens.stratinit.type.GameEventType;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "game_event_log")
public class GameEventLog {

	@Id
	@SequenceGenerator(name = "gel_id_seq", sequenceName = "game_event_log_id_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gel_id_seq")
	private Integer id;
	private int gameId;
	private String nationName;
	private Date eventTime;
	@Enumerated(EnumType.STRING)
	private EventSource source;
	@Enumerated(EnumType.STRING)
	private GameEventType eventType;
	private String description;
	private Integer x;
	private Integer y;
	@Column(length = 2000)
	private String detail;

	public GameEventLog() {
	}

	public GameEventLog(int gameId, String nationName, Date eventTime, EventSource source,
						GameEventType eventType, String description, Integer x, Integer y, String detail) {
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

	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }
	public int getGameId() { return gameId; }
	public void setGameId(int gameId) { this.gameId = gameId; }
	public String getNationName() { return nationName; }
	public void setNationName(String nationName) { this.nationName = nationName; }
	public Date getEventTime() { return eventTime; }
	public void setEventTime(Date eventTime) { this.eventTime = eventTime; }
	public EventSource getSource() { return source; }
	public void setSource(EventSource source) { this.source = source; }
	public GameEventType getEventType() { return eventType; }
	public void setEventType(GameEventType eventType) { this.eventType = eventType; }
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	public Integer getX() { return x; }
	public void setX(Integer x) { this.x = x; }
	public Integer getY() { return y; }
	public void setY(Integer y) { this.y = y; }
	public String getDetail() { return detail; }
	public void setDetail(String detail) { this.detail = detail; }
}
