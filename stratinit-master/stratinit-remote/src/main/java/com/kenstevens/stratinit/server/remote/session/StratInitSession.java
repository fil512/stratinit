package com.kenstevens.stratinit.server.remote.session;

import com.kenstevens.stratinit.model.Nation;

public class StratInitSession {
	private Nation nation;
	
	public void setNation(Nation nation) {
		this.nation = nation;
	}

	public Nation getNation() {
		return nation;
	}
}
