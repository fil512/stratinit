package com.kenstevens.stratinit.client.ui.image;

import com.kenstevens.stratinit.type.CityType;
import com.kenstevens.stratinit.type.UnitType;
import org.eclipse.swt.graphics.Image;

import java.io.FileNotFoundException;


public class ImageFactory {
	private static final String WRITE_FOLDER = "src/main/resources/com/kenstevens/stratinit/ui/image/images";
	ImageManager imageManager = new ImageManager(WRITE_FOLDER);

	public void createImages() throws FileNotFoundException {
		createCityImages();
		createUnitImages();
	}

	private void createUnitImages() throws FileNotFoundException {
		for (UnitType unitType : UnitType.values()) {
			String filename = unitType.toString().toLowerCase() + ".gif";
			buildColouredImages("unit", filename);
		}
	}

	private void createCityImages() throws FileNotFoundException {
		for (CityType cityType : CityType.values()) {
			String filename = cityType.toString().toLowerCase() + ".gif";
			buildColouredImages("city", filename);
		}
	}

	private void buildColouredImages(String folder, String filename) throws FileNotFoundException {
		Image image = imageManager.loadImageFromFile(folder+"/" + filename);
		Image transparentImage = ImageUtils.getTransparentImage(image);

		ImageColourer imageColourer = new ImageColourer(imageManager, transparentImage, folder, filename);
		imageColourer.writeImage("red", RGBColour.RED);
		imageColourer.writeImage("yellow", RGBColour.YELLOW);
		imageColourer.writeImage("gold", RGBColour.GOLD);
		imageColourer.writeImage("aqua", RGBColour.AQUA);
		imageColourer.writeImage("purple", RGBColour.PURPLE);
		imageColourer.writeTransparentImage("trans");
	}
}
