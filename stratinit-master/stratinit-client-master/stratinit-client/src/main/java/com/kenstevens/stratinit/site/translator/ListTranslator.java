package com.kenstevens.stratinit.site.translator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.kenstevens.stratinit.model.Data;

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
