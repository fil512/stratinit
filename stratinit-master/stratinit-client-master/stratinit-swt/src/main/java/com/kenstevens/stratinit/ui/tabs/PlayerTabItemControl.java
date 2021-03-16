package com.kenstevens.stratinit.ui.tabs;

import com.google.common.eventbus.Subscribe;
import com.kenstevens.stratinit.client.control.PlayerTableControl;
import com.kenstevens.stratinit.client.control.TopLevelController;
import com.kenstevens.stratinit.client.control.selection.SelectNationEvent;
import com.kenstevens.stratinit.client.event.GameChangedEvent;
import com.kenstevens.stratinit.client.event.NationListArrivedEvent;
import com.kenstevens.stratinit.client.event.StratinitEventBus;
import com.kenstevens.stratinit.client.model.Data;
import com.kenstevens.stratinit.client.model.GameView;
import com.kenstevens.stratinit.client.model.NationView;
import com.kenstevens.stratinit.client.model.SelectedNation;
import com.kenstevens.stratinit.client.site.action.post.ActionFactory;
import com.kenstevens.stratinit.client.util.Spring;
import com.kenstevens.stratinit.shell.StatusReporter;
import com.kenstevens.stratinit.type.RelationType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Date;

@Scope("prototype")
@Component
public class PlayerTabItemControl implements TopLevelController {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final SimpleDateFormat FORMAT = new SimpleDateFormat(
			"h:mm a");

	@Autowired
	private ActionFactory actionFactory;
	@Autowired
	private Spring spring;
	@Autowired
	private StatusReporter statusReporter;
	@Autowired
	private SelectedNation selectedNation;
	@Autowired
	private Data db;
	@Autowired
	private StratinitEventBus eventBus;

	private final PlayerTabItem playerTabItem;

	private PlayerTableControl playerTableControl;

	public PlayerTabItemControl(PlayerTabItem playerTabItem) {
		this.playerTabItem = playerTabItem;
		setButtonListeners();
	}

	@Subscribe
	public void handleNationListArrivedEvent(NationListArrivedEvent event) {
		setRelations();
	}
	
	@Subscribe
	public void handleSelectNationEvent(SelectNationEvent event) {
		NationView player = selectedNation.getPlayer();
		if (player != null) {
			setRelations();
		}
	}
	
	@SuppressWarnings("unused")
	@PostConstruct
	private void addObservers() {
		eventBus.register(this);
	}

	@Subscribe
	public void handleGameChangedEvent(GameChangedEvent event) {
		GameView selectedGame = db.getSelectedGame();
		boolean noAlliances = !selectedGame.hasStarted()
				|| selectedGame.isNoAlliances();
		Combo combo = playerTabItem.getMyRelationCombo();
		combo.removeAll();
		for (RelationType relationType : RelationType.values()) {
			if (relationType == RelationType.ALLIED && noAlliances) {
				continue;
			}
			combo.add(relationType.toString());
		}
	}

	private void setRelations() {
		final Display display = Display.getDefault();
		display.asyncExec(new Runnable() {
			public void run() {
				updatePlayerTab();
			}
		});
	}

	private void setButtonListeners() {
		playerTabItem.getRefreshButton().addSelectionListener(
				new SelectionAdapter() {

					@Override
					public void widgetSelected(final SelectionEvent e) {
						try {
							actionFactory.getNations();
						} catch (Exception e1) {
							logger.error(e1.getMessage(), e1);
						}
					}
				});

		final Combo relationCombo = playerTabItem.getMyRelationCombo();
		relationCombo.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				String choice = relationCombo.getText();
				NationView selectedPlayer = playerTableControl
						.getSelectedPlayer();
				if (selectedPlayer == null) {
					statusReporter
							.reportError("You must first select a player to change relations with before setting your relation.");
					return;
				}
				actionFactory.setRelation(selectedPlayer,
						RelationType.valueOf(choice));
			}
		});
	}

	public void setControllers() {
		playerTableControl = spring.autowire(new PlayerTableControl(
				playerTabItem.getPlayerTable(), playerTabItem.getTeamTable()));
	}

	public void setContents() {
	}

	private void updatePlayerTab() {
		TableItem[] selection = playerTabItem.getPlayerTable().getSelection();
		if (selection.length == 0) {
			return;
		}
		NationView player = (NationView) selection[0].getData();
		if (playerTabItem.isDisposed())
			return;
		Combo combo = playerTabItem.getMyRelationCombo();
		combo.select(combo.indexOf(player.getMyRelation().getType().toString()));
		Text theirRelationText = playerTabItem.getTheirRelationText();
		theirRelationText.setText(player.getTheirRelation().getType()
				.toString());
		Date switchTime = player.getMyRelation().getSwitchTime();
		Text switchesText = playerTabItem.getSwitchesText();
		Text nextText = playerTabItem.getNextRelation();
		if (switchTime == null) {
			switchesText.setText("");
			nextText.setText("");
		} else {
			switchesText.setText(FORMAT.format(switchTime));
			nextText.setText(player.getMyRelation().getNextType().toString());
		}
		Date theirSwitchTime = player.getTheirRelation().getSwitchTime();
		switchesText = playerTabItem.getTheirSwitchesText();
		nextText = playerTabItem.getTheirNextRelationText();
		if (theirSwitchTime == null) {
			switchesText.setText("");
			nextText.setText("");
		} else {
			switchesText.setText(FORMAT.format(theirSwitchTime));
			nextText.setText(player.getTheirRelation().getNextType().toString());
		}
	}
}
