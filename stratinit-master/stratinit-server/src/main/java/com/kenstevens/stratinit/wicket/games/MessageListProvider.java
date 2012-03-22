package com.kenstevens.stratinit.wicket.games;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.dal.GameDal;
import com.kenstevens.stratinit.dao.MessageDao;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Mail;

@Service
public class MessageListProvider {
	@Autowired
	GameDal gameDal;
	@Autowired
	MessageDao messageDao;
	
	private static Comparator<Mail> messagesByDateComparator = new Comparator<Mail>() {
		@Override
		public int compare(Mail mail1, Mail mail2) {
			return mail2.getDate().compareTo(mail1.getDate());
		}
	};
	
	public List<Mail> getMessages(int gameId) {
		Game game = gameDal.findGame(Integer.valueOf(gameId));
		List<Mail> messages = messageDao.getAnnouncements(game);
		Collections.sort(messages, messagesByDateComparator);
		return messages;
	}
}
