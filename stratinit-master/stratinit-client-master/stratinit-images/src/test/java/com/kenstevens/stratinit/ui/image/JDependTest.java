package com.kenstevens.stratinit.ui.image;

import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.util.Collection;

import jdepend.framework.JDepend;
import jdepend.framework.JavaPackage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.google.common.collect.Lists;

@RunWith(Parameterized.class)
public class JDependTest {

	private static JDepend jdepend = null;
	private static Collection<JavaPackage> packages = null;
	private JavaPackage pack1 = null;

	@SuppressWarnings("unchecked")
	@Parameterized.Parameters
	public static Collection<JavaPackage[]> data() throws IOException {
		Collection<JavaPackage[]> result = Lists.newArrayList();
		jdepend = new JDepend();
		jdepend.addDirectory("target/classes");
		packages = jdepend.analyze();
		for (JavaPackage p : packages) {
			result.add(new JavaPackage[] { p });
		}
		return result;
	}

	public JDependTest(JavaPackage pack) {
		pack1 = pack;
	}

	@Test
	public void cycleTest() {
		assertFalse(pack1.getName() + " failed", pack1.containsCycle());
	}
}
