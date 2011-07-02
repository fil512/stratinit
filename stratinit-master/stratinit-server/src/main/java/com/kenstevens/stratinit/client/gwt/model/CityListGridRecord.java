package com.kenstevens.stratinit.client.gwt.model;


public class CityListGridRecord extends StratInitListGridRecord<String, GWTCity> {
	private GWTCity city;

	public CityListGridRecord(GWTCity city) {
		setValues(city);
	}

	public final void setValues(GWTCity city) {
		this.city = city;
		setId(city.getId());
		setCoords(city.coords);
		setCityType(city.cityType);
		setBuild(city.build);
		if (city.nextBuild != null) {
			setNextBuild(city.nextBuild);
		}
	}

	public final void setId(String id) {
        setAttribute("id", id);
    }

	public final void setCoords(GWTSectorCoords coords) {
        setAttribute("coords", coords.x+","+coords.y);
        setAttribute("x", coords.x);
        setAttribute("y", coords.y);
    }

	public final void setBuild(GWTUnitType build) {
        setAttribute("build", build.toString());
    }

	public final void setNextBuild(GWTUnitType nextBuild) {
        setAttribute("nextBuild", nextBuild.toString());
    }

	public final void setCityType(GWTCityType type) {
        setAttribute("type", type.toString());
    }

	public GWTCity getCity() {
		return city;
	}
}
