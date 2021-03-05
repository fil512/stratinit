package com.kenstevens.stratinit.client.server.rest.svc;

import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.type.SectorCoords;

public class NukeTargetScore {
    private final Unit nuke;
    private SectorCoords coords;
    private int score;

    public NukeTargetScore(SectorCoords targetCoords, int highScore, Unit nuke) {
        this.coords = targetCoords;
        this.score = highScore;
        this.nuke = nuke;
    }

    public SectorCoords getCoords() {
        return coords;
    }

    public void setCoords(SectorCoords coords) {
        this.coords = coords;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Unit getNuke() {
        return nuke;
    }
}
