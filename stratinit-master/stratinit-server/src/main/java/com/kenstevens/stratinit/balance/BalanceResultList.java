package com.kenstevens.stratinit.balance;

import java.util.ArrayList;
import java.util.List;

public class BalanceResultList {
    List<BalanceResult> results = new ArrayList<>();

    public BalanceResultList() {
    }

    public void add(BalanceResult balanceResult) {
        results.add(balanceResult);
    }

    public List<BalanceResult> getBalanceResultList() {
        return results;
    }
}
