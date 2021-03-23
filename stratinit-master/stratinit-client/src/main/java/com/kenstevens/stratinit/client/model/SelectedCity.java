package com.kenstevens.stratinit.client.model;

import org.springframework.stereotype.Repository;

@Repository
public class SelectedCity {
	private CityView city;

	public void setCity(CityView city) {
		this.city = city;
	}

	public CityView getCity() {
		return city;
	}

	public boolean citySelected() {
		return city != null;
	}
}
