package com.kenstevens.stratinit.site.processor;

import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.dto.SIRelation;
import com.kenstevens.stratinit.model.NationView;
import com.kenstevens.stratinit.model.Relation;

@Service
public class RelationProcessor extends Processor {

	public void process(NationView nation, SIRelation sirelation) {
		NationView themNation = db.getNation(sirelation.nationId);
		Relation myRelation = new Relation(nation, themNation);
		myRelation.setType(sirelation.meToThem);
		myRelation.setSwitchTime(sirelation.mineSwitches);
		myRelation.setNextType(sirelation.myNextType);
		themNation.setMyRelation(myRelation);
		Relation theirRelation = new Relation(themNation, nation);
		theirRelation.setType(sirelation.themToMe);
		theirRelation.setSwitchTime(sirelation.theirsSwitches);
		theirRelation.setNextType(sirelation.theirNextType);
		themNation.setTheirRelation(theirRelation);
	}
}
