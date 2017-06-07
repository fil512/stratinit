package com.kenstevens.stratinit;

import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import jdepend.framework.JDepend;
import jdepend.framework.JavaPackage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

// FIXME This test breaks in Java 8
//	@RunWith(Parameterized.class)
public class JDependTest {

	private JavaPackage javaPackage = null;

	@SuppressWarnings("unchecked")
	@Parameterized.Parameters
	public static List<JavaPackage[]> data() throws IOException {
		JDepend jdepend = new JDepend();
		jdepend.addDirectory("target/classes");
		
		return arrayIze(jdepend.analyze());
	}

	private static List<JavaPackage[]> arrayIze(Collection<JavaPackage> packages) {
		return Lists.newArrayList(Collections2.transform(packages, new Function<JavaPackage, JavaPackage[]>() {
			@Override
			public JavaPackage[] apply(JavaPackage pkg) {
				return new JavaPackage[] { pkg };
			}
		}));
	}

	public JDependTest(JavaPackage javaPackage) {
		this.javaPackage = javaPackage;
	}

// FIXME This test breaks in Java 8
//	@Test
	public void cycleTest() {
		assertFalse(javaPackage.getName() + " failed", javaPackage.containsCycle());
	}
}
