package com.kenstevens.stratinit.site.action;

import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.NationView;
import com.kenstevens.stratinit.model.UnitView;
import com.kenstevens.stratinit.site.Action;
import com.kenstevens.stratinit.site.Command;
import com.kenstevens.stratinit.site.command.CedeCommand;
import com.kenstevens.stratinit.site.command.CedeUnitsCommand;
import com.kenstevens.stratinit.util.Spring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Scope("prototype")
@Component
public class CedeUnitsAction extends Action {
	@Autowired
	private Spring spring;
	
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
}