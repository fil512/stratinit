package com.kenstevens.stratinit.client.site;

import com.kenstevens.stratinit.client.api.ICommandList;
import com.kenstevens.stratinit.client.api.IStatusReporter;
import com.kenstevens.stratinit.client.main.ClientConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Component("ActionQueue")
public class ActionQueue {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final BlockingQueue<Action> queue = new ArrayBlockingQueue<Action>(
            ClientConstants.ACTION_QUEUE_SIZE);
    @Autowired
    private CommandProcessor commandProcessor;
    @Autowired
    private IStatusReporter statusReporter;
    @Autowired
    private ICommandList commandList;

    private boolean shuttingDown = false;
    private Thread poll;

    private boolean paused;

    public synchronized void put(Action action) {
        if (!action.canRepeat() && queueContains(action.getClass())) {
            return;
        }
        commandList.addItem(action.getDescription());
        try {
            queue.put(action);
        } catch (InterruptedException e) {
            logger.error("Interrupted", e);
        }
    }

    private boolean queueContains(Class<? extends Action> clazz) {
        for (Action action : queue) {
            if (clazz.isInstance(action)) {
                return true;
            }
        }
        return false;
    }

    public void start() {
        logger.info("Starting ActionQueue");

        poll = new Thread() {
            @Override
            public void run() {
                pollForActions();
            }

        };
        poll.start();
    }

    private boolean queueContainsMoveFor(Integer unitId) {
        for (Action action : queue) {
            if (action instanceof UnitListAction) {
                UnitListAction unitListAction = (UnitListAction) action;
               return unitListAction.containsUnitId(unitId);
            }
        }
        return false;
    }

    private Action take() throws InterruptedException {
        Action action;
        action = queue.take();
        commandList.removeLast();
        return action;
    }

    private synchronized void halt() {
        queue.clear();
        if (commandList == null) {
            return;
        }
        commandList.removeAll();
    }

    public void shutdown() {
        poll.interrupt();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
    }

    public void stop() {
        halt();
    }

    private void pollForActions() {
        while (!shuttingDown) {
            if (paused) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    halt();
                    shuttingDown = true;
                }
                continue;
            }

            takeAndProcessAction();
        }
    }

    private void takeAndProcessAction() {
        Action action = null;
        try {
            action = take();
            commandProcessor.process(action);
        } catch (InterruptedException e) {
            halt();
            shuttingDown = true;
        } catch (Exception e) {
            statusReporter.reportError(e);
            logger.error(e.getMessage(), e);
            if (!(action instanceof ErrorSubmitter)) {
                ((ErrorSubmitter) action).submitError(e);
            }
        } finally {
            // FIXME find a better way to keep track if a unit is in the action queue
//            if (action instanceof UnitListAction) {
//                UnitListAction unitListAction = (UnitListAction) action;
//                List<UnitView> units = unitListAction.getUnits();
//                for (UnitView unit : units) {
//                    if (!queueContainsMoveFor(unit.getId())) {
//                        unit.setInActionQueue(false);
//                    }
//                }
//            }
        }
    }
}
