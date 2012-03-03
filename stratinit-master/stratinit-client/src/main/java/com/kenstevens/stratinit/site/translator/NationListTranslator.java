package com.kenstevens.stratinit.site.translator;

import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.dto.SINation;
import com.kenstevens.stratinit.model.NationView;

@Service
public class NationListTranslator extends ListTranslator<SINation, NationView> {

	@Override
	public NationView translate(SINation input) {
		return new NationView(db.getSelectedGame(), input);
	}

}
