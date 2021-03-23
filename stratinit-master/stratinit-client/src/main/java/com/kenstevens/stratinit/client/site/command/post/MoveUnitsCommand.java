package com.kenstevens.stratinit.client.site.command.post;

import com.kenstevens.stratinit.client.api.IAudioPlayer;
import com.kenstevens.stratinit.client.model.UnitView;
import com.kenstevens.stratinit.client.model.WorldSector;
import com.kenstevens.stratinit.client.site.PostCommand;
import com.kenstevens.stratinit.client.site.command.get.UnitsToSIUnits;
import com.kenstevens.stratinit.client.site.processor.UpdateProcessor;
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
public class MoveUnitsCommand extends PostCommand<SIUpdate, MoveUnitsJson> {
	@Autowired
	private UpdateProcessor updateProcessor;
	@Autowired
	private IAudioPlayer audioPlayer;

	public MoveUnitsCommand(List<UnitView> units, WorldSector targetSector) {
		super(new MoveUnitsJson(UnitsToSIUnits.transform(units), targetSector.getCoords()), buildDescription(units, targetSector));
	}

	@Override
	public Result<SIUpdate> executePost(MoveUnitsJson request) {
		Result<SIUpdate> retval = stratInitServer.moveUnits(request);
		if (!retval.isMoveSuccess()) {
			audioPlayer.playEmpty();
		}
		return retval;
	}

	public static String buildDescription(List<UnitView> units, WorldSector targetSector) {
		String what = "units";
		if (units.size() == 1 && units.get(0).isNavy()) {
			what = units.get(0).getType().toString().toLowerCase();
		}
		Attack attack = new Attack(targetSector);
		SectorCoords targetCoords = targetSector.getCoords();
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

    public SectorCoords getTarget() {
		return getRequest().target;
    }

	public boolean containsUnitId(Integer unitId) {
		return getRequest().units.stream().anyMatch(u -> unitId.equals(u.id));
	}
}
