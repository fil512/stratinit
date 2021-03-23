package com.kenstevens.stratinit.client.util;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.kenstevens.stratinit.client.main.ClientConstants;
import com.kenstevens.stratinit.client.model.Account;
import org.jasypt.util.text.BasicTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class AccountPersister extends JsonPersister {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
    @Autowired
    private Account account;

    public AccountPersister() {
        textEncryptor.setPassword(ClientConstants.KEY);
    }

    public String load() {
        String filename = System.getProperty("user.home") + "/" + ClientConstants.ACCOUNT_FILENAME;
        logger.info("Loading config file {}", filename);
        return load(filename);
    }

    public void save() throws IOException {
        save(System.getProperty("user.home") + "/" + ClientConstants.ACCOUNT_FILENAME);
    }

    public String decryptPassword(String encryptedPassword) {
        return textEncryptor.decrypt(encryptedPassword);
    }

    protected String deserialize(File source) throws IOException {
        Account loadedAccount;
        ObjectMapper mapper = new ObjectMapper();
        loadedAccount = mapper.readValue(source, Account.class);
        String password = decryptPassword(loadedAccount.getPassword());
        loadedAccount.setPassword(password);
        account.load(loadedAccount);
        return "";
    }

    protected void serialize(File result) throws IOException {
        Account savedAccount = new Account();
        savedAccount.load(account);
        String encryptedPassword = textEncryptor.encrypt(account.getPassword());
        savedAccount.setPassword(encryptedPassword);
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        writer.writeValue(result, savedAccount);
    }
}
