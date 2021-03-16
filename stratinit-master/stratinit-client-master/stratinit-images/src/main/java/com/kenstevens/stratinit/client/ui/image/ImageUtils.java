package com.kenstevens.stratinit.client.ui.image;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import java.io.FileNotFoundException;

public final class ImageUtils {
	private ImageUtils() {}
	protected static Image colourifyImage(Image image, RGB colour) {
		Image imageCopy = new Image(Display.getCurrent(), image, SWT.IMAGE_COPY);
		ImageData imageData = imageCopy.getImageData();
		colourifyImage(imageData, colour);
		Image retval = new Image(Display.getDefault(), imageData);
		imageCopy.dispose();
		return retval;
	}

	// Assumes greyscale image
	private static void colourifyImage(ImageData imageData, RGB colour) {
		PaletteData palette = imageData.palette;
		for (RGB rgb : palette.colors) {
			int saturation = rgb.red;
			// Keep white (transparent) and black as is
			if (saturation == 0x00 || saturation == 0xFF) {
				continue;
			}
			rgb.red = colour.red * saturation / 0xFF;
			rgb.green = colour.green * saturation / 0xFF;
			rgb.blue = colour.blue * saturation / 0xFF;
		}
	}

	protected static Image getTransparentImage(Image image)
			throws FileNotFoundException {
		Image imageCopy = new Image(Display.getCurrent(), image, SWT.IMAGE_COPY);
		ImageData imageData = imageCopy.getImageData();
		int whitePixel = imageData.palette.getPixel(new RGB(255, 255, 255));
		imageData.transparentPixel = whitePixel;
		Image retval = new Image(Display.getDefault(), imageData);
		imageCopy.dispose();
		return retval;
	}

	public static Image createSmallSquare(RGB rgb, int size) {
		PaletteData paletteData = new PaletteData(new RGB[]{new RGB(0, 0, 0), rgb});
		ImageData imageData = new ImageData(size, size, 1, paletteData);
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				imageData.setPixel(x, y, 1);
			}
		}
		return new Image(Display.getDefault(), imageData);
	}

	public static Image blackToColour(RGB rgb, Image image, int size) {
		// Note this assumes a white and black palette
		ImageData imageData = image.getImageData();
		return convertBlackToColour(rgb, imageData, size);
	}

	protected static Image convertBlackToColour(RGB rgb, ImageData sourceImageData, int size) {
		RGB white = new RGB(255,255,255);
		PaletteData paletteData = new PaletteData(new RGB[] { white, rgb });
		ImageData imageData = new ImageData(size, size, 1, paletteData);
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				if (sourceImageData.getPixel(x, y) == 0) {
					imageData.setPixel(x, y, 1);
				}
			}
		}
		int whitePixel = imageData.palette.getPixel(white);
		imageData.transparentPixel = whitePixel;
		return new Image(Display.getDefault(), imageData);
	}
}
