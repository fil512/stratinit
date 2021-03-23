package com.kenstevens.stratinit.client.site.translator;

import com.kenstevens.stratinit.client.model.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public abstract class ListTranslator<I,O> {
	@Autowired
	protected Data db;

	public List<O> translate(List<I> inputList) {
		List<O> outputList = new ArrayList<O>();
		for (I input : inputList) {
			O output = translate(input);
			outputList.add(output);
		}
		return outputList;
	}

	public abstract O translate(I input);
}
