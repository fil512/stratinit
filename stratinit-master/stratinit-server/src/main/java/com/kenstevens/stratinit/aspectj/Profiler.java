package com.kenstevens.stratinit.aspectj;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Profiler 
{
	
	private static final String PROFILE_FILENAME = "profile.txt";

	private static final Map<ProfilerID, TimerClass> timers = Collections.synchronizedMap(new HashMap<ProfilerID, TimerClass>());

	private static File proFile = new File(PROFILE_FILENAME);
	private static FileOutputStream outStream;
	private static PrintStream out;
	
	static {
		try {
			System.err.println("Opening "+PROFILE_FILENAME);
			outStream = new FileOutputStream(proFile);
			out = new PrintStream(outStream);
		} catch (IOException e) {
			System.err.println("Cannot open "+PROFILE_FILENAME+" for output.");
		}
	}
	
	public static ProfilerID start(int indent, String className, String methodName, Object[] args) {
		ProfilerID id = new ProfilerID(indent, className, methodName, args);
		TimerClass timer = new TimerClass();
		timer.start();
		timers.put(id, timer);
		return id;
	}

    public static void end(ProfilerID id) {
    	TimerClass timer = timers.remove(id);
        timer.stop();
        
        out.println(timer.getTotalTimeMillis()+"\t"+id);
    }
    
    public static class ProfilerID
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
