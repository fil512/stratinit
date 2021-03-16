package com.kenstevens.stratinit.ui.window;

import com.kenstevens.stratinit.client.model.Data;
import com.kenstevens.stratinit.client.model.NationView;
import com.kenstevens.stratinit.client.model.UnitBase;
import com.kenstevens.stratinit.client.util.BuildHelper;
import com.kenstevens.stratinit.shell.ColourMap;
import com.kenstevens.stratinit.shell.StratInitWindow;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.type.UnitType;
import com.kenstevens.stratinit.ui.image.ImageLibrary;
import com.kenstevens.stratinit.ui.tabs.TableControl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

@Component
public class GameInfoWindow extends TableControl implements StratInitWindow {
	// TODO REF move this to clientConstants
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat(
			"MM/dd HH:mm");

	private Label gameDay;
	private Shell dialog;
	private Group group;
	private Label gameEnds;
	private Label serverVersion;
	private Label tech;
	private Label radar;
	private Label ally;
	private Label allyTech;
	private Label myTechGain;
	private Label dailyTechGain;
	private Label dailyTechBleed;
	private Table table;
	private Label gameStarts;
	private Label powerLimit;
	@Autowired
	private Data db;
	@Autowired
	private ImageLibrary imageLibrary;

	private Label commandPoints;
	private Label cpPerHour;

	/**
	 * Open the window
	 * 
	 * @wbp.parser.entryPoint
	 */
	public void open(Shell shell) {
		dialog = new Shell(shell);
		createContents(dialog);
		dialog.open();
	}

