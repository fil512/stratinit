package com.kenstevens.stratinit.move;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.rollerjm.graph.IGraph;
import org.rollerjm.graph.Path;

import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.WorldSector;

public class SectorGraph implements IGraph<WorldSector>{
	private WorldView worldView;
	private Unit unit;
	
	public SectorGraph(Unit unit, WorldView world) {
		this.worldView = world;
		this.unit = unit;
	}
	
	@Override
	public void addEdge(WorldSector startVertex, WorldSector destinationVertex,
			double weight) {
		// not used
	}

	@Override
	public void addVertex(WorldSector vertex) {
		// not used
	}

	@Override
	public boolean edgeExist(WorldSector startVertex, WorldSector destinationVertex) {
		if (!worldView.getNeighbours(startVertex).contains(destinationVertex)) {
			return false;
		}
		if (!destinationVertex.canEnter(unit)) {
			return false;
		}
		return true;
	}

	@Override
	public Iterator<WorldSector> getAdjacentVertices(WorldSector vertex) {
		List<WorldSector>neighbours = worldView.getNeighbours(vertex);
		List<WorldSector>retval = new ArrayList<WorldSector>();
		for (WorldSector sector : neighbours) {
			if (sector.canEnter(unit)) {
				retval.add(sector);
			}
		}
		return retval.iterator();
	}

	@Override
	public double getEdgeWeight(WorldSector startVertex, WorldSector destinationVertex) {
		return startVertex.isInSupply() ? 1 : 2;
	}

	@Override
	public double getEdgeWeight(Path<WorldSector> path) {
		int weight = 0;
		for (int i = 0; i < path.getLength(); ++i) {
			weight += path.get(i).isInSupply() ? 1 : 2;
		}
		return weight;
	}

	@Override
	public Iterator<WorldSector> getPredecessors(WorldSector vertex) {
		return getAdjacentVertices(vertex);
	}

	@Override
	public int getVerticesNumber() {
		return worldView.size()*worldView.size();
	}

	@Override
	public void removeEdge(WorldSector startVertex, WorldSector destinationVertex) {
		// not required
	}

	@Override
	public boolean vertexExist(WorldSector vertex) {
		// I don't think we care
		return true;
	}
}
