package com.kenstevens.stratinit.site.translator;

import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.model.UnitView;

@Service
public class UnitListTranslator extends ListTranslator<SIUnit, UnitView> {

	@Override
	public UnitView translate(SIUnit input) {
		UnitView unitView = new UnitView(db.getNation(input.nationId), input);
		return unitView;
	}

}
