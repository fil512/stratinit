package com.kenstevens.stratinit.control;

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

import com.google.gwt.event.shared.HandlerManager;
import com.kenstevens.stratinit.dto.SITeam;
import com.kenstevens.stratinit.event.NationListArrivedEvent;
import com.kenstevens.stratinit.event.NationListArrivedEventHandler;
import com.kenstevens.stratinit.event.TeamListArrivedEvent;
import com.kenstevens.stratinit.event.TeamListArrivedEventHandler;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.model.NationList;
import com.kenstevens.stratinit.model.NationView;
import com.kenstevens.stratinit.model.TeamList;
import com.kenstevens.stratinit.ui.selection.SelectEvent;
import com.kenstevens.stratinit.ui.selection.Selection.Source;

@Scope("prototype")
@Component
public class PlayerTableControl {
	private final Table playerTable;
	private final Table teamTable;
	@Autowired
	private Data db;
	@Autowired
	private SelectEvent selectEvent;
	@Autowired
	private HandlerManager handlerManager;

	public PlayerTableControl(Table playerTable, Table teamTable) {
		this.playerTable = playerTable;
		this.teamTable = teamTable;
		setTableListeners();
	}

	@SuppressWarnings("unused")
	@PostConstruct
	private void addObservers() {
		handlerManager.addHandler(NationListArrivedEvent.TYPE,
				new NationListArrivedEventHandler() {
					@Override
					public void dataArrived() {
						rebuildPlayerTable(db.getNationList());
					}
				});
		handlerManager.addHandler(TeamListArrivedEvent.TYPE,
				new TeamListArrivedEventHandler() {
					@Override
					public void dataArrived() {
						rebuildTeamTable(db.getTeamList());
					}
				});
	}

	public final void setTableListeners() {

		playerTable.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				TableItem[] items = playerTable.getSelection();
				if (items == null || items.length != 1) {
					return;
				}
				NationView player = (NationView) items[0].getData();
				selectEvent.selectNation(player, Source.PLAYER_TAB);
			}
		});
	}

	private void addPlayers(final NationList nationList) {
		final Display display = Display.getDefault();

		for (NationView nation : nationList) {
			TableItem item = new TableItem(playerTable, SWT.NONE);
			int perc = 0;
			if (nation.getPlayed() > 0) {
				perc = 100 * nation.getWins() / nation.getPlayed();
			}
			item.setText(new String[] { nation.isLoggedIn() ? "*" : "",
					nation.getMyRelationString(), nation.getTheirRelationString(), nation.getName(),
					"" + nation.getCities(), "" + nation.getPower(),
					"" + nation.getPlayed(), "" + perc });
			item.setData(nation);
			if (nation.isLoggedIn()) {
				item.setForeground(display.getSystemColor(SWT.COLOR_BLUE));
			}
		}
	}

	private void addTeams(final TeamList teamList) {
		for (SITeam team : teamList) {
			TableItem item = new TableItem(teamTable, SWT.NONE);
			item.setText(new String[] { team.nation1, team.nation2, ""+team.score
					 });
		}
	}

	public NationView getSelectedPlayer() {
		TableItem[] tableItems = playerTable.getSelection();
		if (tableItems == null || tableItems.length == 0) {
			return null;
		}
		return (NationView) tableItems[0].getData();
	}

	private void rebuildPlayerTable(final NationList nationList) {
		if (playerTable.isDisposed())
			return;
		int index = playerTable.getSelectionIndex();
		playerTable.removeAll();
		addPlayers(nationList);
		playerTable.setSelection(index);
		playerTable.redraw();
	}

	private void rebuildTeamTable(final TeamList teamList) {
		if (teamTable.isDisposed())
			return;
		teamTable.removeAll();
		addTeams(teamList);
		teamTable.redraw();
	}

}
