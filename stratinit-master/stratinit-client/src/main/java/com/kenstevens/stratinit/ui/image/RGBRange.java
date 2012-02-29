package com.kenstevens.stratinit.ui.image;

import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;

public class RGBRange {
	public RGB min = new RGB(255,255,255);
	public RGB max = new RGB(0,0,0);
	
	public RGBRange(int[] lineData, ImageData imageData, int size) {
		for (int y = 0; y < imageData.height; y++) {
			imageData.getPixels(0, y, size, lineData, 0);
			// Analyze each pixel value in the line
			for (int x = 0; x < lineData.length; x++) {
				// Extract the red, green and blue component
				int pixelValue = lineData[x];
				RGB rgb = imageData.palette.getRGB(pixelValue);
				if (rgb.red > 20 || rgb.green > 20 || rgb.blue > 20) {
					min.red = Math.min(rgb.red, min.red);
					max.red = Math.max(rgb.red, max.red);
					min.green = Math.min(rgb.green, min.green);
					max.green = Math.max(rgb.green, max.green);
					min.blue = Math.min(rgb.blue, min.blue);
					max.blue = Math.max(rgb.blue, max.blue);
				}
			}
		}
	}
	
	public RGB average() {
		return new RGB((max.red + min.red) / 2, (max.green + min.green) / 2, (max.blue + min.blue) / 2);
	}
}
