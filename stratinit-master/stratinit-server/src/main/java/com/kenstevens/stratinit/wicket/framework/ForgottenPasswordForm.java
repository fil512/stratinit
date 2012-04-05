package com.kenstevens.stratinit.wicket.framework;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.value.ValueMap;

import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.PlayerDaoService;
import com.kenstevens.stratinit.wicket.util.InfoResult;

public class ForgottenPasswordForm extends Form<ValueMap> {
	private static final long serialVersionUID = 1L;

	@SpringBean
	PlayerDaoService playerDaoService;

	public ForgottenPasswordForm(String id) {
		super(id, new CompoundPropertyModel<ValueMap>(new ValueMap()));

		add(new TextField<String>("username"));
		add(new TextField<String>("email"));

	}

	@Override
	public final void onSubmit() {
		ValueMap values = getModelObject();

		String username = (String) values.get("username");
		String email = (String) values.get("email");
		Result<None> result = playerDaoService.forgottenPassword(username, email);
		new InfoResult<None>(this).info(result);
	}
}
