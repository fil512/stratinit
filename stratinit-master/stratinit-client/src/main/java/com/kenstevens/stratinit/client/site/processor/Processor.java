package com.kenstevens.stratinit.client.site.processor;

import com.kenstevens.stratinit.client.event.ArrivedDataEventAccumulator;
import com.kenstevens.stratinit.client.model.Data;
import org.springframework.beans.factory.annotation.Autowired;

public class Processor {
	@Autowired
	protected Data db;
	@Autowired
	protected ArrivedDataEventAccumulator arrivedDataEventAccumulator;
}
