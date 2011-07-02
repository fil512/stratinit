package com.kenstevens.stratinit.client.gwt.widget;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.kenstevens.stratinit.client.gwt.model.GWTEntity;
import com.kenstevens.stratinit.client.gwt.model.StratInitListGridRecord;
import com.smartgwt.client.widgets.grid.ListGrid;

public class GridSynchronizer<I, T extends GWTEntity<I>, G extends StratInitListGridRecord<I, T>> {

	private final ListGrid listGrid;
	private final Map<I, G>idToRecord = new HashMap<I, G>();

	public GridSynchronizer(ListGrid listGrid) {
		this.listGrid = listGrid;
	}

	public void sync(List<T> result) {
		Set<I> foundId = new HashSet<I>();
		for (T entity : result) {
			I id = entity.getId();
			foundId.add(id);
			G record = idToRecord.get(id);
			if (record == null) {
				addRecord(entity);
			} else {
				updateRecord(record, entity);
			}
		}
		Set<I> idsToRemove = new HashSet<I>();
		for (I id : idToRecord.keySet()) {
			if (!foundId.contains(id)) {
				idsToRemove.add(id);
			}
		}
		for (I id : idsToRemove) {
			removeRecord(id);
		}
		listGrid.fetchData();
	}

	@SuppressWarnings("unchecked")
	private void addRecord(T entity) {
		G listGridRecord = (G)entity.getListGridRecord();
		listGrid.addData(listGridRecord);
		idToRecord.put(entity.getId(), listGridRecord);
	}

	private void updateRecord(G record, T entity) {
		record.setValues(entity);
	}

	private void removeRecord(I id) {
		G listGridRecord = idToRecord.get(id);
		listGrid.removeData(listGridRecord);
		idToRecord.remove(id);
	}
}
