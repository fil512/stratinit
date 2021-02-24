package com.kenstevens.stratinit.world;

import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.type.CoordMeasure;
import com.kenstevens.stratinit.type.SectorCoords;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Random;

public class ElectronCloud implements CoordMeasure {
	private static final double PLAYER_CHARGE = 1.0;
	private final int numPlayers;
	private final int size;
	private final SectorCoords[] playerCoords;

	public ElectronCloud(int size, int numPlayers) {
		this.size = size;
		this.numPlayers = numPlayers;
		playerCoords = new SectorCoords[numPlayers];
	}

	public ElectronCloud(int size, int numPlayers, SectorCoords[] playerCoords) {
		this.size = size;
		this.numPlayers = numPlayers;
		this.playerCoords = Arrays.copyOf(playerCoords, playerCoords.length);
	}

	public void init(Game game) {
		Random random = new Random();
		for (int i = 0; i < numPlayers; ++i) {
			int x;
			int y;
			do {
				x = 1 + random.nextInt(size - 1);
				y = 1 + random.nextInt(size - 1);
			} while (alreadyPicked(i, x, y));
			playerCoords[i] = new SectorCoords(x, y);
		}
	}

	private boolean alreadyPicked(int i, int x, int y) {
		for (int j = 0; j < i; ++j) {
			if (playerCoords[j].x == x && playerCoords[j].y == y) {
				return true;
			}
		}
		return false;
	}

	public ElectronCloud drift() {
		ElectronCloud cloud = this;
		ElectronCloud betterCloud = cloud.nudgeToLowerEnergy();
		while (betterCloud != null) {
			// System.out.println("--");
			cloud = betterCloud;
			betterCloud = cloud.nudgeToLowerEnergy();
		}
		return cloud;
	}

	// Calculate the total potential energy of the system if the particles were
	// electrons
	public double getEnergy(int i, SectorCoords coords) {
		double energy = 0.0;
		for (int j = 0; j < numPlayers; ++j) {
			if (i == j) {
				continue;
			}
			int distanceSquared = playerCoords[j].euclidianDistanceSquared(
					this, coords);
			energy += PLAYER_CHARGE / distanceSquared;
		}
		return energy;
	}

	public ElectronCloud nudgeToLowerEnergy() {
		for (int i = 0; i < numPlayers; ++i) {
			SectorCoords sectorCoords = playerCoords[i];
			double startEnergy = getEnergy(i, sectorCoords);
			for (SectorCoords neighbour : sectorCoords.neighbours(size, 1)) {
				if (alreadyExists(i, neighbour)) {
					continue;
				}
				double newEnergy = getEnergy(i, neighbour);
				// System.out.println((newEnergy < startEnergy ? "*"+i+"\t" :
				// i+"\t")
				// + sectorCoords + "\t" + startEnergy + "\t" + neighbour
				// + "\t" + newEnergy);
				if (newEnergy < startEnergy) {
					ElectronCloud nudged = this.copy();
					nudged.setPlayerCoord(i, neighbour);
					return nudged;
				}
			}
		}

		return null;
	}

	private boolean alreadyExists(int i, SectorCoords neighbour) {
		for (int j = 0; j < numPlayers; ++j) {
			if (i == j) {
				continue;
			}
			if (neighbour.equals(playerCoords[j])) {
				return true;
			}
		}
		return false;
	}

	private void setPlayerCoord(int i, SectorCoords neighbour) {
		playerCoords[i] = neighbour;
	}

	public ElectronCloud copy() {
		SectorCoords[] coords = new SectorCoords[numPlayers];
		for (int i = 0; i < numPlayers; ++i) {
			coords[i] = playerCoords[i].copy();
		}
		return new ElectronCloud(size, numPlayers, coords);
	}

	public void print(PrintStream stream) {
		for (SectorCoords coords : playerCoords) {
			stream.println(coords);
		}
	}

	protected SectorCoords getPlayerCoord(int i) {
		return playerCoords[i];
	}

	@Override
	public int distance(SectorCoords source, SectorCoords target) {
		return SectorCoords.distance(size, source, target);
	}

	@Override
	public int size() {
		return size;
	}
}
