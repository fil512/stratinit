package com.kenstevens.stratinit.server.rest.mail;

import com.kenstevens.stratinit.config.ServerConfig;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Player;

public final class MailTemplateLibrary {

    private MailTemplateLibrary() {
    }

    public static MailTemplate getRegistration(String username) {
        return new MailTemplate(MailTemplateType.REGISTRATION, "Strategic Initiative Registration", "Dear " + username + ",\n" +
                "\n" +
                "Thank you for registering with Strategic Initiative.  To join a game and play, follow the instructions at the top of the FAQ on\n" +
                "http://www.strategicinitiative.org/\n\n" +
                "See you in the game!\n" +
                "\nHydrogen\n");
    }

    public static MailTemplate getUpdatePlayer(Player player, String email) {
        return new MailTemplate(MailTemplateType.PLAYER_UPDATED, "Strategic Initiative Account Updated", "Dear " + player.getUsername() + ",\n" +
                "\n" +
                "Your account has been updated.  Your e-mail address is " + email + "\n" +
                "\nHydrogen\n");
    }

    public static MailTemplate getGameScheduled(Player player, Game game, ServerConfig serverConfig) {
        return new MailTemplate(MailTemplateType.GAME_SCHEDULED, "StratInit Game " + game.getGamename() + " Starting Tomorrow", "Dear " + player.getUsername() + ",\n" +
                "\n" +
                "Game #" + game.getGamename() + " will be mapped at " + game.getExpectedMapTimeString(serverConfig) + ".  Log in any time after then to set up your country.\n\n" +
                "The game clock in Game #" + game.getGamename() + " will start ticking at " + game.getStartTimeString() + ".\n" +
                "\nHydrogen\n");
    }

    public static MailTemplate getGameMapped(Player player, Game game) {
        return new MailTemplate(MailTemplateType.GAME_MAPPED, "StratInit Game " + game.getGamename() + " Started", "Dear " + player.getUsername() + ",\n" +
                "\n" +
                "Game #" + game.getGamename() + " has been mapped.  You may now login to set up your country.\n\n" +
                "The game clock in Game #" + game.getGamename() + " will start ticking at " + game.getStartTimeString() + ".\n" +
                "\nHydrogen\n");
    }

    public static MailTemplate getGameEmail(Nation from, String subject,
                                            String body) {
        Game game = from.getGame();
        return new MailTemplate(MailTemplateType.GAME_EMAIL, "StratInit Game " + game.getGamename() + ": " + subject,
                "Strategic Inititiative Game " + game.getGamename() + "\nIn-game message from " + from.getName() + ":\n\n" +
                        body);
    }

    public static MailTemplate getForgottenPassword(String username,
                                                    String password) {
        return new MailTemplate(MailTemplateType.RESET_PASSWORD, "StratInit password reset", "Strategic Initiative password for user '" + username + "' has been reset to " + password + ".\n\nTo change your password, login on http://www.strategicinitiative.org/ using this new password and go to the Account Settings tab.");
    }
}
