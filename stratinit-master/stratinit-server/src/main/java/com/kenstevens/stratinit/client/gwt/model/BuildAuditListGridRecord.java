package com.kenstevens.stratinit.client.gwt.model;


public class BuildAuditListGridRecord extends StratInitListGridRecord<Integer, GWTBuildAudit> {

	public BuildAuditListGridRecord(GWTBuildAudit buildAudit) {
		setValues(buildAudit);
	}
	public final void setGameId(int gameId) {
        setAttribute("game", gameId);
    }
	public final void setUnitType(String type) {
        setAttribute("type", type);
    }
	public final void setCount(int count) {
        setAttribute("count", count);
    }
	@Override
	public final void setValues(GWTBuildAudit buildAudit) {
		setGameId(buildAudit.getId());
		setUnitType(buildAudit.getType().toString());
		setCount(buildAudit.getCount());
	}
}
