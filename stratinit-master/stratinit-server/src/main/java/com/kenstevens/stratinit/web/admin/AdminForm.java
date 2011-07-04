package com.kenstevens.stratinit.web.admin;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.util.value.ValueMap;

import com.kenstevens.stratinit.remote.StratInit;
import com.kenstevens.stratinit.server.velocity.SpringContext;

public class AdminForm extends Form<ValueMap> {
	private static final long serialVersionUID = 1L;

	public AdminForm(String id) {
		super(id);
	}

	 @Override
     public final void onSubmit() {
		 StratInit stratInit = (StratInit) SpringContext.getBean("stratInit");
		 stratInit.shutdown();
		 info("SERVER SHUT DOWN");
	 }

}
