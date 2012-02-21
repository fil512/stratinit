package com.kenstevens.stratinit.server.remote.rank;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ELOCalculatorTest {
	@Test
	public void eloRankChange() {
		int winStartELO = 1500;
		int loseStartELO = 1500;
		ELOCalculator eloCalc = new ELOCalculator(winStartELO, loseStartELO);
		int winNewELO = eloCalc.getWinNewELO();
		int loseNewELO = eloCalc.getLoseNewELO();
		assertEquals(ELOCalculator.WEIGHT / 2, winNewELO - winStartELO);
		assertEquals(ELOCalculator.WEIGHT / 2, loseStartELO - loseNewELO);
	}

	@Test
	public void highEqual() {
		int winStartELO = 2000;
		int loseStartELO = 2000;
		ELOCalculator eloCalc = new ELOCalculator(winStartELO, loseStartELO);
		int winNewELO = eloCalc.getWinNewELO();
		int loseNewELO = eloCalc.getLoseNewELO();
		assertEquals(ELOCalculator.WEIGHT / 2, winNewELO - winStartELO);
		assertEquals(ELOCalculator.WEIGHT / 2, loseStartELO - loseNewELO);
	}

	@Test
	public void eloRankChangeNaturalWin() {
		int winStartELO = 1500;
		int loseStartELO = 1400;
		ELOCalculator eloCalc = new ELOCalculator(winStartELO, loseStartELO);
		int winNewELO = eloCalc.getWinNewELO();
		int loseNewELO = eloCalc.getLoseNewELO();
		assertEquals(11, winNewELO - winStartELO);
		assertEquals(20, loseStartELO - loseNewELO);
	}


	@Test
	public void eloRankChangeUpset() {
		int winStartELO = 1400;
		int loseStartELO = 1500;
		ELOCalculator eloCalc = new ELOCalculator(winStartELO, loseStartELO);
		int winNewELO = eloCalc.getWinNewELO();
		int loseNewELO = eloCalc.getLoseNewELO();
		assertEquals(20, winNewELO - winStartELO);
		assertEquals(11, loseStartELO - loseNewELO);
	}

	@Test
	public void eloRankChangeBigUpset() {
		int winStartELO = 1500;
		int loseStartELO = 2000;
		ELOCalculator eloCalc = new ELOCalculator(winStartELO, loseStartELO);
		int winNewELO = eloCalc.getWinNewELO();
		int loseNewELO = eloCalc.getLoseNewELO();
		assertEquals(30, winNewELO - winStartELO);
		assertEquals(1, loseStartELO - loseNewELO);
	}

}