	/**
	 * Create contents of the window
	 */
	protected void createContents(Shell dialog) {
		dialog.setSize(999, 653);
		dialog.setText("Technology");

		group = new Group(dialog, SWT.NONE);
		group.setLocation(0, 0);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		group.setLayout(gridLayout);
		group.setSize(966, 164);

		Label gametimeRemainingLabel;
		// 0,0
		final Label dayLabel = new Label(group, SWT.NONE);
		dayLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		dayLabel.setText("Day:");

		gameDay = new Label(group, SWT.NONE);
		gameDay.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		// 0,1
		Label techLabel;
		techLabel = new Label(group, SWT.NONE);
		techLabel.setText("Tech Level:");
		techLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));

		tech = new Label(group, SWT.NONE);
		tech.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		// 1,0
		Label lblGameStarts = new Label(group, SWT.NONE);
		lblGameStarts.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblGameStarts.setText("Game Starts:");

		gameStarts = new Label(group, SWT.NONE);
		// 1,1
		Label lblDailyTechGain = new Label(group, SWT.NONE);
		lblDailyTechGain.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false, 1, 1));
		lblDailyTechGain.setText("Daily Tech Gain:");

		dailyTechGain = new Label(group, SWT.NONE);
		// 2,0
		gametimeRemainingLabel = new Label(group, SWT.NONE);
		gametimeRemainingLabel.setLayoutData(new GridData(SWT.RIGHT,
				SWT.CENTER, false, false, 1, 1));
		gametimeRemainingLabel.setText("Game Ends:");

		gameEnds = new Label(group, SWT.NONE);
		gameEnds.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		// 2,1
		Label lblMyTechGain = new Label(group, SWT.NONE);
		lblMyTechGain.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblMyTechGain.setText("Tech from Tech Centres:");

		myTechGain = new Label(group, SWT.NONE);
		myTechGain.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false));
		// 3,0
		Label serverLabel;
		serverLabel = new Label(group, SWT.NONE);
		serverLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		serverLabel.setText("Server Version:");

		serverVersion = new Label(group, SWT.NONE);
		serverVersion.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		// 3,1
		Label lblDailyTechBleed = new Label(group, SWT.NONE);
		lblDailyTechBleed.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false, 1, 1));
		lblDailyTechBleed.setText("Tech from Bleed:");

		dailyTechBleed = new Label(group, SWT.NONE);
		// 4,0
		Label lblCommandPonts = new Label(group, SWT.NONE);
		lblCommandPonts.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false, 1, 1));
		lblCommandPonts.setText("Command Ponts:");

		commandPoints = new Label(group, SWT.NONE);
		// 4,1
		Label lblUnitTypeLimit = new Label(group, SWT.NONE);
		lblUnitTypeLimit.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false, 1, 1));
		lblUnitTypeLimit
				.setToolTipText("Your current power / the max power you can build");
		lblUnitTypeLimit.setText("Power / Limit:");

		powerLimit = new Label(group, SWT.NONE);
		powerLimit.setText("");
		powerLimit
				.setToolTipText("The Maximum number of units you can build of each type");
		// 5,0
		Label lblCpHour = new Label(group, SWT.NONE);
		lblCpHour.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblCpHour.setText("CP / hour:");

		cpPerHour = new Label(group, SWT.NONE);

		// 5,1
		Label lblRadarDistance = new Label(group, SWT.NONE);
		lblRadarDistance.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false, 1, 1));
		lblRadarDistance.setText("Radar Distance:");

		radar = new Label(group, SWT.NONE);

		// 6,0
		Label lblally = new Label(group, SWT.NONE);
		lblally.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		lblally.setText("Ally:");

		ally = new Label(group, SWT.NONE);

		// 6,1
		Label lblAllyTech = new Label(group, SWT.NONE);
		lblAllyTech.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblAllyTech.setText("Ally Tech:");

		allyTech = new Label(group, SWT.NONE);

		table = new Table(dialog, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(0, 170, 966, 421);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		// TableColumn imageColumn = new TableColumn(table, SWT.NONE);
		// imageColumn.setWidth(22);

		TableColumn tblclmnType = new TableColumn(table, SWT.NONE);
		tblclmnType.setWidth(130);
		tblclmnType.setText("type");

		TableColumn tblclmnCount = new TableColumn(table, SWT.NONE);
		tblclmnCount.setWidth(55);
		tblclmnCount.setText("count");
		tblclmnCount
				.setToolTipText("Number of units of this type.  May not exceed Unit Type Limit above.");

		TableColumn tblclmnAttack = new TableColumn(table, SWT.NONE);
		tblclmnAttack.setWidth(77);
		tblclmnAttack.setText("built in");

		TableColumn tblclmnTech = new TableColumn(table, SWT.NONE);
		tblclmnTech.setWidth(46);
		tblclmnTech.setText("tech");

		TableColumn tblclmnCost = new TableColumn(table, SWT.NONE);
		tblclmnCost.setWidth(46);
		tblclmnCost.setText("cost");

		TableColumn tblclmnMob = new TableColumn(table, SWT.NONE);
		tblclmnMob.setWidth(54);
		tblclmnMob.setText("mob");

		TableColumn tblclmnSight = new TableColumn(table, SWT.NONE);
		tblclmnSight.setWidth(55);
		tblclmnSight.setText("sight");

		TableColumn tblclmnAtt = new TableColumn(table, SWT.NONE);
		tblclmnAtt.setWidth(36);
		tblclmnAtt.setText("att");

		TableColumn tblclmnAmmo = new TableColumn(table, SWT.NONE);
		tblclmnAmmo.setWidth(63);
		tblclmnAmmo.setText("ammo");

		TableColumn tblclmnFlak = new TableColumn(table, SWT.NONE);
		tblclmnFlak.setWidth(39);
		tblclmnFlak.setText("flak");

		TableColumn tblclmnHp = new TableColumn(table, SWT.NONE);
		tblclmnHp.setWidth(31);
		tblclmnHp.setText("hp");

		TableColumn tblclmnNotes = new TableColumn(table, SWT.NONE);
		tblclmnNotes.setWidth(381);
		tblclmnNotes.setText("notes");
	}

	public void setContents() {
		long powerMax = 0;
		NationView nation = db.getNation();
		if (db != null && db.getCityList() != null) {
			powerMax = BuildHelper.powerLimit(db.getCityList().size());
			powerLimit.setText(nation.getPower() + " / " + powerMax);
		}

		if (gameStarts != null && db.getGameStarts() != null) {
			gameStarts.setText(FORMAT.format(db.getGameStarts()));
		}
		if (gameEnds != null) {
			gameEnds.setText(FORMAT.format(db.getGameEnds()));
			gameDay.setText("" + db.getGameDay());
		}
		if (db.getServerVersion() != null) {
			serverVersion.setText(db.getServerVersion());
		}
		if (nation != null) {
			commandPoints.setText("" + nation.getCommandPoints());
			String maxString = "";
			if (nation.getHourlyCPGain() / 4 <= Constants.MIN_COMMAND_POINTS_GAINED_PER_TICK) {
				maxString = " (min)";
			} else if (nation.getHourlyCPGain() / 4 >= Constants.MAX_COMMAND_POINTS_GAINED_PER_TICK) {
				maxString = " (max)";
			}
			cpPerHour.setText("" + nation.getHourlyCPGain() + maxString);
			tech.setText(String.format("%2.2f", nation.getTech()));
			radar.setText("" + nation.getRadarRadius());
			int techCentreCount = countTechCentres();
			double techCentreContribution = Constants.TECH_INCREASE_DAILY_BY_NUM_TECH_CENTRES[techCentreCount];

			myTechGain
					.setText(String
							.format("%2.2f (%s%d centres)",
									techCentreContribution,
									techCentreCount == Constants.TECH_INCREASE_DAILY_BY_NUM_TECH_CENTRES.length - 1 ? "max "
											: "", techCentreCount));
			dailyTechBleed.setText(String.format("%2.2f",
					nation.getDailyTechBleed()));
			dailyTechGain.setText(String.format("%2.2f", techCentreContribution
					+ nation.getDailyTechBleed()));
			NationView allyNation = db.getAlly();
			if (allyNation != null) {
				ally.setText(allyNation.toString());
				allyTech.setText(String.format("%2.2f", allyNation.getTech()));
			}
		}
		populateTable(powerMax);
		group.layout();
	}

	private int countTechCentres() {
		int techCentres = db.getCityList().countMyTechCentres();
		if (techCentres >= Constants.TECH_INCREASE_DAILY_BY_NUM_TECH_CENTRES.length) {
			techCentres = Constants.TECH_INCREASE_DAILY_BY_NUM_TECH_CENTRES.length - 1;
		}
		return techCentres;
	}

	// TODO REF move to a controller
	private void populateTable(long unitTypeMax) {
		if (db.getNation() == null) {
			return;
		}
		table.removeAll();
		for (UnitType unitType : UnitBase.orderedUnitTypes()) {
			UnitBase unitBase = UnitBase.getUnitBase(unitType);
			TableItem item = new TableItem(table, SWT.NONE);
			int count = db.getUnitList().getUnitTypeCount(unitType);
			unitBaseToItem(unitBase, item, count);
			item.setData(unitBase);

			Color color;
			double myTech = db.getNation().getTech();
			int myTechInt = (int) myTech;
			if (unitBase.getTech() <= myTech) {
				color = ColourMap.WIDGET_HIGHLIGHT_SHADOW;
			} else if (unitBase.getTech() <= myTech + Constants.TECH_NEXT_BUILD) {
				color = ColourMap.WIDGET_LIGHT_SHADOW;
			} else {
				color = ColourMap.WIDGET_NORMAL_SHADOW;
			}
			if (unitBase.getTech() == myTechInt) {
				item.setFont(getBoldTableFont(table, Display.getDefault()));
			}
			item.setBackground(color);
			if (unitTypeMax != 0 && count >= unitTypeMax) {
				item.setForeground(ColourMap.RED);
			}
		}
		table.redraw();
	}

	private void unitBaseToItem(UnitBase unitBase, TableItem item, int count) {
		item.setText(new String[] { unitBase.getType().toString(), "" + count,
				unitBase.getBuiltIn().toString(), "" + unitBase.getTech(),
				"" + unitBase.getProductionTime(), "" + unitBase.getMobility(),
				"" + unitBase.getSight(), "" + unitBase.getAttack(),
				"" + unitBase.getAmmo(), "" + unitBase.getFlak(),
				"" + unitBase.getHp(), unitBase.getNotes() });
		item.setImage(imageLibrary.getUnitImage(RelationType.NEUTRAL,
				unitBase.getType()));
	}

	public boolean isDisposed() {
		return dialog.isDisposed();
	}
}
