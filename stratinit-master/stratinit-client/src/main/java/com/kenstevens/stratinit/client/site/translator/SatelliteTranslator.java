package com.kenstevens.stratinit.client.site.translator;

import com.kenstevens.stratinit.client.model.LaunchedSatellite;
import com.kenstevens.stratinit.client.model.LaunchedSatelliteView;
import com.kenstevens.stratinit.dto.SILaunchedSatellite;
import org.springframework.stereotype.Service;

@Service
public class SatelliteTranslator extends ListTranslator<SILaunchedSatellite, LaunchedSatellite> {

	@Override
	public LaunchedSatellite translate(SILaunchedSatellite input) {
		LaunchedSatellite sat = new LaunchedSatelliteView(input);
		sat.setNation(db.getNation(input.nationId));
		return sat;
	}

}
