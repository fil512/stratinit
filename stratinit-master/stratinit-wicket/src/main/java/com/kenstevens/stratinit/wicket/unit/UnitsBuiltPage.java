package com.kenstevens.stratinit.wicket.unit;

import com.kenstevens.stratinit.wicket.base.BasePage;
import com.kenstevens.stratinit.wicket.model.GameUnitsBuiltListModel;
import com.kenstevens.stratinit.wicket.provider.UnitsBuiltProvider;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class UnitsBuiltPage extends BasePage {
	@SpringBean
	UnitsBuiltProvider unitsBuiltProvider;

	private static final long serialVersionUID = 1L;

	public UnitsBuiltPage() {
		super();
		
		GameUnitsBuiltListView unitsBuiltListView = new GameUnitsBuiltListView("gameUnitsBuilt",
				new GameUnitsBuiltListModel(unitsBuiltProvider), 5);

		add(unitsBuiltListView);
		add(new PagingNavigator("navigator", unitsBuiltListView));
	}
}
