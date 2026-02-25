package com.kenstevens.stratinit.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;
import java.util.List;


@Schema(description = "Complete world view update containing sectors, units, cities, nations, battle logs, and relations")
public class SIUpdate implements StratInitDTO {
    private static final long serialVersionUID = 1L;
    public int nationId;
    public List<SISector> sectors;
    public List<SIUnit> units;
    public List<SIUnit> seenUnits;
    public List<SINation> nations;
    public List<SICityUpdate> cities;
    public List<SIBattleLog> log;
    public List<SIRelation> relations;
    public List<SILaunchedSatellite> launchedSatellites;
    public List<String> messages;
    public Date lastUpdated;
    public long tickIntervalMs;
    public int gameId;
    public String gameName;
    public Date gameEnds;
    public boolean blitz;

	public SIUpdate() {}
}
