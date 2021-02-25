package com.kenstevens.stratinit.aspectj;

public class TimerClass {
    public long startTime;
    public long endTime;

    public void start() {
        this.startTime = System.currentTimeMillis();

    }

    public void stop() {
        this.endTime = System.currentTimeMillis();
    }

    public long getTotalTimeMillis() {
        return (endTime - startTime);
    }
}
