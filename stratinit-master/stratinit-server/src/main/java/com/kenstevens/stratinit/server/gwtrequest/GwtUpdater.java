package com.kenstevens.stratinit.server.gwtrequest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.client.gwt.model.GWTUpdate;
import com.kenstevens.stratinit.dto.SISector;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.server.gwtrequest.translate.GWTCityTranslate;
import com.kenstevens.stratinit.server.gwtrequest.translate.GWTNationTranslate;
import com.kenstevens.stratinit.server.gwtrequest.translate.GWTSectorTranslate;
import com.kenstevens.stratinit.server.gwtrequest.translate.GWTUnitTranslate;
import com.kenstevens.stratinit.server.remote.helper.PlayerCityList;
import com.kenstevens.stratinit.server.remote.helper.PlayerNationList;
import com.kenstevens.stratinit.server.remote.helper.PlayerUnitList;
import com.kenstevens.stratinit.server.remote.helper.PlayerWorldView;

@Service
public class GwtUpdater {
	@Autowired
	private PlayerWorldView playerWorldView;
	@Autowired
	private PlayerUnitList playerUnitList;
	@Autowired
	private PlayerCityList playerCityList;
	@Autowired
	private PlayerNationList playerNationList;


	public GWTUpdate getUpdate(Nation nation) {
		GWTUpdate retval = new GWTUpdate();
		List<SISector> sectors = playerWorldView.getWorldViewSectors(nation);
		retval.nationId = nation.getNationId();
		retval.nations = GWTNationTranslate.translate(playerNationList.getNations(nation, false));
		retval.sectors = GWTSectorTranslate.translate(sectors);
		retval.units = GWTUnitTranslate.translate(playerUnitList.getUnits(nation));
		retval.cities = GWTCityTranslate.translate(playerCityList.getCities(nation));
		return retval;

	}
}
