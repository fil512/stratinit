package com.kenstevens.stratinit.client.site.translator;

import com.kenstevens.stratinit.client.model.NationView;
import com.kenstevens.stratinit.dto.SINation;
import org.springframework.stereotype.Service;

@Service
public class NationListTranslator extends ListTranslator<SINation, NationView> {

	@Override
	public NationView translate(SINation input) {
		return new NationView(db.getSelectedGame(), input);
	}

}
