package com.kenstevens.stratinit.ui.image;

import java.io.FileNotFoundException;

import org.junit.Ignore;
import org.junit.Test;

// TODO CI find a way to get this to run on a headless box 
@Ignore
public class ImageFactoryTest {
	@Test
	public void createImages() throws FileNotFoundException {
		ImageFactory imageFactory = new ImageFactory();
		imageFactory.createImages();
	}
}
