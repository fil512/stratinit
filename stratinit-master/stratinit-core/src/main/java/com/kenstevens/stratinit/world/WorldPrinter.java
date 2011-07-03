package com.kenstevens.stratinit.world;

import java.io.PrintStream;

import com.kenstevens.stratinit.model.Sector;
import com.kenstevens.stratinit.model.World;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.SectorType;

public class WorldPrinter {
	private final World world;

	public WorldPrinter(World world) {
		this.world = world;
	}

	public void print() {
		print(System.out);
	}

	public void print(PrintStream out) {
		int size = world.size();
		for (int y = 0; y < size; ++y) {
			out.printf("%2d", y);
			for (int x = 0; x < size; ++x) {
				Sector sector = world.getSector(x, y);
				if (sector.getType().equals(SectorType.NEUTRAL_CITY)) {
					out.print('C');
				} else if (sector.getType().equals(SectorType.START_CITY)) {
						out.print('_');
				} else if (sector.getIsland() != Constants.UNASSIGNED && sector.getIsland() < world.getIslands()) {
						out.print(sector.getIsland());
				} else if (sector.getType().equals(SectorType.LAND)) {
					out.print('#');
				} else if (sector.getType().equals(SectorType.WATER)) {
					out.print('.');
				} else {
					out.print('?');
				}
			}
			out.println();
		}
	}
}
