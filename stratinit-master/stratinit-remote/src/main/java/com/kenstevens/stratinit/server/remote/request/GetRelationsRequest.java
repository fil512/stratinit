package com.kenstevens.stratinit.server.remote.request;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dto.SIRelation;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Relation;

@Scope("prototype")
@Component
public class GetRelationsRequest extends PlayerRequest<List<SIRelation>> {
	@Autowired
	private GameDao gameDao;

	@Override
	protected List<SIRelation> execute() {
		Nation nation = getNation();
		Collection<Relation> relations = gameDao.getMyRelations(nation);
		Map<Nation, Relation> map = gameDao.getTheirRelationsAsMap(nation);
		List<SIRelation> retval = new ArrayList<SIRelation>();
		for (Relation relation : relations) {
			// TODO REF
			Relation themToMe = map.get(relation.getTo());
			retval.add(new SIRelation(relation, themToMe));
		}
		return retval;
	}
}
