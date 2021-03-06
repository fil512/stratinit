package com.kenstevens.stratinit.client.ui.image;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
public class ManualImageManagerTest {

	private static final String GIF = "red.gif";
	private static final String WRITE_FOLDER = "target/test/images";
	ImageManager imageManager = new ImageManager(WRITE_FOLDER);

	@BeforeEach
	public void createDir() {
		File imageDir = new File(WRITE_FOLDER);
		if (!imageDir.canRead()) {
			imageDir.mkdirs();
			assertTrue(imageDir.canRead());
		}
	}

	@Test
	public void canRead() {
		Image image = imageManager.loadImageFromFile(GIF);
		assertNotNull(image);
	}

	@Test
	public void write() {
		Image image = imageManager.loadImageFromFile(GIF);
		String filename = "test.gif";
		imageManager.writeImageToFile(image.getImageData(), filename );
		ImageData testData = new ImageData(WRITE_FOLDER+"/"+filename);
		assertImageEquals(image.getImageData(), testData);
	}

	private void assertImageEquals(ImageData imageData, ImageData testData) {
		assertEquals(imageData.width, testData.width);
		assertEquals(imageData.height, testData.height);
		for (int x = 0; x < imageData.width; x++) {
			for (int y = 0; y < imageData.height; y++) {
				assertEquals(imageData.getPixel(x,y), testData.getPixel(x, y));
			}
		}
	}

}
