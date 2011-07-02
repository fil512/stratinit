package com.kenstevens.stratinit.client.gwt.datasource;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;

public class UnitDataSource extends DataSource {

	public UnitDataSource() {
		setClientOnly(true);
		DataSourceIntegerField idField = new DataSourceIntegerField("id", "Id");
		idField.setPrimaryKey(true);
		setFields(
				new DataSourceTextField("type", "type"),
				new DataSourceIntegerField("coords", "x,y"),
				new DataSourceIntegerField("mobility", "mob"),
				new DataSourceIntegerField("hp", "hp"),
				new DataSourceIntegerField("ammo", "ammo"),
				new DataSourceIntegerField("fuel", "fuel"));
	}
}
