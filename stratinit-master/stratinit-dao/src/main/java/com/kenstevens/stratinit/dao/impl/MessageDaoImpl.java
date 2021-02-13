package com.kenstevens.stratinit.dao.impl;

import com.kenstevens.stratinit.dao.MessageDao;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Mail;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.audit.RelationChangeAudit;
import com.kenstevens.stratinit.repo.MailRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
@Service
public class MessageDaoImpl implements MessageDao {
	@PersistenceContext
	protected EntityManager entityManager;
	@Autowired
	MailRepo mailRepo;

	@Override
	public void save(Mail mail) {
		mailRepo.save(mail);
	}

	@Override
	public void remove(Mail mail) {
		if (mail == null) {
			return;
		}
		int id = mail.getMessageId();
		Mail foundMail = entityManager.find(Mail.class, id);
		if (foundMail != null) {
			entityManager.remove(foundMail);
		}
	}

	@Override
	public List<Mail> getMessages(Nation nation) {
		return entityManager
				.createQuery(
						"from Mail m WHERE (m.to = :nation or m.from = :nation) or (m.to is null and m.from is null)")
				.setParameter("nation", nation).getResultList();
	}

	@Override
	public List<Mail> getMail(Nation nation) {
		return entityManager
				.createQuery(
						"from Mail m WHERE m.to = :nation")
				.setParameter("nation", nation).getResultList();
	}

	@Override
	public List<Mail> getSentMail(Nation nation) {
		return entityManager
				.createQuery(
						"from Mail m WHERE m.from = :nation")
				.setParameter("nation", nation).getResultList();
	}

	@Override
	public List<Mail> getAnnouncements(Game game) {
		List<Mail>retval = new ArrayList<Mail>();
		retval.addAll(entityManager
				.createQuery(
						"from Mail m WHERE m.to is null and m.from is not null and m.game = :game")
				.setParameter("game", game).getResultList());
		retval.addAll(entityManager
				.createQuery(
						"from Mail m WHERE m.to is null and m.from is null and m.body is not null and m.game = :game")
				.setParameter("game", game).getResultList());
		return retval;
	}

	@Override
	public List<Mail> getBulletins(Game game) {
		return entityManager
				.createQuery(
						"from Mail m WHERE m.from.nationPK.player is null and m.to.nationPK.player is null and m.game = :game")
				.setParameter("game", game).getResultList();
	}

	@Override
	public List<Mail> getNotifications(Nation nation) {
		return entityManager
				.createQuery(
						"from Mail m WHERE m.from is null and m.to = :nation")
				.setParameter("nation", nation).getResultList();
	}

	@Override
	public List<RelationChangeAudit> getRelationChanges(Game game) {
		return entityManager
		.createQuery(
				"from RelationChangeAudit r WHERE r.gameId = :gameId")
		.setParameter("gameId", game.getId()).getResultList();
	}
}
