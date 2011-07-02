package com.kenstevens.stratinit.client.gwt.model;


public class NationListGridRecord extends StratInitListGridRecord<Integer, GWTNation> {

	private GWTNation nation;

	public NationListGridRecord(GWTNation nation) {
		setValues(nation);
	}

	final public void setValues(GWTNation nation) {
		this.nation = nation;
		setId(nation.getId());
		setName(nation.name);
		setCities(nation.cities);
		setWins(nation.wins);
		setPlayed(nation.played);
	}

	public final void setId(Integer id) {
        setAttribute("id", id);
    }

	public final void setName(String name) {
        setAttribute("name", name);
    }

	public final void setCities(int cities) {
        setAttribute("cities", cities);
    }
	
	public final void setWins(int wins) {
        setAttribute("wins", wins);
    }
	
	public final void setPlayed(int played) {
        setAttribute("played", played);
    }
	
	public GWTNation getNation() {
		return nation;
	}
}
