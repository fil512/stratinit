package com.kenstevens.stratinit.wicket.admin;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.value.ValueMap;

import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.StratInit;
import com.kenstevens.stratinit.wicket.util.InfoResult;

public class ShutdownForm extends Form<ValueMap> {
	@SpringBean
	StratInit stratInit;
	private static final long serialVersionUID = 1L;

	public ShutdownForm(String id) {
		super(id);
	}
	
    @Override
    public final void onSubmit() {
    	Result<None> result = stratInit.shutdown();
    	new InfoResult<None>(this).info(result);
    }
}
