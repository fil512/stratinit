package com.kenstevens.stratinit.balance;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

public class BalanceResultPersisterTest {
	@Test
	public void load() throws IOException, ParserConfigurationException,
			SAXException {
		BalanceResultPersister balanceResultPersister = new BalanceResultPersister();
		balanceResultPersister.load();
	}
}
