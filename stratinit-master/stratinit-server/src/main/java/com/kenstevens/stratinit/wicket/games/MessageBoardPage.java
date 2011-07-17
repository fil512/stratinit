package com.kenstevens.stratinit.wicket.games;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.kenstevens.stratinit.dal.GameDal;
import com.kenstevens.stratinit.dao.MessageDao;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Mail;
import com.kenstevens.stratinit.wicket.BasePage;

public class MessageBoardPage extends BasePage {
	private static final long serialVersionUID = 1L;
	@SpringBean
	GameDal gameDal;
	@SpringBean
	MessageDao messageDao;
	
	private final class MessageListView extends ListView<Mail> {

		private static final long serialVersionUID = 1L;

		private MessageListView(String id,
				IModel<? extends List<? extends Mail>> model) {
			super(id, model);
		}

		@Override
		protected void populateItem(ListItem<Mail> item) {
			final Mail mail = item.getModelObject();
			item.add(new Label("date", mail.getDateString()));
			item.add(new Label("from", "" + mail.getAuthor()));
			item.add(new Label("subject", "" + mail.getSubject()));
			item.add(new MultiLineLabel("body", mail.getBody()));
		}
	}
	
	public MessageBoardPage(PageParameters pageParameters) {
		super(pageParameters);
		add(new Label("name", pageParameters.get("name").toString()));
		add(new Label("id", pageParameters.get("id").toString()));
		final ListView<Mail> messageView = new MessageListView("messages",
				new MessageListModel(this));
		add(messageView);
		
	}
	
	private static Comparator<Mail> messagesByDateComparator = new Comparator<Mail>() {
		@Override
		public int compare(Mail mail1, Mail mail2) {
			return mail2.getDate().compareTo(mail1.getDate());
		}
	};
	
	public List<Mail> getMessages() {
		int gameId = getPageParameters().get("id").toInt();
		Game game = gameDal.findGame(Integer.valueOf(gameId));
		List<Mail> messages = messageDao.getAnnouncements(game);
		Collections.sort(messages, messagesByDateComparator);
		return messages;
	}
}
