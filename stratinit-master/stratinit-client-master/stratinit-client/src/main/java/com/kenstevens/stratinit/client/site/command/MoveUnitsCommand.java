package com.kenstevens.stratinit.client.site.command;

import com.kenstevens.stratinit.client.audio.WavPlayer;
import com.kenstevens.stratinit.client.model.Data;
import com.kenstevens.stratinit.client.model.UnitView;
import com.kenstevens.stratinit.client.model.WorldSector;
import com.kenstevens.stratinit.client.site.Command;
import com.kenstevens.stratinit.client.site.processor.UpdateProcessor;
import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.move.Attack;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.MoveUnitsJson;
import com.kenstevens.stratinit.type.SectorCoords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class MoveUnitsCommand extends Command<SIUpdate> {
	@Autowired
	private UpdateProcessor updateProcessor;
	@Autowired
	private WavPlayer wavPlayer;
	@Autowired
	private Data db;

	private final List<UnitView> units;
	private final SectorCoords targetCoords;

	public MoveUnitsCommand(List<UnitView> units, SectorCoords target) {
		this.units = units;
		this.targetCoords = target;
	}

	@Override
	public Result<SIUpdate> execute() {
		List<SIUnit> siunits = UnitsToSIUnits.transform(units);

		MoveUnitsJson request = new MoveUnitsJson(siunits, targetCoords);
		Result<SIUpdate> retval = stratInitServer.moveUnits(request);
		if (!retval.isMoveSuccess()) {
			wavPlayer.playEmpty();
		}
		return retval;
	}

	@Override
	public String getDescription() {
		String what = "units";
		if (units.size() == 1 && units.get(0).isNavy()) {
			what = units.get(0).getType().toString().toLowerCase();
		}
		WorldSector targetSector = db.getWorld().getWorldSector(targetCoords);
		Attack attack = new Attack(targetSector);
		if (targetSector == null) {
			return "Move " + what + " into the Great Unknown";
		} else if (attack.isEnemyCity()) {
			if (targetSector.getNation() == null) {
				return "Capture neutral city at " + targetCoords;
			} else {
				return "Attack " + targetSector.getNation() + "'s city at "
						+ targetCoords;
			}
		} else if (targetSector.isNeutralCity()) {
			return "Capture neutral city at " + targetCoords;
		} else if (attack.isAttackable()) {
			return "Attack " + targetSector.getTopUnitType() + " at "
					+ targetCoords;
		} else if (units.size() == 1 && units.get(0).isLaunchable()){
			return "Launch " + units.get(0).getType().toString().toLowerCase() + " towards " + targetSector.getDescription()
			+ " " + targetCoords;
		} else {
			return "Move " + what + " to " + targetSector.getDescription()
					+ " " + targetCoords;
		}
	}

	@Override
	public void handleSuccess(SIUpdate siupdate) {
		updateProcessor.process(siupdate, false);
	}
}
