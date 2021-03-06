package com.kenstevens.stratinit.wicket.admin;

import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.svc.StratInitAdmin;
import com.kenstevens.stratinit.wicket.util.InfoResult;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.value.ValueMap;

public class PostForm extends Form<ValueMap> {
	private static final long serialVersionUID = 1L;

	@SpringBean
	StratInitAdmin stratInitAdmin;

	public PostForm(String id) {
		super(id, new CompoundPropertyModel<ValueMap>(new ValueMap()));

		add(new TextField<String>("subject"));
		add(new TextArea<String>("body"));

	}

	@Override
	public final void onSubmit() {
		ValueMap values = getModelObject();

		String subject = (String) values.get("subject");
		String body = (String) values.get("body");
		Result<Integer> result = stratInitAdmin.postAnnouncement(subject, body);
		new InfoResult<Integer>(this).info(result);
	}
}
