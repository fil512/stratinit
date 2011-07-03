package com.kenstevens.stratinit.ui.tabs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.kenstevens.stratinit.type.UnitType;

public class BuildingCombos extends Composite {
	private Combo nextBuildCombo;
	private Combo buildingCombo;
	private Label buildETA;
	private Label percentDoneLabel;
	private Button btnSwtichOnTechChange;
	private Label lblWaypoint;
	private Label waypoint;

	/**
	 * Create the composite
	 * @param parent
	 * @param style
	 */
	public BuildingCombos(Composite parent, int style) {
		super(parent, style);
		createContents();
	}

	private void createContents() {
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		setLayout(gridLayout);

		Label nextBuildLabel;
		final Label buildingLabel = new Label(this, SWT.NONE);
		buildingLabel.setText("Building:");
		buildingLabel.setToolTipText("(lowercase) means you have reached max number for that unit type");

		buildingCombo = new Combo(this, SWT.BORDER | SWT.DROP_DOWN
				| SWT.SINGLE);
		buildingCombo.setLayoutData(new GridData(125, SWT.DEFAULT));
		buildingCombo.setVisibleItemCount(10);

		percentDoneLabel = new Label(this, SWT.NONE);
		percentDoneLabel.setToolTipText("Percent Building Complete");
		percentDoneLabel.setLayoutData(new GridData(60, SWT.DEFAULT));

		buildETA = new Label(this, SWT.NONE);
		buildETA.setToolTipText("Time Unit Will be Built");
		buildETA.setLayoutData(new GridData(80, SWT.DEFAULT));
		nextBuildLabel = new Label(this, SWT.NONE);
		nextBuildLabel.setText("Next build:");

		nextBuildCombo = new Combo(this, SWT.BORDER | SWT.DROP_DOWN
				| SWT.SINGLE);
		nextBuildCombo.setLayoutData(new GridData(125, SWT.DEFAULT));
		nextBuildCombo.setVisibleItemCount(10);
		
		lblWaypoint = new Label(this, SWT.NONE);
		lblWaypoint.setText("Waypoint:");
		lblWaypoint.setToolTipText("Sector that units in this city will automatically move to");
		
		waypoint = new Label(this, SWT.NONE);
		waypoint.setToolTipText("Sector that units in this city will automatically move to");
		waypoint.setLayoutData(new GridData(80, SWT.DEFAULT));
		new Label(this, SWT.NONE);

		btnSwtichOnTechChange = new Button(this, SWT.CHECK);
		btnSwtichOnTechChange.setToolTipText("Switch to the Next Build as soon as your tech is high enough");
		btnSwtichOnTechChange.setText("Switch on tech gain");
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public Combo getNextBuildCombo() {
		return nextBuildCombo;
	}

	public Combo getBuildingCombo() {
		return buildingCombo;
	}
	public Label getBuildETA() {
		return buildETA;
	}

	public Label getPercentDoneLabel() {
		return percentDoneLabel;
	}

	@Override
	public void setVisible(boolean visible) {
		buildingCombo.setVisible(visible);
		if (buildingCombo.getText().equals(UnitType.BASE.toString())) {
			nextBuildCombo.setVisible(false);
		} else {
			nextBuildCombo.setVisible(visible);
		}
		super.setVisible(visible);
	}


	public Button getBtnSwitchOnTechChange() {
		return btnSwtichOnTechChange;
	}
	public Label getWaypoint() {
		return waypoint;
	}
}
