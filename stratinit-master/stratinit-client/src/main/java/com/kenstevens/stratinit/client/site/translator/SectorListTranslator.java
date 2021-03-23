package com.kenstevens.stratinit.client.site.translator;

import com.kenstevens.stratinit.client.model.SectorView;
import com.kenstevens.stratinit.client.model.WorldSector;
import com.kenstevens.stratinit.dto.SISector;
import com.kenstevens.stratinit.type.Constants;
import org.springframework.stereotype.Service;

@Service
public class SectorListTranslator extends ListTranslator<SISector, WorldSector> {

	@Override
	public WorldSector translate(SISector input) {
		WorldSector worldSector = new SectorView(input);
		worldSector.setNation(db.getNation(input.nationId));
		if (input.topUnitId != Constants.UNASSIGNED) {
			worldSector.setTopUnit(db.getUnitList().get(input.topUnitId));
		}
		return worldSector;
	}

}
