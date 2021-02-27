package com.kenstevens.stratinit.ui.tabs;

import com.kenstevens.stratinit.control.BattleLogDownloader;
import com.kenstevens.stratinit.control.TopLevelController;
import com.kenstevens.stratinit.site.action.post.ActionFactory;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.util.Spring;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.FileDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Scope("prototype")
@Component
public class BattleTabItemControl implements TopLevelController {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private ActionFactory actionFactory;
	@Autowired
	private Spring spring;
	@Autowired
	private BattleLogDownloader battleLogDownloader;

	private final BattleTabItem battleTabItem;

	private BattleLogTableControl battleLogTableControl;

	public BattleTabItemControl(BattleTabItem battleTabItem) {
		this.battleTabItem = battleTabItem;
		setButtonListeners();
	}

	private final void setButtonListeners() {
		battleTabItem.getUpdateButton().addSelectionListener(
				new SelectionAdapter() {

					@Override
					public void widgetSelected(final SelectionEvent e) {
						try {
							actionFactory.battleLog();
						} catch (Exception e1) {
							logger.error(e1.getMessage(), e1);
						}
					}
				});
		battleTabItem.getDownloadButton().addSelectionListener(
				new SelectionAdapter() {

					@Override
					public void widgetSelected(final SelectionEvent e) {
						try {
							downloadData();
						} catch (Exception e1) {
							logger.error(e1.getMessage(), e1);
						}
					}
				});
	}

	protected final void downloadData() {
		if (battleLogDownloader.noData()) {
			return;
		}
		FileDialog dlg = new FileDialog(battleTabItem.getShell(), SWT.SAVE);
		dlg.setFileName(Constants.DEFAULT_BATTLELOG_FILENAME);
		String filename = dlg.open();
		if (filename == null) {
			// cancelled
			return;
		}

		File file = new File(filename);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			bw.write(battleLogDownloader.getText());
			bw.close();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void setControllers() {
		battleLogTableControl = spring.autowire(new BattleLogTableControl(battleTabItem));
	}

	public void setContents() {
		battleLogTableControl.setContents();
	}
}
