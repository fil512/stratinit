package com.kenstevens.stratinit.client.gwt.model;


public class PlayerListGridRecord extends StratInitListGridRecord<String, GWTPlayer> {

	public PlayerListGridRecord(GWTPlayer player) {
		setValues(player);
	}

	@Override
	public final void setValues(GWTPlayer player) {
		setUsername(player.getUsername());
		setWins(player.getWins());
		setPlayed(player.getPlayed());
		setWinPerc(player.getWinPercentage() + "%");
		setEnabled(player.isEnabled() ? "" : "X");
		setEmailGameMail(player.isEmailGameMail() ? "" : "X");
		setEmail(player.getEmail());
	}
	public final void setUsername(String username) {
        setAttribute("name", username);
    }
	public final void setWins(int wins) {
        setAttribute("wins", wins);
    }
	public final void setPlayed(int played) {
        setAttribute("played", played);
    }
	public final void setWinPerc(String winperc) {
		setAttribute("winperc", winperc);
	}
	public final void setEnabled(String enabled) {
		setAttribute("enabled", enabled);
	}
	public final void setEmailGameMail(String emailGameMail) {
		setAttribute("emailGameMail", emailGameMail);
	}
	public final void setEmail(String email) {
        setAttribute("email", email);
    }
}
