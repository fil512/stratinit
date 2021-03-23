package com.kenstevens.stratinit.client.ui.tabs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class UnitButtons extends Composite {
	private Button cedeButton;
	private Button disbandButton;
	private Button centreUnitButton;
	private Button centreHomeButton;
	private Button cancelMoveButton;
	private Button buildCityButton;
	private Button switchTerrainButton;
	private Button updateButton;

	/**
	 * Create the composite
	 * @param parent
	 * @param style
	 */
	public UnitButtons(Composite parent, int style) {
		super(parent, style);
		createContents();
	}

	private final void createContents() {
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 8;
		setLayout(gridLayout);

		updateButton = new Button(this, SWT.NONE);
		GridData gridDataUpdateButton = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gridDataUpdateButton.widthHint = 36;
		gridDataUpdateButton.heightHint = 28;
		updateButton.setLayoutData(gridDataUpdateButton);
		updateButton.setToolTipText("Refresh unit data");
		
		centreUnitButton = new Button(this, SWT.NONE);
		GridData gridDataCentreUnit = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gridDataCentreUnit.widthHint = 36;
		gridDataCentreUnit.heightHint = 28;
		centreUnitButton.setLayoutData(gridDataCentreUnit);
		centreUnitButton.setToolTipText("Centre map on selected unit");

		centreHomeButton = new Button(this, SWT.NONE);
		GridData gridDataCentreHome = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gridDataCentreHome.widthHint = 36;
		gridDataCentreHome.heightHint = 28;
		centreHomeButton.setLayoutData(gridDataCentreHome);
		centreHomeButton.setToolTipText("Centre map on home island");

		cancelMoveButton = new Button(this, SWT.NONE);
		GridData gridDataCancelMove = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gridDataCancelMove.widthHint = 36;
		gridDataCancelMove.heightHint = 28;
		cancelMoveButton.setLayoutData(gridDataCancelMove);
		cancelMoveButton.setToolTipText("Cancel move order");
		
		cedeButton = new Button(this, SWT.NONE);
		GridData gridDataCede = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gridDataCede.widthHint = 36;
		gridDataCede.heightHint = 28;
		cedeButton.setLayoutData(gridDataCede);
		cedeButton.setToolTipText("Give unit to ally");
		
		disbandButton = new Button(this, SWT.NONE);
		GridData gridDataDisband = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gridDataDisband.widthHint = 36;
		gridDataDisband.heightHint = 28;
		disbandButton.setLayoutData(gridDataDisband);
		disbandButton.setToolTipText("Disband unit");

		buildCityButton = new Button(this, SWT.NONE);
		GridData gridDataBuildCity = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gridDataBuildCity.widthHint = 36;
		gridDataBuildCity.heightHint = 28;
		buildCityButton.setLayoutData(gridDataBuildCity);
		buildCityButton.setToolTipText("Build new city");

		switchTerrainButton = new Button(this, SWT.NONE);
		GridData gridDataSwitchTerrain = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gridDataSwitchTerrain.widthHint = 36;
		gridDataSwitchTerrain.heightHint = 28;
		switchTerrainButton.setLayoutData(gridDataSwitchTerrain);
		switchTerrainButton.setToolTipText("Switch terrain");
		
		setIsEngineer(false);
	}

	public void setIsEngineer(boolean isEngineer) {
		buildCityButton.setEnabled(isEngineer);
		switchTerrainButton.setEnabled(isEngineer);
	}

	public Button getCedeButton() {
		return cedeButton;
	}

	public Button getDisbandButton() {
		return disbandButton;
	}

	public Button getBuildCityButton() {
		return buildCityButton;
	}

	public Button getSwitchTerrainButton() {
		return switchTerrainButton;
	}

	public Button getCentreUnitButton() {
		return centreUnitButton;
	}
	public Button getCentreHomeButton() {
		return centreHomeButton;
	}
	public Button getCancelMoveButton() {
		return cancelMoveButton;
	}
	public Button getUpdateButton() {
		return updateButton;
	}
}
