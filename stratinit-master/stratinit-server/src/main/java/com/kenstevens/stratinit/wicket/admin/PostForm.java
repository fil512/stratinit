package com.kenstevens.stratinit.wicket.admin;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.StratInit;
import com.kenstevens.stratinit.wicket.util.InfoResult;

public class PostForm extends Form {
	private static final long serialVersionUID = 1L;

	@SpringBean
	StratInit stratInit;

	@SuppressWarnings("serial")
	public PostForm(String id) {
		super(id);

		// FIXME
		final TextField subject = new TextField("subject");
		final TextArea body = new TextArea("body");
		Button postButton = new Button("postButton") {
			public void onSubmit() {
				Result<Integer> result = stratInit.postAnnouncement(subject.getInput(), body.getInput());
				new InfoResult<Integer>(this).info(result);
			}
		};
		add(subject);
		add(body);
		add(postButton);
	}
}
