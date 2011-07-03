package com.kenstevens.stratinit.site.action;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.NationView;
import com.kenstevens.stratinit.model.UnitView;
import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.ActionQueue;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.command.CedeCommand;
import com.kenstevens.stratinit.site.command.CedeUnitsCommand;
import com.kenstevens.stratinit.ui.selection.SelectEvent;
import com.kenstevens.stratinit.ui.selection.Selection.Source;
import com.kenstevens.stratinit.util.Spring;

@Scope("prototype")
@Component
public class CedeUnitsAction extends Action {
	@Autowired
	private Spring spring;
	@Autowired
	private ActionQueue actionQueue;
	@Autowired
	private SelectEvent selectEvent;
	
	private CedeCommand cedeCommand;

	private final List<UnitView> units;
	private final Nation nation;

	public CedeUnitsAction(List<UnitView> units, NationView nation) {
		this.units = units;
		this.nation = nation;
	}

	@SuppressWarnings("unused")
	@PostConstruct
	private void initialize() {
			cedeCommand = spring.autowire(new CedeUnitsCommand(
					units, nation ));
	}

	@Override
	public Command<? extends Object> getCommand() {
		return cedeCommand;
	}
	
	@Override
	public void postRequest() {
		super.postRequest();
		if (actionQueue.isEmpty()) {
			if (!units.isEmpty()) {
				selectEvent.selectSectorCoords(units.get(0).getCoords(), Source.CANVAS_SELECT);
			}
		}
	}

}