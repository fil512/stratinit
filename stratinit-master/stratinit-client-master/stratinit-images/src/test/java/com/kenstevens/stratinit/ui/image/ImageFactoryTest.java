package com.kenstevens.stratinit.ui.image;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

// TODO CI find a way to get this to run on a headless box 
@Disabled
public class ImageFactoryTest {
	@Test
	public void createImages() throws FileNotFoundException {
		ImageFactory imageFactory = new ImageFactory();
		imageFactory.createImages();
	}
}
