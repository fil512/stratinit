package com.kenstevens.stratinit.ui.image;

import com.kenstevens.stratinit.main.ClientConstants;
import com.kenstevens.stratinit.type.CityType;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.type.SectorType;
import com.kenstevens.stratinit.type.UnitType;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

@Component
public class ImageLibrary {
	public static final int MAX_ALPHA = 255;
	public static final int GRADIENT = 16;
	public static final int MAX_ALPHA_IMAGE_DATA = MAX_ALPHA / GRADIENT;
	public static int maxWhiteImages = 10;

	private final ImageManager imageManager = new ImageManager();
	private final Map<SectorType, Image> sectorMap = new Hashtable<SectorType, Image>();
	private final Map<CityType, Map<RelationType, Image>> cityMap = new HashMap<CityType, Map<RelationType, Image>>();
	private final Map<CityType, Image> cityDestroyed = new HashMap<CityType, Image>();
	private final Map<UnitType, Map<RelationType, Image>> unitMap = new HashMap<UnitType, Map<RelationType, Image>>();
	private final Map<UnitType, Image> unitDestroyed = new HashMap<UnitType, Image>();
	private boolean initialized = false;
	private Image activeBox;
	private Image[] whites;
	private Image[] blacks;
	private Image[] yellows;
	private Image[] reds;
	private Image red1;
	private Image yellow1;
	private Image redLine;
	private Image yellowLine;
	private Image smallCity;
	private Image smallLand;
	private Image smallAir;
	private Image smallNavy;
	private Image smallBlack;
	private Image smallGreen;
	private Image smallBlue;
	private Image blank;
	private Image seen2;
	private Image blankCity;
	private Image flag;
	private Image centreUnit;
	private Image centreHome;
	private Image cede;
	private Image disband;
	private Image buildCity;
	private Image dig;
	private Image fill;
	private Image cancelMove;
	private Image update;
	private Image refresh;

	public synchronized void loadImages() throws FileNotFoundException {
		if (initialized) {
			return;
		}
		loadSectorImages();
		loadCityImages();
		loadUnitImages();
		loadStratInitImages();

		initialized = true;
	}

	private void loadUnitImages() throws FileNotFoundException {
		for (UnitType unitType : UnitType.values()) {
			loadBuildImage(unitType);
		}
	}

	private void loadBuildImage(UnitType unitType) throws
			FileNotFoundException {
		String filename = unitType.toString().toLowerCase() + ".gif";
		Map<RelationType, Image>map = new HashMap<RelationType, Image>();
		unitMap.put(unitType, map);
		fillRelationMap(map, "unit", filename);
		unitDestroyed.put(unitType, getImage("unit", "purple", filename));
	}

	private Image loadImageFromFile(String filename) {
		return imageManager.loadImageFromFile(filename);
	}

	private void fillRelationMap(Map<RelationType, Image> map,
			String subfolder, String filename) {

		map.put(RelationType.WAR, getImage(subfolder, "red", filename));
		map.put(RelationType.NEUTRAL, getImage(subfolder, "trans", filename));
		map.put(RelationType.FRIENDLY, getImage(subfolder, "yellow", filename));
		map.put(RelationType.ALLIED, getImage(subfolder, "gold", filename));
		map.put(RelationType.ME, getImage(subfolder, "aqua", filename));
	}

	private Image getImage(String subfolder, String colour, String filename) {
		return loadImageFromFile(subfolder+"/"+colour+"/"+filename);
	}

	public Image getUnitImage(RelationType relationType, UnitType unitType) {
		if (!initialized) {
			throw new IllegalStateException(
					"ImageLibrary.get() called before images loaded.");
		}
		return unitMap.get(unitType).get(defaultRelation(relationType));
	}

	private RelationType defaultRelation(RelationType relationType) {
		RelationType relationTypeToGet;
		if (relationType == null) {
			relationTypeToGet = RelationType.NEUTRAL;
		} else {
			relationTypeToGet = relationType;
		}
		return relationTypeToGet;
	}

	public Image getCityImage(RelationType relationType, CityType cityType) {
		if (!initialized) {
			throw new IllegalStateException(
					"ImageLibrary.get() called before images loaded.");
		}
		return cityMap.get(cityType).get(defaultRelation(relationType));
	}

	public Image getDestroyed(UnitType type) {
		if (!initialized) {
			throw new IllegalStateException(
					"ImageLibrary.get() called before images loaded.");
		}
		return unitDestroyed.get(type);
	}

	public Image getDestroyed(CityType type) {
		if (!initialized) {
			throw new IllegalStateException(
					"ImageLibrary.get() called before images loaded.");
		}
		return cityDestroyed.get(type);
	}

	private void loadSectorImages() {
		for (SectorType sectorType : SectorType.values()) {
			if (sectorType == SectorType.PLAYER_CITY) {
				continue;
			}
			String filename = sectorType.toString().toLowerCase() + ".gif";
			Image image = loadImageFromFile("sector/" + filename);
			sectorMap.put(sectorType, image);
		}
	}

	private void loadCityImages() throws FileNotFoundException {
		for (CityType cityType : CityType.values()) {
			String filename = cityType.toString().toLowerCase() + ".gif";
			Map<RelationType, Image>map = new HashMap<RelationType, Image>();
			cityMap.put(cityType, map);
			fillRelationMap(map, "city", filename);
			cityDestroyed.put(cityType, getImage("city", "purple", filename));
		}
	}

