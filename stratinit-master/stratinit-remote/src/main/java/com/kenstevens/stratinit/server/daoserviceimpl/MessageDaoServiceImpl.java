package com.kenstevens.stratinit.server.daoserviceimpl;

import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.MessageDao;
import com.kenstevens.stratinit.dto.news.SINewsLogsDay;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Mail;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.server.daoservice.MessageDaoService;
import com.kenstevens.stratinit.server.remote.helper.NewsLogBuilder;
import com.kenstevens.stratinit.server.remote.mail.MailService;
import com.kenstevens.stratinit.server.remote.mail.MailTemplateLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageDaoServiceImpl implements MessageDaoService {
	@Autowired
	private MessageDao messageDao;
	@Autowired
	private GameDao gameDao;
	@Autowired
	private MailService mailService;
	@Autowired
	private NewsLogBuilder newsLogBuilder;

	public Mail sendMail(Nation from, Nation to, String subject, String body) {
		Mail mail = new Mail(from.getGame(), from, to, subject, body);
		messageDao.save(mail);
		newMail(to);
		if (to != null && to.getPlayer().isEmailGameMail()) {
			mailService.sendEmail(to.getPlayer(), MailTemplateLibrary.getGameEmail(from, subject, body));
		}
		return mail;
	}

	private void newMail(Nation to) {
		if (to == null) {
			return;
		}
		to.setNewMail(true);
		gameDao.markCacheModified(to);
	}

	public void notify(Nation to, String subject, String body) {
		Mail mail = new Mail(to.getGame(), null, to, subject, body);
		messageDao.save(mail);
		newMail(to);
	}

	public void postBulletin(Game game, String subject, String body) {
		Mail mail = new Mail(game, null, null, subject, body);
		messageDao.save(mail);
	}

	@Override
	public List<SINewsLogsDay> getNewsLogs(Game game) {
		return newsLogBuilder.getNews(game);
	}
}
