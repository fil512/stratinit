package com.kenstevens.stratinit.site.translator;

import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.dto.SICity;
import com.kenstevens.stratinit.model.CityView;

@Service
public class CityListTranslator extends ListTranslator<SICity, CityView> {

	@Override
	public CityView translate(SICity input) {
		return new CityView(db.getSelectedGame(), db.getNation(input.nationId), input);
	}

}
