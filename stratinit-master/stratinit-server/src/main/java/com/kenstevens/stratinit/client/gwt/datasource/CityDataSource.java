package com.kenstevens.stratinit.client.gwt.datasource;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;

public class CityDataSource extends DataSource {

	public CityDataSource() {
		setClientOnly(true);
		DataSourceTextField idField = new DataSourceTextField("id", "Id");
		idField.setPrimaryKey(true);
		setFields(
				new DataSourceTextField("coords", "x,y"),
				new DataSourceTextField("build", "build"),
				new DataSourceTextField("next", "next"));
	}
}
