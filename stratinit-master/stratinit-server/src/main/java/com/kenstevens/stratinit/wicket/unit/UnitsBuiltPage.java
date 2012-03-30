package com.kenstevens.stratinit.wicket.unit;

import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.kenstevens.stratinit.wicket.BasePage;

public class UnitsBuiltPage extends BasePage {
	@SpringBean
	UnitsBuiltProvider unitsBuiltProvider;

	private static final long serialVersionUID = 1L;

	public UnitsBuiltPage() {
		super();
		UnitsBuiltListView unitsBuiltListView = new UnitsBuiltListView("unitsBuilt",
				new UnitsBuiltListModel(unitsBuiltProvider), 200);

		add(unitsBuiltListView);
		add(new PagingNavigator("navigator", unitsBuiltListView));
	}
}
