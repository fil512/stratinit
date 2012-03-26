package com.kenstevens.stratinit.server.remote.request.write;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dto.SIRelation;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Relation;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.RelationType;

@Scope("prototype")
@Component
public class SetRelationRequest extends PlayerWriteRequest<SIRelation> {
	@Autowired
	private GameDao gameDao;

	private final int nationId;
	private final RelationType relationType;

	public SetRelationRequest(int nationId, RelationType relationType) {
		this.nationId = nationId;
		this.relationType = relationType;
	}

	@Override
	protected Result<SIRelation> executeWrite() {
		Nation nation = getNation();
		Nation target = gameDao.getNation(nation.getGameId(), nationId);
		if (nation.equals(target)) {
			return new Result<SIRelation>(
					"You may not change relations with yourself", false);
		}
		if (relationType == RelationType.ME) {
			return new Result<SIRelation>(
					"You may not change relations to the ME status", false);
		}
		if (relationType == RelationType.ALLIED) {
			if (!nation.getGame().hasStarted()) {
				return new Result<SIRelation>(
						"Alliances are not allowed in this game yet.  Alliances will be permitted once the game has started.", false);
			} else if (nation.getGame().isNoAlliances()) {
				return new Result<SIRelation>(
						"Alliances are not allowed in this game.", false);
			}
		}
		Result<Relation> result = gameDaoService.setRelation(nation, target,
				relationType, false);
		// TODO REF
		Map<Nation, Relation> map = gameDao.getTheirRelationsAsMap(nation);
		Relation themToMe = map.get(target);
		return new Result<SIRelation>(result.getMessages(), result.isSuccess(),
				new SIRelation(result.getValue(), themToMe));
	}

	@Override
	protected int getCommandCost() {
		return Constants.COMMAND_COST;
	}
}
