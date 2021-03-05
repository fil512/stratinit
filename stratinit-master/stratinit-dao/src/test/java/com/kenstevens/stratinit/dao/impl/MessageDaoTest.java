package com.kenstevens.stratinit.dao.impl;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.StratInitTest;
import com.kenstevens.stratinit.client.model.Mail;
import com.kenstevens.stratinit.dao.MessageDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MessageDaoTest extends StratInitTest {
	private static final String SUB = "subject";
	private static final String BOD = "body";
	@Autowired
	private MessageDao messageDao;

	@Test
	public void testMail() {
		createNation2();
		Mail mail = new Mail(testGame, testNation1, testNation2, SUB, BOD);
		messageDao.save(mail);
		Iterable<Mail> messages = messageDao.getSentMail(testNation1);
		assertMessage(mail, messages);
		messages = messageDao.getMail(testNation2);
		assertMessage(mail, messages);
	}

	@Test
	public void testAnno() {
		createNation1();
		Mail mail = new Mail(testGame, testNation1, null, SUB, BOD);
		messageDao.save(mail);
		Iterable<Mail> messages = messageDao.getSentMail(testNation1);
		assertMessage(mail, messages);
		messages = messageDao.getAnnouncements(testGame);
		assertMessage(mail, messages);
	}

	@Test
	public void testNews() {
		createGame();
		Mail mail = new Mail(testGame, null, null, SUB, BOD);
		messageDao.save(mail);
		Iterable<Mail> messages = messageDao.getBulletins(testGame);
		assertMessage(mail, messages);
	}

	@Test
	public void testNotification() {
		createNation1();
		Mail mail = new Mail(testGame, null, testNation1, SUB, BOD);
		messageDao.save(mail);
		Iterable<Mail> messages = messageDao.getNotifications(testNation1);
		assertMessage(mail, messages);
	}

	private void assertMessage(Mail mail, Iterable<Mail> messages) {
		ArrayList<Mail> msgs = Lists.newArrayList(messages);
		assertEquals(1, msgs.size());
		Mail message = msgs.get(0);
		assertEquals(mail.getMessageId(), message.getMessageId());
		assertEquals(SUB, message.getSubject());
		assertEquals(BOD, message.getBody());
	}
}
