package com.kenstevens.stratinit.server.daoservice;

import com.kenstevens.stratinit.dao.MessageDao;
import com.kenstevens.stratinit.model.Mail;
import com.kenstevens.stratinit.server.remote.TwoPlayerBase;
import com.kenstevens.stratinit.server.remote.mail.MailService;
import com.kenstevens.stratinit.server.remote.mail.MailTemplate;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MessageDaoServiceTest extends TwoPlayerBase {
	private final Mockery context = new Mockery();
	private MailService mailService;
	private MessageDao messageDao;

	@Autowired
	private MailService origMailService;
	@Autowired
	private MessageDao origMessageDao;

	@Autowired
	private MessageDaoService messageDaoService;

	@Before
	public void setupMocks() {

		mailService = context.mock(MailService.class);
		messageDao = context.mock(MessageDao.class);
		ReflectionTestUtils
				.setField(messageDaoService, "mailService", mailService);
		ReflectionTestUtils
		.setField(messageDaoService, "messageDao", messageDao);
	}

	@After
	public void undoMocks() {
		ReflectionTestUtils.setField(messageDaoService, "mailService",
				origMailService);
		ReflectionTestUtils.setField(messageDaoService, "messageDao",
				origMessageDao);
	}

	@Test
	public void sendMessage() {
		context.checking(new Expectations() {
			{
				oneOf(mailService).sendEmail(with(same(playerThem)), with(any(MailTemplate.class)));
				oneOf(messageDao).save(with(any(Mail.class)));
			}
		});
		Mail result = messageDaoService.sendMail(nationMe, nationThem, "test subject", "test body");
		assertEquals("test body", result.getBody());
		assertEquals("test subject", result.getSubject());
		assertEquals(nationMe, result.getFrom());
		assertEquals(nationThem, result.getTo());
		context.assertIsSatisfied();
	}

	@Test
	public void sendMessageBoard() {
		context.checking(new Expectations() {
			{
				oneOf(messageDao).save(with(any(Mail.class)));
			}
		});
		Mail result = messageDaoService.sendMail(nationMe, null, "test subject", "test body");
		assertEquals("test body", result.getBody());
		assertEquals("test subject", result.getSubject());
		assertEquals(nationMe, result.getFrom());
		assertNull(result.getTo());
		context.assertIsSatisfied();
	}
}
