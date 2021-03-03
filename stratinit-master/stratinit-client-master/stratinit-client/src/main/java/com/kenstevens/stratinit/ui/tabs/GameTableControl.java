package com.kenstevens.stratinit.ui.tabs;

import com.google.common.eventbus.Subscribe;
import com.kenstevens.stratinit.config.ServerConfig;
import com.kenstevens.stratinit.event.GameListArrivedEvent;
import com.kenstevens.stratinit.event.StratinitEventBus;
import com.kenstevens.stratinit.model.GameList;
import com.kenstevens.stratinit.model.GameView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Scope("prototype")
@Component
public class GameTableControl {
	@Autowired
	private StratinitEventBus eventBus;
	// FIXME pull this from rest call to server config
	@Autowired
	private ServerConfig serverConfig;

	private final GameTable gameTable;
	private final GameList gameList;

	public GameTableControl(GameTable gameTable, GameList gameList) {
		this.gameTable = gameTable;
		this.gameList = gameList;
	}

	@Subscribe
	public void handleGameListArrivedEvent(GameListArrivedEvent event) {
		updateTable();
	}

	@SuppressWarnings("unused")
	@PostConstruct
	private void addObservers() {
		eventBus.register(this);
	}

	private void updateTable() {
		Table table = gameTable.getTable();
		if (gameTable.isDisposed())
			return;
		if (gameList != null) {
			table.removeAll();
			for (GameView game : gameList) {
				TableItem item = new TableItem(table, SWT.NONE);
				item.setText(new String[]{"" + game.getId(), game.getGamename(),
						"" + game.getGamesize(), game.getPlayersString(serverConfig),
						game.getCreatedString(),
						game.getExpectedMapTimeString(serverConfig),
						game.getStartTimeString(), game.getEndsString()});
				item.setData(game);
			}
			table.redraw();
		}
	}

	public void setContents() {
		updateTable();
	}
}
