package com.kenstevens.stratinit.client.gwt.model;

import java.io.Serializable;


public class GWTPlayer implements Serializable, GWTEntity<String> {

	private static final long serialVersionUID = 1L;
	private String username;
	private String password;
	private String email;
	private boolean enabled = true;
	private boolean emailGameMail = false;
	private int wins;
	private int played;

	public GWTPlayer() {
	}

	public String getId() {
		return username;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getWins() {
		return wins;
	}

	public void setWins(int wins) {
		this.wins = wins;
	}

	public int getPlayed() {
		return played;
	}

	public void setPlayed(int played) {
		this.played = played;
	}

	public int getWinPercentage() {
		if (played == 0) {
			return 0;
		}
		return 100 * wins / played;
	}

	public StratInitListGridRecord<String, GWTPlayer> getListGridRecord() {
		return new PlayerListGridRecord(this);
	}

	public void setEmailGameMail(boolean emailGameMail) {
		this.emailGameMail = emailGameMail;
	}

	public boolean isEmailGameMail() {
		return emailGameMail;
	}
}