	public Image get(SectorType type) {
		if (!initialized) {
			throw new IllegalStateException(
					"ImageLibrary.get() called before images loaded.");
		}
		return sectorMap.get(type);
	}

	private void loadStratInitImages() {
		String path = ClientConstants.IMAGES_SUBFOLDER_NAME;
		Display display = Display.getDefault();

		activeBox = new Image(display, ImageLibrary.class
				.getResourceAsStream(path + "/activebox.gif"));

		whites = setColourImage("images/white.gif");
		blacks = setColourImage("images/black.gif");
		yellows = setColourImage("images/yellow.gif");
		reds = setColourImage("images/red.gif");

		red1 = loadImageFromFile("red1.gif");
		yellow1 = loadImageFromFile("yellow1.gif");
		redLine = loadImageFromFile("red_line.gif");
		yellowLine = loadImageFromFile("yellow_line.gif");
		blank = loadImageFromFile("blank.gif");
		seen2 = loadImageFromFile("seen_2.gif");
		flag = loadImageFromFile("flag.gif");
		blankCity = loadImageFromFile("city/blank.gif");
		Image smCity = loadImageFromFile("small/city.gif");
		smallCity = whiteTransparent(smCity);
		smCity.dispose();
		Image smLand = loadImageFromFile("small/land.gif");
		smallLand = whiteTransparent(smLand);
		smLand.dispose();
		Image smNavy = loadImageFromFile("small/navy.gif");
		smallNavy = whiteTransparent(smNavy);
		smNavy.dispose();
		Image smAir = loadImageFromFile("small/air.gif");
		smallAir = whiteTransparent(smAir);
		smAir.dispose();
		smallBlack = loadImageFromFile("small/black.gif");

		smallBlue = ImageUtils.createSmallSquare(RGBColour.BLUE,
				ClientConstants.IMG_PIXELS_SMALL);
		smallGreen = ImageUtils.createSmallSquare(RGBColour.GREEN,
				ClientConstants.IMG_PIXELS_SMALL);
		centreUnit = loadImageFromFile("button/centre_unit.gif");
		centreHome = loadImageFromFile("button/centre_home.gif");
		cede = loadImageFromFile("button/cede.gif");
		disband = loadImageFromFile("button/disband.gif");
		buildCity = loadImageFromFile("button/build_city.gif");
		dig = loadImageFromFile("button/dig.gif");
		fill = loadImageFromFile("button/fill.gif");
		cancelMove = loadImageFromFile("button/cancel_move.gif");
		update = loadImageFromFile("button/update.gif");
		refresh = loadImageFromFile("button/refresh.gif");
	}

	private Image whiteTransparent(Image image) {
		ImageData imageData = image.getImageData();
		int whitePixel = imageData.palette.getPixel(RGBColour.WHITE);
		imageData.transparentPixel = whitePixel;
		return new Image(Display.getDefault(), imageData);
	}

	private Image[] setColourImage(String filename) {
		Image[] images = new Image[MAX_ALPHA_IMAGE_DATA];
		for (int i = 0; i < MAX_ALPHA_IMAGE_DATA; i += 1) {
			ImageData imageData = new ImageData(ImageLibrary.class
					.getResourceAsStream(filename));
			imageData.alpha = GRADIENT * i;
			images[i] = new Image(Display.getDefault(), imageData);
		}
		return images;
	}

	public Image getActiveBox() {
		return activeBox;
	}

	public Image getShadedImage(boolean canLand, boolean enoughMoves, boolean inSupply) {
		if (!inSupply) {
			return reds[3];
		}
		if (!canLand) {
			if (enoughMoves) {
				return blacks[3];
			} else {
				return blacks[6];
			}
		} else if (enoughMoves) {
			return whites[6];
		} else {
			return yellows[3];
		}
	}

	public Image getDamaged() {
		return red1;
	}

	public Image getWounded() {
		return yellow1;
	}

	public Image getOutOfSupply() {
		return redLine;
	}

	public Image getLowAmmo() {
		return yellowLine;
	}

	public Image getSmallBlack() {
		return smallBlack;
	}

	public Image getSmallBlue() {
		return smallBlue;
	}

	public Image getSmallGreen() {
		return smallGreen;
	}

	public Image getSmallCity() {
		return smallCity;
	}

	public Image getSmallLand() {
		return smallLand;
	}

	public Image getSmallAir() {
		return smallAir;
	}

	public Image getSmallNavy() {
		return smallNavy;
	}

	public Image getBlank() {
		return blank;
	}

	public Image getFOW() {
		return seen2;
	}

	public Image getFlag() {
		return flag;
	}

	public Image getBlankCity() {
		return blankCity;
	}

	public Image getCentreUnit() {
		return centreUnit;
	}

	public Image getCentreHome() {
		return centreHome;
	}

	public Image getCede() {
		return cede;
	}
	
	public Image getDisband() {
		return disband;
	}

	public Image getBuildCity() {
		return buildCity;
	}

	public Image getDig() {
		return dig;
	}

	public Image getFill() {
		return fill;
	}
	
	public Image getCancelMove() {
		return cancelMove;
	}
	public Image getUpdate() {
		return update;
	}
	public Image getRefresh() {
		return refresh;
	}

	public Image getSupplyImage() {
		return whites[2];
	}

}
