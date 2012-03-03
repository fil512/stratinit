package com.kenstevens.stratinit.dto;

import java.io.Serializable;
import java.util.List;


public class SIUpdate implements Serializable {
	private static final long serialVersionUID = 1L;
	public int nationId;
	public List<SISector> sectors;
	public List<SIUnit> units;
	public List<SIUnit> seenUnits;
	public List<SINation> nations;
	public List<SICity> cities;
	public List<SIBattleLog> log;
	public List<SIRelation> relations;
	public List<SILaunchedSatellite> launchedSatellites;
	
	public SIUpdate() {}
}
