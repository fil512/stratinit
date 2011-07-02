package com.kenstevens.stratinit.client.gwt.widget.play.tab;

import com.kenstevens.stratinit.client.gwt.model.GWTUnitType;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;

public class BuildPanel extends Canvas {
	private final ComboBoxItem buildItem;
	private final ComboBoxItem nextBuildItem;

	public BuildPanel() {
		setWidth100();
        DynamicForm buildComboForm = new DynamicForm();
        buildComboForm.setWidth100();
        buildComboForm.setIsGroup(true);
        buildComboForm.setGroupTitle("City Build Controls");
		buildItem = new ComboBoxItem();
		getBuildItem().setTitle("Build");
        // ETA
//        cbItem.setHint("<nobr>A simple combobox</nobr>");
		getBuildItem().setType("comboBox");
		getBuildItem().setValueMap(asStrings(GWTUnitType.values()));

		nextBuildItem = new ComboBoxItem();
		getNextBuildItem().setTitle("Next Build");
        // ETA
//        cbItem.setHint("<nobr>A simple combobox</nobr>");
		getNextBuildItem().setType("comboBox");
		getNextBuildItem().setValueMap(asStrings(GWTUnitType.values()));
        buildComboForm.setItems(getBuildItem(), getNextBuildItem());
        this.addChild(buildComboForm);
	}

	private String[] asStrings(GWTUnitType[] values) {
		String[] retval = new String[values.length];
		int i = 0;
		for (GWTUnitType type : values) {
			retval[i++] = type.toString();
		}
		return retval;
	}

	public void setBuild(GWTUnitType build) {
		getBuildItem().setValue(build.toString());
	}

	public void setNextBuild(GWTUnitType nextBuild) {
		if (nextBuild == null) {
			return;
		}
		getBuildItem().setValue(nextBuild.toString());
	}

	final public ComboBoxItem getBuildItem() {
		return buildItem;
	}

	final public ComboBoxItem getNextBuildItem() {
		return nextBuildItem;
	}
}
