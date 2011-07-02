package com.kenstevens.stratinit.client.gwt.model;

import java.io.Serializable;
import java.util.List;

public class GWTUpdate implements Serializable {
	private static final long serialVersionUID = 1L;
	public int nationId;
	public List<GWTNation> nations;
	public List<GWTUnit> units;
	public List<GWTSector> sectors;
	public List<GWTCity> cities;
}
