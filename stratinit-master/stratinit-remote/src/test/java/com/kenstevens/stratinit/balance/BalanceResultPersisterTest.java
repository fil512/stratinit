package com.kenstevens.stratinit.balance;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

@Disabled
public class BalanceResultPersisterTest {
	@Test
	public void load() throws IOException, ParserConfigurationException,
			SAXException {
		BalanceResultPersister balanceResultPersister = new BalanceResultPersister();
		balanceResultPersister.load();
	}
}
