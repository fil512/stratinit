package com.kenstevens.stratinit.server.remote.rank;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ELOCalculatorTest {
	@Test
	public void eloRankChange() {
		double winStartELO = 1500;
		double loseStartELO = 1500;
		ELOCalculator eloCalc = new ELOCalculator(winStartELO, loseStartELO);
		double winNewELO = eloCalc.getWinNewELO();
		double loseNewELO = eloCalc.getLoseNewELO();
		assertEquals(ELOCalculator.WEIGHT / 2, winNewELO - winStartELO, 0.1);
		assertEquals(ELOCalculator.WEIGHT / 2, loseStartELO - loseNewELO, 0.1);
	}

	@Test
	public void highEqual() {
		double winStartELO = 2000;
		double loseStartELO = 2000;
		ELOCalculator eloCalc = new ELOCalculator(winStartELO, loseStartELO);
		double winNewELO = eloCalc.getWinNewELO();
		double loseNewELO = eloCalc.getLoseNewELO();
		assertEquals(ELOCalculator.WEIGHT / 2, winNewELO - winStartELO, 0.1);
		assertEquals(ELOCalculator.WEIGHT / 2, loseStartELO - loseNewELO, 0.1);
	}

	@Test
	public void invertable() {
		double winStartELO = 2000;
		double loseStartELO = 2000;
		ELOCalculator eloCalc = new ELOCalculator(winStartELO, loseStartELO);
		double winNewELO = eloCalc.getWinNewELO();
		double loseNewELO = eloCalc.getLoseNewELO();
		ELOCalculator eloCalc2 = new ELOCalculator(loseNewELO, winNewELO);
		assertEquals(eloCalc2.getWinNewELO(), eloCalc2.getLoseNewELO(), 0.1);
	}

	@Test
	public void eloRankChangeNaturalWin() {
		double winStartELO = 1500;
		double loseStartELO = 1400;
		ELOCalculator eloCalc = new ELOCalculator(winStartELO, loseStartELO);
		double winNewELO = eloCalc.getWinNewELO();
		double loseNewELO = eloCalc.getLoseNewELO();
		assertEquals(11.5, winNewELO - winStartELO, 0.1);
		assertEquals(20.5, loseStartELO - loseNewELO, 0.1);
	}


	@Test
	public void eloRankChangeUpset() {
		double winStartELO = 1400;
		double loseStartELO = 1500;
		ELOCalculator eloCalc = new ELOCalculator(winStartELO, loseStartELO);
		double winNewELO = eloCalc.getWinNewELO();
		double loseNewELO = eloCalc.getLoseNewELO();
		assertEquals(20.5, winNewELO - winStartELO, 0.1);
		assertEquals(11.5, loseStartELO - loseNewELO, 0.1);
	}

	@Test
	public void eloRankChangeBigUpset() {
		double winStartELO = 1500;
		double loseStartELO = 2000;
		ELOCalculator eloCalc = new ELOCalculator(winStartELO, loseStartELO);
		double winNewELO = eloCalc.getWinNewELO();
		double loseNewELO = eloCalc.getLoseNewELO();
		assertEquals(30.3, winNewELO - winStartELO, 0.1);
		assertEquals(1.7, loseStartELO - loseNewELO, 0.1);
	}
}
