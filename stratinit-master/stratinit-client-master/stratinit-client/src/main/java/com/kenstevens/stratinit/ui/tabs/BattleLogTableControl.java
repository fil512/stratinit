package com.kenstevens.stratinit.ui.tabs;

import javax.annotation.PostConstruct;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.control.Controller;
import com.kenstevens.stratinit.control.selection.SelectEvent;
import com.kenstevens.stratinit.control.selection.Selection;
import com.kenstevens.stratinit.event.BattleLogListArrivedEvent;
import com.kenstevens.stratinit.event.BattleLogListArrivedEventHandler;
import com.kenstevens.stratinit.model.BattleLogEntry;
import com.kenstevens.stratinit.model.BattleLogList;
import com.kenstevens.stratinit.model.Data;

@Scope("prototype")
@Component
public class BattleLogTableControl extends TableControl implements Controller {
	private final Table table;
	@Autowired
	private Data db;
	@Autowired
	private SelectEvent selectEvent;

	private final BattleTabItem battleTabItem;

	public BattleLogTableControl(BattleTabItem battleTabItem) {
		this.battleTabItem = battleTabItem;
		this.table = battleTabItem.getBattleLogTable();
		setTableListeners();
	}

	@SuppressWarnings("unused")
	@PostConstruct
	private void addObservers() {
		handlerManager.addHandler(BattleLogListArrivedEvent.TYPE,
				new BattleLogListArrivedEventHandler() {

					@Override
					public void dataArrived() {
						updateTable();
					}

				});
	}

	public final void setTableListeners() {
		table.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				TableItem item = (TableItem) event.item;
				if (item == null) {
					return;
				}
				BattleLogEntry battleLogEntry = (BattleLogEntry) item.getData();
				selectEvent.selectSectorCoords(battleLogEntry.getCoords(),
						Selection.Source.BATTLE_TAB);
				battleTabItem.getMessageText().setText(
						battleLogEntry.getMessage());
			}
		});
	}

	private void updateTable() {
		final Display display = Display.getDefault();

		if (display.isDisposed())
			return;
		display.asyncExec(new Runnable() {
			public void run() {
				updateTable(display);
			}
		});
	}

	public void setContents() {
		updateTable();
	}

	private void updateTable(final Display display) {
		if (table.isDisposed())
			return;
		table.removeAll();
		BattleLogList battleLogList = db.getBattleLogList();
		for (BattleLogEntry entry : battleLogList) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(new String[] { entry.getDateString(),
					entry.getOpponent(), entry.getOpponentUnit(),
					entry.getEvent(), entry.getMyUnit() });
			item.setData(entry);
			item.setForeground(display.getSystemColor(entry.getColor()));
		}
		table.redraw();
	}

}
