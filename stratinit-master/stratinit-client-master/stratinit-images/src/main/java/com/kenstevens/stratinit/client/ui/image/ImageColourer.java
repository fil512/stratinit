package com.kenstevens.stratinit.client.ui.image;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;

public class ImageColourer {

	private final Image transparentImage;
	private final String folder;
	private final String filename;
	private final ImageManager imageManager;

	protected ImageColourer(ImageManager imageManager, Image transparentImage, String folder, String filename) {
		this.imageManager = imageManager;
		this.transparentImage = transparentImage;
		this.folder = folder;
		this.filename = filename;
	}

	protected void writeTransparentImage(String subfolder) {
		imageManager.writeImage(folder+"/"+subfolder, filename, transparentImage);
	}

	protected void writeImage(String subfolder, RGB colour) {
		Image redImage = ImageUtils.colourifyImage(transparentImage, colour);
		imageManager.writeImage(folder+"/"+subfolder, filename, redImage);
	}

}
