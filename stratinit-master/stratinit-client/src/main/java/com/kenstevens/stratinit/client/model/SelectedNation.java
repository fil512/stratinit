package com.kenstevens.stratinit.client.model;

import org.springframework.stereotype.Repository;

@Repository
public class SelectedNation {
	private NationView nation;

	public void setNation(NationView nation) {
		this.nation = nation;
	}
	
	public NationView getPlayer() {
		return nation;
	}
}
