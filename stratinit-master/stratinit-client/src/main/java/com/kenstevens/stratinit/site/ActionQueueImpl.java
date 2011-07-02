package com.kenstevens.stratinit.site;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.main.ClientConstants;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitView;
import com.kenstevens.stratinit.site.action.ActionFactory;
import com.kenstevens.stratinit.site.action.SubmitErrorAction;
import com.kenstevens.stratinit.ui.shell.StatusReporter;
import com.kenstevens.stratinit.ui.shell.WidgetContainer;

@Component("ActionQueue")
public class ActionQueueImpl implements ActionQueue {
	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private CommandProcessor commandProcessor;
	@Autowired
	private StatusReporter statusReporter;
	@Autowired
	private WidgetContainer widgetContainer;
	@Autowired
	private ActionFactory actionFactory;

	private boolean shuttingDown = false;

	private BlockingQueue<Action> queue = new ArrayBlockingQueue<Action>(
			ClientConstants.ACTION_QUEUE_SIZE);
	private Thread poll;

	private boolean paused;

	public synchronized void put(Action action) throws InterruptedException {
		if (!action.canRepeat() && queueContains(action.getClass())) {
			return;
		}
		widgetContainer.getCommandListControl().addItem(action.getDescription());
		queue.put(action);
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
		poll = new Thread() {
			@Override
			public void run() {
				pollForActions();
			}

		};
		poll.start();
	}

	private boolean queueContainsMoveFor(Unit unit) {
		for (Action action : queue) {
			if (action instanceof UnitAwareAction) {
				UnitAwareAction unitAwareAction = (UnitAwareAction) action;
				List<UnitView> units = unitAwareAction.getUnits();
				for (Unit moved : units) {
					if (unit.getId() == moved.getId()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private Action take() throws InterruptedException {
		Action action;
		action = queue.take();
		widgetContainer.getCommandListControl().removeLast();
		return action;
	}

	private synchronized void halt() {
		queue.clear();
		if (widgetContainer == null || widgetContainer.getCommandListControl() == null) {
			return;
		}
		widgetContainer.getCommandListControl().removeAll();
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
			if (!(action instanceof SubmitErrorAction)) {
				actionFactory.submitError(e);
			}
		} finally {
			if (action instanceof UnitAwareAction) {
				UnitAwareAction unitAwareAction = (UnitAwareAction) action;
				List<UnitView> units = unitAwareAction.getUnits();
				for (UnitView unit : units) {
					if (!queueContainsMoveFor(unit)) {
						unit.setInActionQueue(false);
					}
				}
			}
		}
	}
}
