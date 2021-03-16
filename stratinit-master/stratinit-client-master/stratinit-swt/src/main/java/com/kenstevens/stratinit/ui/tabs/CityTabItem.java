package com.kenstevens.stratinit.ui.tabs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;

import java.text.Collator;
import java.util.Locale;

public class CityTabItem extends Composite {
	private final class SortListener implements Listener {
		private final TableColumn xyColumn;
		private final TableColumn nextColumn;
		private final TableColumn timeRemainingColumn;
		private final TableColumn buildingColumn;
		private final TableColumn isleColumn;

		private SortListener(TableColumn xyColumn, TableColumn nextColumn,
				TableColumn timeRemainingColumn, TableColumn buildingColumn,
				TableColumn isleColumn) {
			this.xyColumn = xyColumn;
			this.nextColumn = nextColumn;
			this.timeRemainingColumn = timeRemainingColumn;
			this.buildingColumn = buildingColumn;
			this.isleColumn = isleColumn;
		}

		public void handleEvent(Event e) {
		    TableItem[] items = cityTable.getItems();
		    Collator collator = Collator.getInstance(Locale.getDefault());
		    TableColumn column = (TableColumn)e.widget;
		    int index = 0;
		    if (column.equals(xyColumn)) {
		    	index = 0;
		    } else if (column.equals(isleColumn)) {
		    	index = 1;
		    } else if (column.equals(buildingColumn)) {
		    	index = 2;
		    } else if (column.equals(nextColumn)) {
		    	index = 3;
		    } else if (column.equals(timeRemainingColumn)) {
		    	index = 4;
		    }
		    for (int i = 1; i < items.length; i++) {
		        String value1 = items[i].getText(index);
		        for (int j = 0; j < i; j++){
		            String value2 = items[j].getText(index);
		            if (collator.compare(value1, value2) < 0) {
		                String[] values = {
		                		items[i].getText(0),
		                		items[i].getText(1),
		                		items[i].getText(2),
		                		items[i].getText(3),
		                		items[i].getText(4),
		                		};
		                Object data = items[i].getData();
		                items[i].dispose();
		                TableItem item = new TableItem(cityTable, SWT.NONE, j);
		                item.setText(values);
		                item.setData(data);
		                items = cityTable.getItems();
		                break;
		            }
		        }
		    }
		    cityTable.setSortColumn(column);
		}
	}
	private Button updateCitiesButton;
	public static final String TITLE = "Cities";
	private Table cityTable;
	private Button btnCede;
	private BuildingCombos buildingCombos;
	private CityButtons cityButtons;

	public CityTabItem(Composite parent, int style) {
		super(parent, style);
		createContents();
	}


	private void createContents() {

		setLayout(new FormLayout());

		Group buildingGroup = new Group(this, SWT.NONE);
		final FormData fdBuildingGroup = new FormData();
		fdBuildingGroup.top = new FormAttachment(0);
		fdBuildingGroup.right = new FormAttachment(102);
		fdBuildingGroup.left = new FormAttachment(0, 5);
		buildingGroup.setLayoutData(fdBuildingGroup);
		buildingGroup.setLayout(new FormLayout());

		buildingCombos = new BuildingCombos(buildingGroup, SWT.NONE);
		buildingCombos.getNextBuildCombo().setVisibleItemCount(19);
		buildingCombos.getBuildingCombo().setVisibleItemCount(20);
		final FormData fdBuildingCombos = new FormData();
		fdBuildingCombos.right = new FormAttachment(100, -5);
		fdBuildingCombos.left = new FormAttachment(0, 3);
		buildingCombos.setLayoutData(fdBuildingCombos);
		new Label(buildingCombos, SWT.NONE);

		Label unitsLabel = new Label(this, SWT.NONE);
		fdBuildingGroup.bottom = new FormAttachment(27);
		final FormData fdUnitsLabel = new FormData();
		fdUnitsLabel.top = new FormAttachment(buildingGroup, 6);
		fdUnitsLabel.left = new FormAttachment(buildingGroup, 0, SWT.LEFT);
		unitsLabel.setLayoutData(fdUnitsLabel);
		unitsLabel.setText("Cities:");
		
		cityTable = new Table(this, SWT.FULL_SELECTION | SWT.BORDER);
		final FormData fdCityTable = new FormData();
		fdCityTable.top = new FormAttachment(unitsLabel, 6);
		fdCityTable.right = new FormAttachment(0, 411);
		fdCityTable.left = new FormAttachment(0, 0);
		cityTable.setLayoutData(fdCityTable);
		cityTable.setLinesVisible(true);
		cityTable.setHeaderVisible(true);

		final TableColumn xyColumn = new TableColumn(cityTable, SWT.NONE);
		xyColumn.setAlignment(SWT.RIGHT);
		xyColumn.setWidth(50);
		xyColumn.setText("x,y");

		final TableColumn isleColumn = new TableColumn(cityTable,
				SWT.NONE);
		isleColumn.setToolTipText("Island number.  * means port.  - means waypoint.");
		isleColumn.setWidth(50);
		isleColumn.setText("Isle");

		final TableColumn buildingColumn = new TableColumn(cityTable, SWT.NONE);
		buildingColumn.setWidth(111);
		buildingColumn.setText("Building");

		final TableColumn nextColumn = new TableColumn(cityTable, SWT.NONE);
		nextColumn.setWidth(108);
		nextColumn.setText("Next");

		final TableColumn timeRemainingColumn = new TableColumn(cityTable,
				SWT.NONE);
		timeRemainingColumn.setToolTipText("Time Unit Will be Built");
		timeRemainingColumn.setAlignment(SWT.RIGHT);
		timeRemainingColumn.setWidth(66);
		timeRemainingColumn.setText("ETA");


		

		Group cityButtonsGroup = new Group(this, SWT.NONE);
		fdCityTable.bottom = new FormAttachment(cityButtonsGroup, -6);
		final FormData fdCityButtonsGroup = new FormData();
		fdCityButtonsGroup.bottom = new FormAttachment(100, -10);
		fdCityButtonsGroup.right = new FormAttachment(102, -19);
		fdCityButtonsGroup.left = new FormAttachment(0, 5);
		cityButtonsGroup.setLayoutData(fdCityButtonsGroup);
		cityButtonsGroup.setLayout(new FormLayout());

		cityButtons = new CityButtons(cityButtonsGroup, SWT.NONE);

		final FormData fdCityButtons = new FormData();
		fdCityButtons.right = new FormAttachment(100, -5);
		fdCityButtons.left = new FormAttachment(0, 3);
		cityButtons.setLayoutData(fdCityButtons);
		
		
		Listener sortListener = new SortListener(xyColumn, nextColumn, timeRemainingColumn,
				buildingColumn, isleColumn);
	    xyColumn.addListener(SWT.Selection, sortListener);
	    isleColumn.addListener(SWT.Selection, sortListener);
	    buildingColumn.addListener(SWT.Selection, sortListener);
	    nextColumn.addListener(SWT.Selection, sortListener);
	    timeRemainingColumn.addListener(SWT.Selection, sortListener);
	    cityTable.setSortColumn(isleColumn);
	    cityTable.setSortDirection(SWT.UP);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public Table getCityTable() {
		return cityTable;
	}
	public Button getUpdateCitiesButton() {
		return updateCitiesButton;
	}
	public Button getCedeButton() {
		return btnCede;
	}


	public BuildingCombos getBuildingCombos() {
		return buildingCombos;
	}
	
	public CityButtons getCityButtons() {
		return cityButtons;
	}
}
