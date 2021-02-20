package com.kenstevens.stratinit.ui.tabs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class SectorTabItem extends Composite {
	private BuildingCombos buildingCombos;
	private Label sectorCoordsLabel;
	private Label sectorType;
	private UnitTable unitTable;
	private UnitButtons unitButtons;


	public SectorTabItem(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());
		createContents();
	}

	private void createContents() {

		final Group sectorGroup = new Group(this, SWT.NONE);
		final FormData fdSectorGroup = new FormData();
		fdSectorGroup.left = new FormAttachment(0, 5);
		fdSectorGroup.top = new FormAttachment(0, 5);
		sectorGroup.setLayoutData(fdSectorGroup);
		GridLayout sectorGroupGridLayout = new GridLayout();
		sectorGroupGridLayout.numColumns = 3;
		sectorGroup.setLayout(sectorGroupGridLayout);

		Label sectorLabel = new Label(sectorGroup, SWT.NONE);
		sectorLabel.setText("Sector:");

		sectorCoordsLabel = new Label(sectorGroup, SWT.NONE);
		sectorCoordsLabel.setLayoutData(new GridData(40, SWT.DEFAULT));

		sectorType = new Label(sectorGroup, SWT.NONE);
		sectorType.setLayoutData(new GridData(250, SWT.DEFAULT));

		Group buildingGroup = new Group(this, SWT.NONE);
		final FormData fdBuildingGroup = new FormData();
		fdBuildingGroup.top = new FormAttachment(sectorGroup);
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
		new Label(buildingCombos, SWT.NONE);
		new Label(buildingCombos, SWT.NONE);
		new Label(buildingCombos, SWT.NONE);

		Label unitsLabel = new Label(this, SWT.NONE);
		fdBuildingGroup.bottom = new FormAttachment(27);
		final FormData fdUnitsLabel = new FormData();
		fdUnitsLabel.top = new FormAttachment(0, 183);
		fdUnitsLabel.left = new FormAttachment(0, 5);
		fdUnitsLabel.left = new FormAttachment(sectorGroup, 0, SWT.LEFT);
		unitsLabel.setLayoutData(fdUnitsLabel);
		unitsLabel.setText("Units:");

		
		unitTable = new UnitTable(this, SWT.NONE, false);
		final FormData fdUnitTable = new FormData();
		fdUnitTable.top = new FormAttachment(unitsLabel, 10);
		fdUnitTable.left = new FormAttachment(0, 5);
		fdUnitTable.right = new FormAttachment(sectorGroup, 18, SWT.RIGHT);

		Group unitButtonsGroup = new Group(this, SWT.NONE);
		fdUnitTable.bottom = new FormAttachment(unitButtonsGroup, -6);
		final FormData fdUnitButtonsGroup = new FormData();
		fdUnitButtonsGroup.bottom = new FormAttachment(100, -10);
		fdUnitButtonsGroup.right = new FormAttachment(102, -19);
		fdUnitButtonsGroup.left = new FormAttachment(0, 5);
		unitButtonsGroup.setLayoutData(fdUnitButtonsGroup);
		unitButtonsGroup.setLayout(new FormLayout());

		unitButtons = new UnitButtons(unitButtonsGroup, SWT.NONE);

		final FormData fdUnitButtons = new FormData();
		fdUnitButtons.right = new FormAttachment(100, -5);
		fdUnitButtons.left = new FormAttachment(0, 3);
		unitButtons.setLayoutData(fdUnitButtons);
		new Label(unitButtons, SWT.NONE);
		new Label(unitButtons, SWT.NONE);
		new Label(unitButtons, SWT.NONE);
		new Label(unitButtons, SWT.NONE);
		new Label(unitButtons, SWT.NONE);
		new Label(unitButtons, SWT.NONE);
		new Label(unitButtons, SWT.NONE);
		new Label(unitButtons, SWT.NONE);
		


		unitTable.setLayoutData(fdUnitTable);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public UnitTable getUnitTable() {
		return unitTable;
	}

	public Label getSectorCoordsLabel() {
		return sectorCoordsLabel;
	}

	public Label getSectorType() {
		return sectorType;
	}

	BuildingCombos getBuildingCombos() {
		return buildingCombos;
	}

	public UnitButtons getUnitButtons() {
		return unitButtons;
	}
}
