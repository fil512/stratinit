package com.kenstevens.stratinit.server.remote.rank;

//		E = 1/(1+10^(-(R-Rother)/C2))
//				R = own ELO
//				Rother = ELO of your opponent
//				C2 = Constant (currently 400)
//
//				Your ELO after the game is then:
//				Rnew = R + C1 * ( W - E )
//				C1 = Constant (currently 50)
//				W = percentage of win (currently only 100% for win, 0% for loss)
public class ELOCalculator {

	private static final int RANGE = 400;
	public static final int WEIGHT = 32;
	private final int winStartELO;
	private final int loseStartELO;
	private final int winNewELO;
	private final int loseNewELO;

	public ELOCalculator(int winStartELO, int loseStartELO) {
		this.winStartELO = winStartELO;
		this.loseStartELO = loseStartELO;
		double e = 1.0 / (1.0 + Math.pow(10, (((double)loseStartELO - (double)winStartELO) / RANGE)));
		this.winNewELO = winStartELO + (int)(WEIGHT * (1.0 - e));;
		this.loseNewELO = loseStartELO - (int)(WEIGHT * e);
	}

	public int getWinNewELO() {
		return winNewELO;
	}

	public int getLoseNewELO() {
		return loseNewELO;
	}

	public int getWinStartELO() {
		return winStartELO;
	}

	public int getLoseStartELO() {
		return loseStartELO;
	}

}
