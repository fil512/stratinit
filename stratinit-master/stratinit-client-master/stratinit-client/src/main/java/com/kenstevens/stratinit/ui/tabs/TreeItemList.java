package com.kenstevens.stratinit.ui.tabs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class TreeItemList {
	public static final String ORBIT = "Orbit";
	public static final String HOLD = "Hold";
	public static final String BOARDING = "Boarding";
	public static final String AT_SEA = "At Sea";
	public static final String STRANDED = "Stranded";
	public static final String REPAIRING = "Repairing";
	public static final String MOVED = "Moved";

	private TreeItem navyItem;
	private TreeItem airItem;
	private TreeItem landItem;
	private TreeItem techItem;
	private TreeItem orbitItem;
	private final Tree tree;
	private Font boldTreeFont = null;
	
	public TreeItemList(Tree tree) {
		this.tree = tree;
		addHeaders();
	}
	

	public Font getBoldTreeFont() {
		if (boldTreeFont == null) {
			FontData fd = tree.getFont().getFontData()[0];
			fd.setStyle(SWT.BOLD);
			boldTreeFont = new Font(Display.getDefault(), fd);
		}
		return boldTreeFont;
	}
	
	public void addHeaders() {
		// TODO REF refactor to use UnitBaseType
		navyItem = new TreeItem(tree, 0);
		airItem = new TreeItem(tree, 0);
		landItem = new TreeItem(tree, 0);
		techItem = new TreeItem(tree, 0);
		orbitItem = new TreeItem(tree, 0);
		navyItem.setText("Navy");
		airItem.setText("Air");
		landItem.setText("Land");
		techItem.setText("Tech");
		orbitItem.setText(ORBIT);
	}
	
	public TreeItem getNavyItem() {
		return navyItem;
	}
	public void setNavyItem(TreeItem navyItem) {
		this.navyItem = navyItem;
	}
	public TreeItem getAirItem() {
		return airItem;
	}
	public void setAirItem(TreeItem airItem) {
		this.airItem = airItem;
	}
	public TreeItem getLandItem() {
		return landItem;
	}
	public void setLandItem(TreeItem landItem) {
		this.landItem = landItem;
	}
	public TreeItem getTechItem() {
		return techItem;
	}
	public void setTechItem(TreeItem techItem) {
		this.techItem = techItem;
	}
	public TreeItem getOrbitItem() {
		return orbitItem;
	}
	public void setOrbitItem(TreeItem orbitItem) {
		this.orbitItem = orbitItem;
	}

	public void clear() {
		tree.removeAll();
	}
}
