package com.kenstevens.stratinit.ui.window.map;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.control.selection.SelectEvent;
import com.kenstevens.stratinit.model.Account;
import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.NationView;
import com.kenstevens.stratinit.model.SelectedCoords;
import com.kenstevens.stratinit.model.SelectedNation;
import com.kenstevens.stratinit.model.UnitBase;
import com.kenstevens.stratinit.model.WorldSector;
import com.kenstevens.stratinit.shell.WidgetContainer;
import com.kenstevens.stratinit.type.CityType;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.SectorType;
import com.kenstevens.stratinit.type.UnitType;
import com.kenstevens.stratinit.ui.image.ImageLibrary;
import com.kenstevens.stratinit.ui.window.LineStyle;
import com.kenstevens.stratinit.ui.window.MapImageManager;

@Service
public class SectorDrawerService {
	@Autowired
	Data db;
	@Autowired
	MapDrawerService mapDrawer;
	@Autowired
	UnitDrawerService unitDrawer;
	@Autowired
	Account account;
	@Autowired
	ImageLibrary imageLibrary;
	@Autowired
	MapImageManager mapImageManager;
	@Autowired
	SelectedCoords selectedCoords;
	@Autowired
	WidgetContainer widgetContainer;
	@Autowired
	SelectedNation selectedNation;
	@Autowired
	SelectEvent selectEvent;
	
	public void drawSectors(int boardSize, GC gc) {
		for (int x = 0; x < boardSize; ++x) {
			for (int y = 0; y < boardSize; ++y) {
				drawSector(gc, new SectorCoords(x, y));
			}
		}
	}


	private void drawSector(GC gc, SectorCoords coords) {
		int boardSize = db.getBoardSize();
		SectorCoords worldCoords = new SectorCoords(boardSize, coords);
		WorldSector sector = db.getWorld().getWorldSector(worldCoords);
		Image image = getSectorImage(sector);
		mapDrawer.drawSquare(gc, coords, image);

		unitDrawer.drawUnit(gc, sector);

		if (sector == null) {
			return;
		}

		if (sector.isPlayerCity()) {
			City city = db.getCity(sector);
			if (city != null
					&& sector.isMine()
					&& (account.getPreferences().isShowBuilding() || widgetContainer
							.getTabControl().cityTabSelected())) {
				mapDrawer.drawSquare(gc, sector.getCoords(), imageLibrary.getBlankCity());
				UnitType unitType = city.getBuild();
				if (UnitBase.isUnit(unitType)) {
					Image buildTypeImage = imageLibrary.getUnitImage(null,
							unitType);
					mapDrawer.drawSquare(gc, coords, buildTypeImage);
				}
			} else {
				drawCity(gc, sector);
			}
		}
		if (account.getPreferences().isShowFOW() && !sector.isVisible()) {
			mapDrawer.drawSquare(gc, coords, imageLibrary.getFOW());
		}
	}
	
	private void drawCity(GC gc, WorldSector sector) {
		CityType cityType = sector.getCityType();
		if (cityType != null) {
			Image image;
			if (isNationSelected(sector.getNation())) {
				image = imageLibrary.getDestroyed(cityType);
			} else {
				RelationType relationType = sector.getMyRelation();
				image = imageLibrary.getCityImage(relationType, cityType);
			}
			mapDrawer.drawSquare(gc, sector.getCoords(), image);
		}
	}

	protected boolean isNationSelected(Nation theNation) {
		NationView nation = selectedNation.getPlayer();
		return widgetContainer.getTabControl().playerTabSelected()
				&& nation != null && nation.equals(theNation);
	}

	public void drawCityMove(City city) {
		if (city == null || city.getCityMove() == null) {
			return;
		}
		mapDrawer.drawLine(city.getCoords(), city.getCityMove().getCoords(),
				LineStyle.CITY_MOVE);
	}




	private Image getSectorImage(WorldSector sector) {
		Image image = null;
		if (sector != null) {
			SectorType type = sector.getType();
			if (type == SectorType.PLAYER_CITY) {
				// for drawing purposes
				type = SectorType.LAND;
			}
			image = imageLibrary.get(type);
		}
		if (image == null) {
			image = imageLibrary.getBlank();
		}
		return image;
	}


	public void displayActiveLocation() {
		if (selectedCoords.getCoords() != null) {
			mapDrawer.drawSquare(imageLibrary.getActiveBox(), selectedCoords.getCoords());
		}
	}


}
