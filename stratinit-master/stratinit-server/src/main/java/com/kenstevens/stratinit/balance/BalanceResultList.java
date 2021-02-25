package com.kenstevens.stratinit.balance;

import com.google.common.collect.Lists;

import java.util.List;

public class BalanceResultList {
    List<BalanceResult> results = Lists.newArrayList();

    public BalanceResultList() {
    }

    public void add(BalanceResult balanceResult) {
        results.add(balanceResult);
    }

    public List<BalanceResult> getBalanceResultList() {
        return results;
    }
}
