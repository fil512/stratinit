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

	private static final double RANGE = 400;
	public static final double WEIGHT = 32;
	private final double winStartELO;
	private final double loseStartELO;
	private final double winNewELO;
	private final double loseNewELO;

	public ELOCalculator(double winStartELO, double loseStartELO) {
		this.winStartELO = winStartELO;
		this.loseStartELO = loseStartELO;
		double e = 1.0 / (1.0 + Math.pow(10, (((double)loseStartELO - (double)winStartELO) / RANGE)));
		this.winNewELO = winStartELO + (WEIGHT * (1.0 - e));
		this.loseNewELO = loseStartELO - (WEIGHT * e);
	}

	public double getWinNewELO() {
		return winNewELO;
	}

	public double getLoseNewELO() {
		return loseNewELO;
	}

	public double getWinStartELO() {
		return winStartELO;
	}

	public double getLoseStartELO() {
		return loseStartELO;
	}

}
