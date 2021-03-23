package com.kenstevens.stratinit.client.model;

import org.springframework.stereotype.Repository;

@Repository
public class Preferences {
	private boolean showBuilding = false;
	private boolean playSounds = true;
	private boolean liberator = true;
	private boolean switchMouse = false;
	private boolean showFOW = true;
	private boolean canvasScroll = true;
	
	public void setShowBuilding(boolean showBuilding) {
		this.showBuilding = showBuilding;
	}
	public boolean isShowBuilding() {
		return showBuilding;
	}
	public void setPlaySounds(boolean playSounds) {
		this.playSounds = playSounds;
	}
	public boolean isPlaySounds() {
		return playSounds;
	}
	public void setLiberator(boolean liberator) {
		this.liberator = liberator;
	}
	public boolean isLiberator() {
		return liberator;
	}
	public void setSwitchMouse(boolean switchMouse) {
		this.switchMouse = switchMouse;
	}
	public boolean isSwitchMouse() {
		return switchMouse;
	}
	public void setShowFOW(boolean showFOW) {
		this.showFOW = showFOW;
	}
	public boolean isShowFOW() {
		return showFOW;
	}
	public boolean isCanvasScroll() {
		return canvasScroll;
	}
	public void setCanvasScroll(boolean canvasScroll) {
		this.canvasScroll = canvasScroll;
	}
}
