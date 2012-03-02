package com.kenstevens.stratinit.ui.shell;

import com.kenstevens.stratinit.model.SelectedCoords;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.type.SectorCoords;

public interface MapControl {

	public void centreMap(SectorCoords coords);

	public void scrollToSeeLocation(SectorCoords coords);

	public void setCanvasCoordsVisibility(boolean visibility);

	public void updateCoordsAndDistance(WorldView world, SectorCoords coords,
			SelectedCoords selectedCoords);

	public void centreAndScroll(SectorCoords coords);

	public void setCentreHomeEnabled(boolean b);

}
