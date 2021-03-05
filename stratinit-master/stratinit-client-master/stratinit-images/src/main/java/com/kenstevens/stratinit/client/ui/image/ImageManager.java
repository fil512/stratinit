package com.kenstevens.stratinit.client.ui.image;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;

import java.io.File;
import java.io.InputStream;

public class ImageManager {
	private final String writeFolder;
	public ImageManager() {
		this.writeFolder = null;
	}

	public ImageManager(String writeFolder) {
		this.writeFolder = writeFolder;
	}

	public Image loadImageFromFile(String filename) {
		Image seen;
		InputStream imageResource = ImageManager.class
				.getResourceAsStream("images/" + filename);
		if (imageResource == null) {
			throw new IllegalArgumentException("Unable to load " + filename);
		}
		ImageData seen1Data = new ImageData(imageResource);
		seen = new Image(Display.getDefault(), seen1Data);
		return seen;
	}

	protected void writeImageToFile(ImageData imageData, String filename) {
		if (writeFolder == null) {
			throw new IllegalStateException("ImageManager must have a writeFolder specificed before writing");
		}
		ImageLoader imageLoader = new ImageLoader();
		imageLoader.data = new ImageData[] {imageData};
		imageLoader.save(writeFolder+"/"+filename, SWT.IMAGE_GIF);
	}


	public void writeImage(String folder, String filename, Image image) {
		String subfolder = writeFolder+"/"+folder;
		File imageDir = new File(subfolder);
		if (!imageDir.canRead()) {
			imageDir.mkdir();
			if (!imageDir.canRead()) {
				throw new IllegalStateException("Unable to create subdirectory ["+subfolder+"].  Parent folder must not exist.");
			}
		}
		writeImageToFile(image.getImageData(), folder+"/"+filename);
	}
}
