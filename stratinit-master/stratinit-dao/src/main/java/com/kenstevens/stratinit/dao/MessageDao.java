package com.kenstevens.stratinit.dao;

import java.util.List;

import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Mail;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.audit.RelationChangeAudit;

public interface MessageDao {

	public abstract void persist(Mail mail);

	public abstract void remove(Mail mail);

	public abstract List<Mail> getMessages(Nation nation);

	public abstract List<Mail> getMail(Nation nation);

	public abstract List<Mail> getSentMail(Nation nation);

	public abstract List<Mail> getAnnouncements(Game game);

	public abstract List<Mail> getBulletins(Game game);

	public abstract List<Mail> getNotifications(Nation nation);

	public abstract List<RelationChangeAudit> getRelationChanges(Game game);

}