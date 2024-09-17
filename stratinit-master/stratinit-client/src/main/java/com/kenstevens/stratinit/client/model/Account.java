package com.kenstevens.stratinit.client.model;

import org.springframework.stereotype.Repository;

@Repository
public class Account {
	private String username = "";
	private String password = "";
	private Preferences preferences = new Preferences();
	private int x = -1;
	private int y = -1;
	private int width = 0;
	private int height = 0;

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

	public void load(Account account) {
		setPassword(account.getPassword());
		setUsername(account.getUsername());
		setPreferences(account.getPreferences());
		setX(account.x);
		setY(account.y);
		setWidth(account.width);
		setHeight(account.height);
	}

	public void setPreferences(Preferences preferences) {
		this.preferences = preferences;
	}

	public Preferences getPreferences() {
		return preferences;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void setSize(int x, int y) {
		this.width = x;
		this.height = y;
	}
}
