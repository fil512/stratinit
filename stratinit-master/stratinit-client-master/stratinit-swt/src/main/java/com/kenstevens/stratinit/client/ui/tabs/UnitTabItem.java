package com.kenstevens.stratinit.client.ui.tabs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

public class UnitTabItem extends Composite {

	public static final String TITLE = "Units";
	private Tree unitListTree;
	private UnitButtons unitButtons;
	public UnitTabItem(Composite parent, int style) {
		super(parent, style);
		createContents();
		}

	private void createContents() {
		setLayout(new FormLayout());

		unitListTree = new Tree(this, SWT.BORDER | SWT.FULL_SELECTION| SWT.NO_SCROLL | SWT.V_SCROLL);
		unitListTree.setHeaderVisible(true);
		final FormData fdUnitListTree = new FormData();
		fdUnitListTree.top = new FormAttachment(0);
		fdUnitListTree.right = new FormAttachment(100, 0);
		fdUnitListTree.left = new FormAttachment(0, 0);

		unitListTree.setLayoutData(fdUnitListTree);


		final TreeColumn newColumnTreeColumn6 = new TreeColumn(unitListTree,
				SWT.NONE);
		newColumnTreeColumn6.setToolTipText("* indicates in a port.  number indicates number of passengers.");
		newColumnTreeColumn6.setWidth(130);
		newColumnTreeColumn6.setText("type");

		final TreeColumn newColumnTreeColumn = new TreeColumn(unitListTree,
				SWT.NONE);
		newColumnTreeColumn.setAlignment(SWT.RIGHT);
		newColumnTreeColumn.setWidth(50);
		newColumnTreeColumn.setText("x,y");
		newColumnTreeColumn.setToolTipText("/X = max X units of each type");

		final TreeColumn newColumnTreeColumn2 = new TreeColumn(unitListTree,
				SWT.NONE);
		newColumnTreeColumn2.setToolTipText("Moves Remaining (\"-\" indicates move order)");
		newColumnTreeColumn2.setAlignment(SWT.RIGHT);
		newColumnTreeColumn2.setWidth(43);
		newColumnTreeColumn2.setText("mob");

		TreeColumn trclmnAmmo = new TreeColumn(unitListTree, SWT.RIGHT);
		trclmnAmmo.setWidth(54);
		trclmnAmmo.setText("ammo");

		final TreeColumn newColumnTreeColumn3 = new TreeColumn(unitListTree,
				SWT.NONE);
		newColumnTreeColumn3.setToolTipText("Hit Points");
		newColumnTreeColumn3.setAlignment(SWT.RIGHT);
		newColumnTreeColumn3.setWidth(37);
		newColumnTreeColumn3.setText("hp");

		final TreeColumn newColumnTreeColumn4 = new TreeColumn(unitListTree,
				SWT.NONE);
		newColumnTreeColumn4.setAlignment(SWT.RIGHT);
		newColumnTreeColumn4.setWidth(39);
		newColumnTreeColumn4.setText("fuel");

		final TreeColumn newColumnTreeColumn5 = new TreeColumn(unitListTree,
				SWT.NONE);
		newColumnTreeColumn5.setToolTipText("Next Move / Repair Time");
		newColumnTreeColumn5.setWidth(50);
		newColumnTreeColumn5.setText("ETA");

		

		Group unitButtonsGroup = new Group(this, SWT.NONE);
		fdUnitListTree.bottom = new FormAttachment(unitButtonsGroup, -6);
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

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public Tree getUnitListTree() {
		return unitListTree;
	}

	public UnitButtons getUnitButtons() {
		return unitButtons;
	}
}
