package com.kenstevens.stratinit.ui.tabs;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gwt.event.shared.HandlerManager;
import com.kenstevens.stratinit.control.PlayerTableControl;
import com.kenstevens.stratinit.control.TopLevelController;
import com.kenstevens.stratinit.event.GameChangedEvent;
import com.kenstevens.stratinit.event.GameChangedEventHandler;
import com.kenstevens.stratinit.event.NationListArrivedEvent;
import com.kenstevens.stratinit.event.NationListArrivedEventHandler;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.model.GameView;
import com.kenstevens.stratinit.model.NationView;
import com.kenstevens.stratinit.model.SelectedNation;
import com.kenstevens.stratinit.site.action.ActionFactory;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.ui.selection.SelectNationEvent;
import com.kenstevens.stratinit.ui.selection.SelectNationEventHandler;
import com.kenstevens.stratinit.ui.selection.Selection.Source;
import com.kenstevens.stratinit.ui.shell.StatusReporter;
import com.kenstevens.stratinit.util.Spring;

@Scope("prototype")
@Component
public class PlayerTabItemControl implements TopLevelController {
	private final Log logger = LogFactory.getLog(getClass());

	private static final SimpleDateFormat FORMAT = new SimpleDateFormat(
			"h:mm a");

	@Autowired
	private ActionFactory actionFactory;
	@Autowired
	private Spring spring;
	@Autowired
	private HandlerManager handlerManager;
	@Autowired
	private StatusReporter statusReporter;
	@Autowired
	private SelectedNation selectedNation;
	@Autowired
	private Data db;

	private final PlayerTabItem playerTabItem;

	private PlayerTableControl playerTableControl;

	public PlayerTabItemControl(PlayerTabItem playerTabItem) {
		this.playerTabItem = playerTabItem;
		setButtonListeners();
	}

	@SuppressWarnings("unused")
	@PostConstruct
	private void addObservers() {
		handlerManager.addHandler(NationListArrivedEvent.TYPE,
				new NationListArrivedEventHandler() {
					@Override
					public void dataArrived() {
						setRelations();
					}
				});
		handlerManager.addHandler(SelectNationEvent.TYPE,
				new SelectNationEventHandler() {

					@Override
					public void selectNation(Source source) {
						NationView player = selectedNation.getPlayer();
						if (player != null) {
							setRelations();
						}
					}
				});
		handlerManager.addHandler(GameChangedEvent.TYPE, new GameChangedEventHandler() {

			@Override
			public void gameChanged() {
				GameView selectedGame = db.getSelectedGame();
				boolean noAlliances = !selectedGame.hasStarted() || selectedGame.isNoAlliances();
				Combo combo = playerTabItem.getMyRelationCombo();
				combo.removeAll();
				for (RelationType relationType : RelationType.values()) {
					if (relationType == RelationType.ALLIED && noAlliances) {
						continue;
					}
					combo.add(relationType.toString());
				}


			}
			
		});
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
							logger.error(e1.getMessage(), e1);						}
					}
				});

		final Combo relationCombo = playerTabItem.getMyRelationCombo();
		relationCombo.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				String choice = relationCombo.getText();
				NationView selectedPlayer = playerTableControl
						.getSelectedPlayer();
				if (selectedPlayer == null) {
					statusReporter.reportError("You must first select a player to change relations with before setting your relation.");
					return;
				}
				actionFactory.setRelation(selectedPlayer, RelationType.valueOf(choice));
			}
		});
	}

	public void setControllers() {
		playerTableControl = spring.autowire(new PlayerTableControl( playerTabItem.getPlayerTable(), playerTabItem.getTeamTable() ));
	}

	public void setContents() {
	}

	private void updatePlayerTab() {
		TableItem[] selection = playerTabItem.getPlayerTable().getSelection();
		if (selection.length == 0) {
			return;
		}
		NationView player = (NationView)selection[0].getData();
		if (playerTabItem.isDisposed())
			return;
		Combo combo = playerTabItem.getMyRelationCombo();
		combo.select(combo.indexOf(player.getMyRelation().getType()
				.toString()));
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
			nextText.setText(player.getMyRelation().getNextType()
					.toString());
		}
		Date theirSwitchTime = player.getTheirRelation().getSwitchTime();
		switchesText = playerTabItem.getTheirSwitchesText();
		nextText = playerTabItem.getTheirNextRelationText();
		if (theirSwitchTime == null) {
			switchesText.setText("");
			nextText.setText("");
		} else {
			switchesText.setText(FORMAT.format(theirSwitchTime));
			nextText.setText(player.getTheirRelation().getNextType()
					.toString());
		}
	}
}
