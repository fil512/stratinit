package com.kenstevens.stratinit.client.site.processor;

import com.kenstevens.stratinit.client.model.NationView;
import com.kenstevens.stratinit.dto.SIRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
