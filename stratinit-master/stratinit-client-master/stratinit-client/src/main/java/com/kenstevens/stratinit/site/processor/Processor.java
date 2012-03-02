package com.kenstevens.stratinit.site.processor;

import org.springframework.beans.factory.annotation.Autowired;

import com.kenstevens.stratinit.event.ArrivedDataEventAccumulator;
import com.kenstevens.stratinit.model.Data;

public class Processor {
	@Autowired
	protected Data db;
	@Autowired
	protected ArrivedDataEventAccumulator arrivedDataEventAccumulator;
}
