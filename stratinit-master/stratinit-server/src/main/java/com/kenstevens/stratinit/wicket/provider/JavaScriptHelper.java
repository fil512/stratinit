package com.kenstevens.stratinit.wicket.provider;

import org.apache.wicket.util.string.Strings;

import com.kenstevens.stratinit.type.UnitType;

public final class JavaScriptHelper {
	private JavaScriptHelper() {
	}

	public static String unitTypeAsJSString(UnitType unitType) {
		return quote(capitalize(unitType.toString()));
	}
	public static String capitalize(String string) {
		return Strings.capitalize(string.toLowerCase());
	}
	

	private static String quote(String string) {
		return "'" + string + "'";
	}

}
