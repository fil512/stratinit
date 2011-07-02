package com.kenstevens.stratinit.ui.image;

import java.io.FileNotFoundException;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class ImageFactoryTest {
	@Test
	public void createImages() throws FileNotFoundException {
		ImageFactory imageFactory = new ImageFactory();
		imageFactory.createImages();
	}
}
