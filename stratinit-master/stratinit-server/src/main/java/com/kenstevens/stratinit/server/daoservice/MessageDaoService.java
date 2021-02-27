package com.kenstevens.stratinit.server.daoservice;

import com.kenstevens.stratinit.dto.news.SINewsLogsDay;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Mail;
import com.kenstevens.stratinit.model.Nation;

import java.util.List;

public interface MessageDaoService {

    Mail sendMail(Nation from, Nation to, String subject,
                  String body);

    void notify(Nation to, String subject, String body);

    void postBulletin(Game game, String subject, String body);

    List<SINewsLogsDay> getNewsLogs(Game game);

}