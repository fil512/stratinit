package com.kenstevens.stratinit.wicket.message;

import com.kenstevens.stratinit.client.model.Mail;
import com.kenstevens.stratinit.wicket.base.BasePage;
import com.kenstevens.stratinit.wicket.provider.MessageListProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

public class MessageBoardPage extends BasePage {
	private static final long serialVersionUID = 1L;
	@SpringBean
	MessageListProvider messageListProvider;

	public MessageBoardPage(PageParameters pageParameters) {
		super(pageParameters);
		add(new Label("name", pageParameters.get("name").toString()));
		add(new Label("id", pageParameters.get("id").toString()));
		final ListView<Mail> messageView = new MessageListView("messages", messageListProvider.getMessages(pageParameters.get("id").toInt()));
		add(messageView);

	}

	private final class MessageListView extends ListView<Mail> {

		private static final long serialVersionUID = 1L;

		private MessageListView(String id, List<Mail> model) {
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
	
	
}
