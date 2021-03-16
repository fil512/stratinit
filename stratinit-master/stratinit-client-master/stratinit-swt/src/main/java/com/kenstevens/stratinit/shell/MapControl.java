package com.kenstevens.stratinit.shell;

import com.kenstevens.stratinit.client.model.SelectedCoords;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.type.SectorCoords;

public interface MapControl {

	void centreMap(SectorCoords coords);

	void scrollToSeeLocation(SectorCoords coords);

	void setCanvasCoordsVisibility(boolean visibility);

	void updateCoordsAndDistance(WorldView world, SectorCoords coords,
			SelectedCoords selectedCoords);

	void centreAndScroll(SectorCoords coords);

	void setCentreHomeEnabled(boolean b);

}
