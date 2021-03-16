package com.kenstevens.stratinit.client.site.translator;

import com.kenstevens.stratinit.client.model.UnitView;
import com.kenstevens.stratinit.dto.SIUnit;
import org.springframework.stereotype.Service;

@Service
public class UnitListTranslator extends ListTranslator<SIUnit, UnitView> {

	@Override
	public UnitView translate(SIUnit input) {
		return new UnitView(db.getNation(input.nationId), input);
	}
}
