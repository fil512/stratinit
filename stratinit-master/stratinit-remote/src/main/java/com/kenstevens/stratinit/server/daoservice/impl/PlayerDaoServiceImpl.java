package com.kenstevens.stratinit.server.daoservice.impl;

import java.util.List;
import java.util.Random;

import org.apache.commons.validator.EmailValidator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.dao.PlayerDao;
import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.model.PlayerRole;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.PlayerDaoService;
import com.kenstevens.stratinit.server.remote.mail.MailService;
import com.kenstevens.stratinit.server.remote.mail.MailTemplateLibrary;

@Service
public class PlayerDaoServiceImpl implements PlayerDaoService {
	private static final Random RANDOM = new Random();

	private static final int RANDOM_PASSWORD_LENGTH = 8;
	
	private Logger logger = Logger.getLogger(getClass());
	@Autowired
	private PlayerDao playerDao;
	@Autowired
	private MailService mailService;

	public Result<Player> register(String username, String password, String email) {

		if (playerDao == null) {
			return new Result<Player>("playerDao is null", false);
		}
		if (playerDao.find(username) != null) {
			return new Result<Player>("Username '" + username
					+ "' already exists.  Please choose a different username.", false);
		}
		Player player = new Player(username);
		Result<Player> result = setPlayer(player, password, email, true);
		PlayerRole playerRole = new PlayerRole();
		playerRole.setPlayer(player);
		playerRole.setRoleName("ROLE_USER");

		playerDao.persist(player);
		playerDao.persist(playerRole);

		if (!result.isSuccess()) {
			return result;
		}

		logger.info("REGISTERING USER [" + player.getUsername() + "].");
		mailService.sendEmail(player, MailTemplateLibrary.getRegistration(username));

		return new Result<Player>("New user [" + username + "] saved.", true, player);
	}

	private Result<Player> setPlayer(Player player, String password, String email, boolean emailGameMail) {
		EmailValidator emailValidator = EmailValidator.getInstance();
		if (!emailValidator.isValid(email)) {
			return new Result<Player>("Email ["+email+"] is an invalid email address.", false);
		}
		player.setEnabled(true);
		if (password != null) {
			player.setPassword(password);
		}
		player.setEmail(email);
		player.setEmailGameMail(emailGameMail);

		return new Result<Player>("Account "+player.getUsername()+" updated", true, player);
	}

	public List<Player> getPlayers() {
		return playerDao.getAllPlayers();
	}

	public Result<Player> updatePlayer(Player player, String password,
			String email, boolean emailGameMail) {
		if (playerDao == null) {
			return new Result<Player>("playerDao is null", false);
		}

		Result<Player> result = setPlayer(player, password, email, emailGameMail);
		if (!result.isSuccess()) {
			return result;
		}
		playerDao.merge(player);

		logger.info("UPDATING USER [" + player.getUsername() + "].");
		mailService.sendEmail(player, MailTemplateLibrary.getUpdatePlayer(player, email));

		return new Result<Player>("Account [" + player.getUsername() + "] updated.", true, player);

	}

	@Override
	public boolean isAdmin(Player player) {
		List<PlayerRole> playerRoles = playerDao.getRoles(player);
		boolean retval = false;
		for (PlayerRole role : playerRoles) {
			if (role.isAdmin()) {
				retval = true;
				break;
			}
		}
		return retval;

	}

	@Override
	public Result<None> forgottenPassword(String username, String email) {
		if (playerDao == null) {
			return new Result<None>("playerDao is null", false);
		}
		Player player = playerDao.find(username);
		if (player == null) {
			player = playerDao.findByEmail(email);
		}
		if (player == null) {
			return new Result<None>("No player with username [" + username
					+ "] or e-mail address ["+email+"]found.", false);
		}

		logger.info("PASSWORD RESET FOR USER [" + player.getUsername() + "].");
		
		String password = newRandomPassword();
		
		mailService.sendEmail(player, MailTemplateLibrary.getForgottenPassword(username, password));

		ShaPasswordEncoder encoder = new ShaPasswordEncoder();
		player.setPassword(encoder.encodePassword(password, null));
		playerDao.merge(player);

		return new Result<None>("Password reset for user [" + username + "].", true);
	}

	private String newRandomPassword() {
		String password = "";
		char a = 'a';
		char z = 'z';
		for (int i = 0; i < RANDOM_PASSWORD_LENGTH; ++i) {
			char letter = (char) (a + RANDOM.nextInt(z - a));
			password += letter;
		}
		return password;
	}
}
