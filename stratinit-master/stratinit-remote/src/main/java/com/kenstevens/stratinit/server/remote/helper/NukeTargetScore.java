package com.kenstevens.stratinit.server.remote.helper;

import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.type.SectorCoords;

public class NukeTargetScore {
	private static final long serialVersionUID = 1L;
	private SectorCoords coords;
	private int score;
	private final Unit nuke;
	public NukeTargetScore(SectorCoords targetCoords, int highScore, Unit nuke) {
		this.coords = targetCoords;
		this.score = highScore;
		this.nuke = nuke;
	}
	public void setCoords(SectorCoords coords) {
		this.coords = coords;
	}
	public SectorCoords getCoords() {
		return coords;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public int getScore() {
		return score;
	}
	public Unit getNuke() {
		return nuke;
	}
}
