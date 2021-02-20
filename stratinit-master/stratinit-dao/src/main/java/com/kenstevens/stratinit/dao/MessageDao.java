package com.kenstevens.stratinit.dao;

import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Mail;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.audit.RelationChangeAudit;

public interface MessageDao {

	void save(Mail mail);

	void remove(Mail mail);

	Iterable<Mail> getMessages(Nation nation);

	Iterable<Mail> getMail(Nation nation);

	Iterable<Mail> getSentMail(Nation nation);

	Iterable<Mail> getAnnouncements(Game game);

	Iterable<Mail> getBulletins(Game game);

	Iterable<Mail> getNotifications(Nation nation);

	Iterable<RelationChangeAudit> getRelationChanges(Game game);

}