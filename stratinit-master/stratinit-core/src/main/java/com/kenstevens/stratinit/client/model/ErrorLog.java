package com.kenstevens.stratinit.client.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class ErrorLog {
	@Id
	@SequenceGenerator(name="errorlog_id_seq", sequenceName="errorlog_id_sequence", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="errorlog_id_seq")
	private Integer id;
	private Date date;
	private Integer gameId;
	private String username;
	@Lob
	private String stackTrace;

	public ErrorLog() {}

	public ErrorLog(int gameId, String username, String stackTrace) {
		this.setGameId(gameId);
		this.setUsername(username);
		this.stackTrace = stackTrace;
		this.setDate(new Date());
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}

	public Integer getGameId() {
		return gameId;
	}

	public void setGameId(Integer gameId) {
		this.gameId = gameId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
