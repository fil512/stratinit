package com.kenstevens.stratinit.server.rest.svc;

import com.kenstevens.stratinit.dto.SISector;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.SectorSeen;
import com.kenstevens.stratinit.model.WorldSector;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.type.CoordMeasure;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.type.SectorCoords;

import java.util.*;

public class WorldSeenMap implements CoordMeasure {
	private final Map<SectorCoords, SectorSeen> sectorMap = new HashMap<SectorCoords, SectorSeen>();
	private final Map<SectorCoords, WorldView> worldViewMap = new HashMap<SectorCoords, WorldView>();
	private final WorldView myWorldView;

	public WorldSeenMap(WorldView myWorldView) {
		this.myWorldView = myWorldView;
	}

	public void merge(Collection<SectorSeen> sectorsSeen, WorldView worldView) {
		for (SectorSeen sectorSeen : sectorsSeen) {
			SectorCoords coords = sectorSeen.getCoords();
			SectorSeen prev = sectorMap.get(coords);
			if (prev == null) {
				sectorMap.put(coords, sectorSeen);
				worldViewMap.put(coords, worldView);
			}
		}
	}

	public List<SISector> sectorsToSISectors() {
		List<SISector> retval = new ArrayList<SISector>();
		for (SectorCoords coords : sectorMap.keySet()) {
			SectorSeen sectorSeen = sectorMap.get(coords);
			WorldView sectorWorldView = worldViewMap.get(coords);
			SISector sisector = sectorSeenToSISector(sectorSeen, sectorWorldView);
			retval.add(sisector);
		}
		return retval;
	}

	private SISector sectorSeenToSISector(SectorSeen sectorSeen,
										  WorldView sectorWorldView) {
		WorldSector worldSector = sectorWorldView.getWorldSector(sectorSeen
				.getCoords());
		SISector sisector = new SISector(worldSector);
		sisector.lastSeen = sectorSeen.getLastSeen();
		sisector.myRelation = myWorldView.getMyRelation(worldSector.getNation());
		sisector.theirRelation = myWorldView.getTheirRelation(worldSector
				.getNation());
		return sisector;
	}

	public void merge(Collection<SectorSeen> sectorsSeen) {
		merge(sectorsSeen, myWorldView);
	}

	public Set<SectorCoords> keySet() {
		return sectorMap.keySet();
	}

	public RelationType getMyRelation(Nation nation) {
		return myWorldView.getMyRelation(nation);
	}

	public WorldSector getWorldSector(SectorCoords coords) {
		WorldView sectorWorldView = worldViewMap.get(coords);
		return sectorWorldView.getWorldSector(coords);
	}

	@Override
	public int distance(SectorCoords source, SectorCoords target) {
		return myWorldView.distance(source, target);
	}

	@Override
	public int size() {
		return myWorldView.size();
	}

	public List<WorldSector> getSectorsWithin(SectorCoords coords, int sight,
											  boolean includeSelf) {
		return myWorldView.getNeighbours(coords, sight, includeSelf);
	}


}
