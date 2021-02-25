package com.kenstevens.stratinit.server.remote.helper;

import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.LogDaoService;
import com.kenstevens.stratinit.server.remote.mail.SMTPService;
import com.kenstevens.stratinit.server.remote.session.PlayerSession;
import com.kenstevens.stratinit.server.remote.session.PlayerSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ErrorProcessor {
    @Autowired
    private SMTPService smtpService;
    @Autowired
    private LogDaoService logDaoService;
    @Autowired
    private PlayerSessionFactory playerSessionFactory;

    public Result<Integer> processError(String subject, String stackTrace) {

        PlayerSession playerSession = playerSessionFactory.getPlayerSession();
        int errno = logDaoService.logError(playerSession.getGame(), playerSession.getPlayer(), stackTrace);
        String newSubject = subject + " error #" + errno;
        smtpService.sendException(newSubject, stackTrace);
        return new Result<Integer>("Error #" + errno + " logged.  When reporting this error, please make reference to this error number and what you were doing at the time.  Thanks!", true, errno);
    }

}
