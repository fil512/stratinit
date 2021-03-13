package com.kenstevens.stratinit.balance;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@Disabled
public class ManualBalanceResultPersisterTest {
    @Test
    public void load() throws IOException {
        BalanceResultPersister balanceResultPersister = new BalanceResultPersister();
        balanceResultPersister.load();
    }
}
