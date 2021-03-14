package com.kenstevens.stratinit.client.model;

import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.SectorType;

public class UnknownSector extends WorldSector {
    public UnknownSector(Game game, int x, int y) {
        super(game, new SectorCoords(x, y), SectorType.UNKNOWN, -1);
    }
}
