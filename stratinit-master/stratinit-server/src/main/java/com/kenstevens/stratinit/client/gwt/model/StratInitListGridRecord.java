package com.kenstevens.stratinit.client.gwt.model;

import com.smartgwt.client.widgets.grid.ListGridRecord;


public abstract class StratInitListGridRecord<I, T extends GWTEntity<I>> extends ListGridRecord {
	public abstract void setValues(T entity);
}
