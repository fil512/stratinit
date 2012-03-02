package com.kenstevens.stratinit.site.processor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.dto.SIRelation;
import com.kenstevens.stratinit.model.NationView;

@Service
public class RelationListProcessor extends Processor {
	@Autowired
	private RelationProcessor relationProcessor;
	
	public void process(List<SIRelation> entries) {
		NationView nation = db.getNation();
		for (SIRelation sirelation : entries) {
			relationProcessor.process(nation, sirelation);
		}
	}
}
