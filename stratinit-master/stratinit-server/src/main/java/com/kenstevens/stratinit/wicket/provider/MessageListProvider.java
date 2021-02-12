package com.kenstevens.stratinit.wicket.provider;

import com.kenstevens.stratinit.dao.MessageDao;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Mail;
import com.kenstevens.stratinit.repo.GameRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class MessageListProvider {
    @Autowired
    GameRepo gameRepo;
    @Autowired
    MessageDao messageDao;

    private static final Comparator<Mail> messagesByDateComparator = new Comparator<Mail>() {
        @Override
        public int compare(Mail mail1, Mail mail2) {
            return mail2.getDate().compareTo(mail1.getDate());
        }
    };

    public List<Mail> getMessages(int gameId) {
        Optional<Game> game = gameRepo.findById(Integer.valueOf(gameId));
        if (!game.isPresent()) {
            return Collections.emptyList();
        }
        List<Mail> messages = messageDao.getAnnouncements(game.get());
        Collections.sort(messages, messagesByDateComparator);
        return messages;
    }
}
