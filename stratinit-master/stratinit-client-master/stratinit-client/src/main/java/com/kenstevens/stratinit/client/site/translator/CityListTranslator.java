package com.kenstevens.stratinit.client.site.translator;

import com.kenstevens.stratinit.client.model.CityView;
import com.kenstevens.stratinit.dto.SICity;
import org.springframework.stereotype.Service;

@Service
public class CityListTranslator extends ListTranslator<SICity, CityView> {

	@Override
	public CityView translate(SICity input) {
		return new CityView(db.getSelectedGame(), db.getNation(input.nationId), input);
	}

}
