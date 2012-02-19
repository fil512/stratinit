package com.kenstevens.stratinit.client.gwt.model;


public interface GWTEntity<I> {

	public abstract I getId();

	public abstract StratInitListGridRecord<I, ? extends GWTEntity<I>> getListGridRecord();

}