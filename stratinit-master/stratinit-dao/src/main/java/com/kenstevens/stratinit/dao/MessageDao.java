package com.kenstevens.stratinit.dao;

import java.util.List;

import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Mail;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.audit.RelationChangeAudit;

public interface MessageDao {

	void persist(Mail mail);

	void remove(Mail mail);

	List<Mail> getMessages(Nation nation);

	List<Mail> getMail(Nation nation);

	List<Mail> getSentMail(Nation nation);

	List<Mail> getAnnouncements(Game game);

	List<Mail> getBulletins(Game game);

	List<Mail> getNotifications(Nation nation);

	List<RelationChangeAudit> getRelationChanges(Game game);

}