package com.kenstevens.stratinit.server.remote.helper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public final class StackTraceHelper {
	private StackTraceHelper() {};
	
	public static String getStackTrace(Throwable aThrowable) {
	    final Writer result = new StringWriter();
	    final PrintWriter printWriter = new PrintWriter(result);
	    aThrowable.printStackTrace(printWriter);
	    return result.toString();
	  }

}
