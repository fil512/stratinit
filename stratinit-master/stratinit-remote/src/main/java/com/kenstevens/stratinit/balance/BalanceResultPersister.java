package com.kenstevens.stratinit.balance;

import com.kenstevens.stratinit.type.UnitType;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class BalanceResultPersister {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	static final String SAVE_NAME = "unitBalancer.xml";
	static final String SAVE_FILE = "src/main/resources/com/kenstevens/stratinit/balance/" + SAVE_NAME;

	public void save(BalanceResultList balanceResultList) throws IOException {
		Serializer serializer = new Persister();
		File result = new File(SAVE_FILE);
		serialize(serializer, result, balanceResultList);
	}

	protected void serialize(Serializer serializer, File result,
			BalanceResultList balanceResultList) throws IOException {
		try {
			serializer.write(balanceResultList, result);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	public BalanceResultList load() throws IOException, ParserConfigurationException, SAXException  {
		BalanceResultList retval = null;
		Serializer serializer = new Persister();
		InputStream source = this.getClass().getResourceAsStream(SAVE_NAME);
		if (source != null) {
			try {
				retval = deserialize(serializer, source);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				throw e;
			}
			
		}
		if (retval == null) {
			retval = new BalanceResultList();
			BalanceResult error = new BalanceResult();
			error.attackerType = UnitType.BASE;
			retval.add(error);
		}
		return retval;
	}

	private BalanceResultList deserialize(Serializer serializer,
			InputStream source) throws IOException {
		BalanceResultList balanceResultList;
		try {
			balanceResultList = serializer
					.read(BalanceResultList.class, source);
		} catch (Exception e) {
			throw new IOException(e);
		}
		return balanceResultList;
	}
}
