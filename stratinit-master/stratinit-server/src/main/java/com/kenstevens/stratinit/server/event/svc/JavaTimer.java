package com.kenstevens.stratinit.server.event.svc;

import com.kenstevens.stratinit.server.event.Event;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

@Service
public class JavaTimer {

    private final Timer timer = new Timer();

    public void cancel() {
        timer.cancel();
    }

    public void schedule(TimerTask task, Date time, long periodMillis) {
        timer.schedule(task, time, periodMillis);
    }

    public void schedule(TimerTask task, Date time) {
        timer.schedule(task, time);
    }

    public void cancel(Event event) {
        event.cancel();
    }
}
