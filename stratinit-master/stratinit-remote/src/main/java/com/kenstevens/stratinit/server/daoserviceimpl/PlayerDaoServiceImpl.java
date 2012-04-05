package com.kenstevens.stratinit.server.daoserviceimpl;

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.EmailValidator;
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

	private final Log logger = LogFactory.getLog(getClass());
	@Autowired
	private PlayerDao playerDao;
	@Autowired
	private MailService mailService;

	private Result<Player> validatePlayer(Player player) {
		EmailValidator emailValidator = EmailValidator.getInstance();
		if (!emailValidator.isValid(player.getEmail())) {
			return new Result<Player>("Email [" + player.getEmail()
					+ "] is an invalid email address.", false);
		}
		return new Result<Player>(true);
	}

	public Result<Player> register(Player player) {
		Result<Player> result = validatePlayer(player);
		if (!result.isSuccess()) {
			return result;
		}
		if (playerDao.find(player.getUsername()) != null) {
			return new Result<Player>("Username '" + player.getUsername()
					+ "' already exists.  Please choose a different username.",
					false);
		}

		PlayerRole playerRole = new PlayerRole();
		playerRole.setPlayer(player);
		playerRole.setRoleName("ROLE_USER");

		playerDao.persist(player);
		playerDao.persist(playerRole);

		logger.info("REGISTERING USER [" + player.getUsername() + "].");
		mailService.sendEmail(player,
				MailTemplateLibrary.getRegistration(player.getUsername()));

		return new Result<Player>("New user [" + player.getUsername()
				+ "] saved.", true, player);
	}

	public Result<Player> updatePlayer(Player newPlayer) {
		Player player = playerDao.find(newPlayer.getUsername());
		if (player == null) {
			return new Result<Player>("No player with username [" + newPlayer.getUsername()
					+ "]found.", false);
		}
		Result<Player> result = validatePlayer(newPlayer);
		if (!result.isSuccess()) {
			return result;
		}
		if (newPlayer.getPassword() != null) {
			player.setPassword(newPlayer.getPassword());
		}
		player.setEmail(newPlayer.getEmail());
		player.setEmailGameMail(newPlayer.isEmailGameMail());
		player.setUserAgent(newPlayer.getUserAgent());
		player.setLastLogin(new Date());
		playerDao.merge(player);

		logger.info("UPDATING USER [" + player.getUsername() + "].");
		mailService.sendEmail(player,
				MailTemplateLibrary.getUpdatePlayer(player, player.getEmail()));

		return new Result<Player>("Account [" + player.getUsername()
				+ "] updated.", true, player);
	}

	public List<Player> getPlayers() {
		return playerDao.getAllPlayers();
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
		Player player = playerDao.find(username);
		if (player == null) {
			player = playerDao.findByEmail(email);
		}
		if (player == null) {
			return new Result<None>("No player with username [" + username
					+ "] or e-mail address [" + email + "]found.", false);
		}

		logger.info("PASSWORD RESET FOR USER [" + player.getUsername() + "].");

		String password = newRandomPassword();

		mailService.sendEmail(player,
				MailTemplateLibrary.getForgottenPassword(username, password));

		ShaPasswordEncoder encoder = new ShaPasswordEncoder();
		player.setPassword(encoder.encodePassword(password, null));
		playerDao.merge(player);

		return new Result<None>("Password reset for user [" + username + "].",
				true);
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

	@Override
	public boolean authorizePlayer(String username, String password) {
		Player player = playerDao.find(username);
		if (player == null) {
			return false;
		}
		ShaPasswordEncoder encoder = new ShaPasswordEncoder();
		String encodedPassword = encoder.encodePassword(password, null);

		if (!player.getPassword().equals(encodedPassword)) {
			return false;
		}
		if (!player.isEnabled()) {
			return false;
		}
		return true;
	}

	@Override
	public void setLastLogin(Player playerIn, Date now) {
		Player player = playerDao.find(playerIn.getId());
		player.setLastLogin(now);
		playerDao.merge(player);
	}

	@Override
	public Player findPlayer(String username) {
		return playerDao.find(username);
	}
}
