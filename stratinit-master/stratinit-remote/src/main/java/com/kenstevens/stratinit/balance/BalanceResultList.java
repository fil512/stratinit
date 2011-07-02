package com.kenstevens.stratinit.balance;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import com.google.common.collect.Lists;

@Root
public class BalanceResultList {
	@ElementList
	List<BalanceResult> results = Lists.newArrayList();

	public BalanceResultList() {}
	
	public void add(BalanceResult balanceResult) {
		results.add(balanceResult);
	}

	public List<BalanceResult> getBalanceResultList() {
		return results;
	}
}
