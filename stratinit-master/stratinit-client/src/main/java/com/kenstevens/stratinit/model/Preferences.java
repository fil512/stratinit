package com.kenstevens.stratinit.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.springframework.stereotype.Repository;

@Root
@Repository
public class Preferences {
	@Element
	private boolean showBuilding = false;
	@Element
	private boolean playSounds = true;
	@Element(required=false)
	private boolean liberator = true;
	@Element(required=false)
	private boolean switchMouse = false;
	@Element(required=false)
	private boolean showFOW = true;
	
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
	

}
