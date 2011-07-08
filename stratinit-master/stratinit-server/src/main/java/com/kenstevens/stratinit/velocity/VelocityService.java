package com.kenstevens.stratinit.velocity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.SAXException;

import com.kenstevens.stratinit.balance.BalanceResult;
import com.kenstevens.stratinit.balance.BalanceResultPersister;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Mail;

public class VelocityService extends SpringAware {
	Comparator<GameTable> gameByIdComparator = new Comparator<GameTable>() {
		@Override
		public int compare(GameTable game1, GameTable game2) {
			return Integer.valueOf(game1.getId()).compareTo(game2.getId());
		}
	};

	Comparator<Mail> messagesByDateComparator = new Comparator<Mail>() {
		@Override
		public int compare(Mail mail1, Mail mail2) {
			return mail2.getDate().compareTo(mail1.getDate());
		}
	};

	public List<GameTable> listGames(String mode) {
		List<GameTable> retval = new ArrayList<GameTable>();
		GameListProvider gameListProvider;
		if ("archive".equals(mode)) {
			gameListProvider = getArchiveGameListProvider();
		} else {
			gameListProvider = getActiveGameListProvider();
		}
		retval = gameListProvider.getGameTableList();
		Collections.sort(retval, gameByIdComparator);
		return retval;
	}
	

	public List<Mail> listMessages(String gameId) {
		Game game = getGameDao().findGame(Integer.valueOf(gameId));
		List<Mail> messages = getMessageDao().getAnnouncements(game);
		Collections.sort(messages, messagesByDateComparator);
		return messages;
	}
	
	public String format(String body) {
		return StringUtils.replace(body, "\n", "<br/>\n");
	}
	
	public List<BalanceResult> listBalanceResults() throws ParserConfigurationException, SAXException, IOException {
		BalanceResultPersister balanceResultPersister = new BalanceResultPersister();
		return balanceResultPersister.load().getBalanceResultList();
	}
}
