package com.kenstevens.stratinit.client.gwt.model;

import java.io.Serializable;


public class GWTBuildAudit implements Serializable, GWTEntity<Integer> {

	private static final long serialVersionUID = 1L;
	private Integer id;
    private String type;
	private int count;

    public GWTBuildAudit() {}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}


	public void setCount(int count) {
		this.count = count;
	}

	public int getCount() {
		return count;
	}

	public StratInitListGridRecord<Integer, GWTBuildAudit> getListGridRecord() {
		return new BuildAuditListGridRecord(this);
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
