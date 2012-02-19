package com.kenstevens.stratinit.aspectj;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class Profiler
{	private final static Log logger = LogFactory.getLog(Profiler.class);

	private static final String PROFILE_FILENAME = "profile.txt";

	private static final Map<ProfilerID, TimerClass> TIMERS = Collections.synchronizedMap(new HashMap<ProfilerID, TimerClass>());

	private static File proFile = new File(PROFILE_FILENAME);
	private static FileOutputStream outStream;
	private static PrintStream out;

	private Profiler() {}

	static {
		try {
			logger.info("Opening "+PROFILE_FILENAME);
			outStream = new FileOutputStream(proFile);
			out = new PrintStream(outStream);
		} catch (IOException e) {
			logger.error("Cannot open "+PROFILE_FILENAME+" for output.");
		}
	}

	public static ProfilerID start(int indent, String className, String methodName, Object[] args) {
		ProfilerID id = new ProfilerID(indent, className, methodName, args);
		TimerClass timer = new TimerClass();
		timer.start();
		TIMERS.put(id, timer);
		return id;
	}

    public static void end(ProfilerID id) {
    	TimerClass timer = TIMERS.remove(id);
        timer.stop();

        out.println(timer.getTotalTimeMillis()+"\t"+id);
    }

    public static final class ProfilerID
	{
    	private final int indent;
		private final String className;
		private final String methodName;
		private final Object[] args;

		private ProfilerID(int indent, String className, String methodName, Object[] args) {
			this.indent = indent;
			this.className = className;
			this.methodName = methodName;
			this.args = args;
		}

		@Override
		public String toString() {
			String retval = "";
			for (int i = 0; i < indent; ++i) {
				retval += "  ";
			}
			retval += className + "." + methodName+"(";
			boolean first = true;
			for (Object arg : args) {
				if (!first) {
					retval += ", ";
				}
				if (arg == null) {
					retval += "null";
				} else {
					retval += arg.getClass().getSimpleName();
				}
				first = false;
			}
			retval += ")";
			return retval;
		}
	}
}
