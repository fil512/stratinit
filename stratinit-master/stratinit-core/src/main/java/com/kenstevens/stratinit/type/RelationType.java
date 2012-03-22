package com.kenstevens.stratinit.type;


public enum RelationType {
	            // old   new
	WAR,		// 0	0
	NEUTRAL,	// 1	1
//	NAP,		// 2	1
	FRIENDLY,	// 3	2
	ALLIED,		// 4	3
	ME;			// 5	4

	public boolean isTeam() {
		return this == ALLIED || this == ME;
	}
}
