package com.kenstevens.stratinit.aspectj;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Profiler {
    private final static Logger logger = LoggerFactory.getLogger(Profiler.class);

    private static final String PROFILE_FILENAME = "profile.txt";

    private static final Map<ProfilerID, TimerClass> TIMERS = new ConcurrentHashMap<>();

    private static final File proFile = new File(PROFILE_FILENAME);
    private static FileOutputStream outStream;
    private static PrintStream out;

    static {
        try {
            logger.info("Opening " + PROFILE_FILENAME);
            outStream = new FileOutputStream(proFile);
            out = new PrintStream(outStream);
        } catch (IOException e) {
            logger.error("Cannot open " + PROFILE_FILENAME + " for output.");
        }
    }

    private Profiler() {
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

        out.println(timer.getTotalTimeMillis() + "\t" + id);
    }

    public static final class ProfilerID {
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
            retval += className + "." + methodName + "(";
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
