package com.kenstevens.stratinit.model;

import org.eclipse.swt.graphics.Point;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.springframework.stereotype.Repository;

@Root
@Repository
public class Account {
	@Element
	private String username = "";
	@Element
	private String password = "";
	@Element
	private Preferences preferences = new Preferences();
	@Element
	private int x = -1;
	@Element
	private int y = -1;
	@Element
	private int width = 0;
	@Element
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

	public void setLocation(Point point) {
		this.x = point.x;
		this.y = point.y;
	}

	public void setSize(Point point) {
		this.width = point.x;
		this.height = point.y;
	}

}
