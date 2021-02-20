package com.kenstevens.stratinit.dao.impl;

import com.kenstevens.stratinit.dao.MessageDao;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Mail;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.QMail;
import com.kenstevens.stratinit.model.audit.QRelationChangeAudit;
import com.kenstevens.stratinit.model.audit.RelationChangeAudit;
import com.kenstevens.stratinit.repo.MailRepo;
import com.kenstevens.stratinit.repo.RelationChangeAuditRepo;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;

@SuppressWarnings("unchecked")
@Service
public class MessageDaoImpl implements MessageDao {
    @Autowired
    MailRepo mailRepo;
    @Autowired
    RelationChangeAuditRepo relationChangeAuditRepo;

    @Override
    public void save(Mail mail) {
        mailRepo.save(mail);
    }

    @Override
    public void remove(@Nonnull Mail mail) {
        mailRepo.delete(mail);
    }

    @Override
    public Iterable<Mail> getMessages(Nation nation) {
        QMail mail = QMail.mail;
        return mailRepo.findAll((mail.to.eq(nation).or(mail.from.eq(nation))).or(isAnnouncement(mail)));

    }

    private Predicate isAnnouncement(QMail mail) {
        return mail.to.isNull().and(mail.from.isNull());
    }

    @Override
    public Iterable<Mail> getMail(Nation nation) {
        return mailRepo.findAll(QMail.mail.to.eq(nation));
    }

    @Override
    public Iterable<Mail> getSentMail(Nation nation) {
        return mailRepo.findAll(QMail.mail.from.eq(nation));
    }

    @Override
    public Iterable<Mail> getAnnouncements(Game game) {
        QMail mail = QMail.mail;
        return mailRepo.findAll(adminAnnouncements(mail, game).or(gameAnnouncements(mail, game)));
    }

    private Predicate gameAnnouncements(QMail mail, Game game) {
        return mail.to.isNull().and(mail.from.isNull()).and(mail.body.isNotNull()).and(mail.game.eq(game));
    }

    private BooleanExpression adminAnnouncements(QMail mail, Game game) {
        return mail.to.isNull().and(mail.from.isNotNull()).and(mail.game.eq(game));
    }

    @Override
    public Iterable<Mail> getBulletins(Game game) {
        QMail mail = QMail.mail;
        return mailRepo.findAll(mail.from.nationPK.player.isNull().and(mail.to.nationPK.player.isNull()).and(mail.game.eq(game)));
    }

    @Override
    public Iterable<Mail> getNotifications(Nation nation) {
        QMail mail = QMail.mail;
        return mailRepo.findAll(mail.from.isNull().and(mail.to.eq(nation)));
    }

    @Override
    public Iterable<RelationChangeAudit> getRelationChanges(Game game) {
        return relationChangeAuditRepo.findAll(QRelationChangeAudit.relationChangeAudit.gameId.eq(game.getId()));
    }
}
