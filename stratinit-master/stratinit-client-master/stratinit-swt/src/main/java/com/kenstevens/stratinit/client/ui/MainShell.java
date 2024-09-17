package com.kenstevens.stratinit.client.ui;

import com.kenstevens.stratinit.client.api.IStatusReporter;
import com.kenstevens.stratinit.client.main.ClientConstants;
import com.kenstevens.stratinit.client.model.Account;
import com.kenstevens.stratinit.client.rest.RestClient;
import com.kenstevens.stratinit.client.shell.TopShell;
import com.kenstevens.stratinit.client.site.action.post.ActionFactory;
import com.kenstevens.stratinit.client.ui.image.ImageLibrary;
import com.kenstevens.stratinit.client.ui.tabs.ControllerManager;
import com.kenstevens.stratinit.client.util.AccountPersister;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("MainShell")
public class MainShell {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	MainWindow mainWindow;
	@Autowired
	Account account;
	@Autowired
	private AccountPersister accountPersister;
	@Autowired
	private ImageLibrary imageLibrary;
	@Autowired
	private TopShell topShell;
	@Autowired
	private ControllerManager controllerManager;
	@Autowired
	private IStatusReporter statusReporter;
	@Autowired
	private ActionFactory actionFactory;
    @Autowired
    private NewbHelper newbHelper;
    @Autowired
    private RestClient restClient;

	private Shell shell;

	public void start() {
		final Display display = Display.getDefault();
		shell = new Shell();

		try {
            loadFiles();

            restClient.setAccount();

            display.asyncExec(new Runnable() {
                public void run() {
                    initContents();
                    mainWindow.open(shell);
                    setShellSize();
                    shell.layout();
                    shell.open();
                    newbHelper.openNextWindow();
                }
			});
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		} finally {
			persistAccount();
			if (display.isDisposed()) {
				display.dispose();
			}
		}
	}

	private void setShellSize() {
		Rectangle rect = Display.getCurrent().getClientArea();
		if (account.getWidth() > 0) {
			shell.setSize(account.getWidth(), account.getHeight());
		} else {
			shell.setSize(shell.computeSize(rect.width
					- ClientConstants.HMARGIN, rect.height
					- ClientConstants.VMARGIN));
		}
	}

	private void persistAccount() {
		try {
			accountPersister.save();
		} catch (Exception e) {
			logger.error("Failed to save file: " + e.getMessage());
			logger.error(e.getMessage(), e);
		}
	}

	private void initContents() {
		setShellLocation();
		topShell.setShell(shell);
		shell.setLayout(new FormLayout());
		shell.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(final MouseEvent e) {
			}
		});
		shell.setSize(1072, 735);
		controllerManager.setTitle();
	}

	private void setShellLocation() {
		if (account.getX() > -1) {
			shell.setLocation(account.getX(), account.getY());
		} else {
			shell.setLocation(0, 0);
		}
	}

	private void loadFiles() {
		String loadResult;
		try {
			loadResult = accountPersister.load();
			imageLibrary.loadImages();
			statusReporter.reportResult(loadResult);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			loadResult = e.getMessage();
			statusReporter.reportError(loadResult);
			submitError(e);
		}
	}

	private void submitError(Exception e) {
		actionFactory.submitError(e);
	}

}
