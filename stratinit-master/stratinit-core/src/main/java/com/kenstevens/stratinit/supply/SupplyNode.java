package com.kenstevens.stratinit.supply;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.kenstevens.stratinit.type.SectorCoords;

public abstract class SupplyNode implements Iterable<SupplyNode> {
	private final List<SupplyNode> children = new ArrayList<SupplyNode>();
	private final SupplyNode parent;

	protected SupplyNode(SupplyNode parent) {
		this.parent = parent;
	}

	public boolean isRoot() {
		return parent == null;
	}

	public void addChild(SupplyNode childNode) {
		children.add(childNode);
	}
	
	
	
	@Override
	public Iterator<SupplyNode> iterator() {
		return children.iterator();
	}

	public abstract SectorCoords getSectorCoords();
}
