package com.kenstevens.stratinit.server.daoservice;

import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.MessageDao;
import com.kenstevens.stratinit.helper.NationHelper;
import com.kenstevens.stratinit.model.Mail;
import com.kenstevens.stratinit.server.rest.mail.MailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class MessageDaoServiceTest {

    static final NationHelper nationHelper = new NationHelper();

    @Mock
    private MessageDao messageDao;
    @Mock
    private GameDao gameDao;
    @Mock
    private MailService mailService;

    @InjectMocks
    private MessageDaoService messageDaoService;

    @Test
    public void sendMessage() {
        Mail result = messageDaoService.sendMail(NationHelper.nationMe, NationHelper.nationThem, "test subject", "test body");
        assertEquals("test body", result.getBody());
        assertEquals("test subject", result.getSubject());
        assertEquals(NationHelper.nationMe, result.getFrom());
        assertEquals(NationHelper.nationThem, result.getTo());
        verify(messageDao).save(any());
        verify(gameDao).markCacheModified(NationHelper.nationThem);
        verify(mailService).sendEmail(any(), any());
    }

    @Test
    public void sendMessageBoard() {
        Mail result = messageDaoService.sendMail(NationHelper.nationMe, null, "test subject", "test body");
        assertEquals("test body", result.getBody());
        assertEquals("test subject", result.getSubject());
        assertEquals(NationHelper.nationMe, result.getFrom());
        assertNull(result.getTo());
        verify(messageDao).save(any());
        verifyNoInteractions(gameDao);
        verifyNoInteractions(mailService);
    }
}
