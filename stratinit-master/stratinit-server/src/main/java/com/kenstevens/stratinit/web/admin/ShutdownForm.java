package com.kenstevens.stratinit.web.admin;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.StratInit;
import com.kenstevens.stratinit.web.wicket.InfoResult;

public class ShutdownForm extends Form {
	@SpringBean
	StratInit stratInit;
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("serial")
	public ShutdownForm(String id) {
		super(id);

		Button shutdownButton = new Button("shutdownButton") {
			public void onSubmit() {
				Result<None> result = stratInit.shutdown();
				new InfoResult<None>(this).info(result);
			}
		};
		add(shutdownButton);
	}
}
