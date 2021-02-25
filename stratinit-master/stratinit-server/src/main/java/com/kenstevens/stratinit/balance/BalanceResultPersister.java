package com.kenstevens.stratinit.balance;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.kenstevens.stratinit.type.UnitType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class BalanceResultPersister {
    static final String SAVE_NAME = "unitBalancer.json";
    static final String SAVE_FILE = "src/main/resources/com/kenstevens/stratinit/balance/" + SAVE_NAME;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public void save(BalanceResultList balanceResultList) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File result = new File(SAVE_FILE);
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        writer.writeValue(result, balanceResultList);
    }

    public BalanceResultList load() throws IOException {
        BalanceResultList retval = null;
        ObjectMapper mapper = new ObjectMapper();
        InputStream source = this.getClass().getResourceAsStream(SAVE_NAME);
        if (source != null) {
            try {
                retval = mapper.readValue(source, BalanceResultList.class);
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
}
