package com.kenstevens.stratinit.client.control;

import com.google.common.collect.Sets;
import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.client.model.CityView;
import com.kenstevens.stratinit.client.model.Data;
import com.kenstevens.stratinit.client.model.WorldSector;
import com.kenstevens.stratinit.move.WorldView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import java.util.Set;

public class CityTableItemControl {

	private final Table table;
	private final Data db;

	private final Set<City> citiesInTable = Sets.newHashSet();

	// continue copying this from treeitemlistcontrol

	public CityTableItemControl(Table table, Data db) {
		this.table = table;
		this.db = db;
	}

	public void tablifyCities() {
		WorldView world = db.getWorld();
		if (table.isDisposed())
			return;
		for (CityView city : db.getCityList()) {
			addCityToTable(world, city);
		}
	}
	
	public void clear() {
		table.removeAll();
		citiesInTable.clear();
	}

	private void addCityToTable(WorldView worldView,
			CityView city) {
		TableItem item = new TableItem(table, SWT.NONE);
		item.setText(toStringArray(worldView, city));
		item.setData(city);
		citiesInTable.add(city);
	}

	public String[] toStringArray(WorldView world, 
			CityView city) {
		WorldSector sector = world.getWorldSectorOrNull(city.getCoords());
		String islandId = "";
		if (sector != null) {
			islandId = "" + sector.getIsland();
			if (world.isCoastal(sector)) {
				islandId += "*";
			}
			if (city.getCityMove() != null) {
				islandId += "-";
			}
		}
		ETAHelper etaHelper = new ETAHelper(db);

		return new String[]{"" + city.getX() + "," + city.getY(), islandId,
				city.getBuildingString(), city.getNextString(),
				etaHelper.getETA(city)};
	}

	public String[] deadCityStringArray() {
		return new String[] { "X,X", "", "", "", "" };
	}


	public boolean contains(CityView city) {
		return citiesInTable.contains(city);
	}
}
