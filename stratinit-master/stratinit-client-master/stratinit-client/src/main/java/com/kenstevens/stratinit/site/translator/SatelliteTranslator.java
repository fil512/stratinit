package com.kenstevens.stratinit.site.translator;

import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.dto.SILaunchedSatellite;
import com.kenstevens.stratinit.model.LaunchedSatellite;
import com.kenstevens.stratinit.model.LaunchedSatelliteView;

@Service
public class SatelliteTranslator extends ListTranslator<SILaunchedSatellite, LaunchedSatellite> {

	@Override
	public LaunchedSatellite translate(SILaunchedSatellite input) {
		LaunchedSatellite sat = new LaunchedSatelliteView(input);
		sat.setNation(db.getNation(input.nationId));
		return sat;
	}

}
