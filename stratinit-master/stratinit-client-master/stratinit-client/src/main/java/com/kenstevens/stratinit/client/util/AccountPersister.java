package com.kenstevens.stratinit.client.util;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.kenstevens.stratinit.client.main.ClientConstants;
import com.kenstevens.stratinit.client.model.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.spec.KeySpec;
import java.util.Base64;

@Component
public class AccountPersister extends JsonPersister {
    private static final String ALGORITHM = "AES";
    private static final byte[] SALT = {0x53, 0x74, 0x72, 0x61, 0x74, 0x49, 0x6e, 0x69}; // "StratIni"

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final SecretKey secretKey;
    @Autowired
    private Account account;

    public AccountPersister() {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(ClientConstants.KEY.toCharArray(), SALT, 65536, 128);
            SecretKey tmp = factory.generateSecret(spec);
            secretKey = new SecretKeySpec(tmp.getEncoded(), ALGORITHM);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Failed to initialize encryption key", e);
        }
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
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decoded = Base64.getDecoder().decode(encryptedPassword);
            return new String(cipher.doFinal(decoded));
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Failed to decrypt password", e);
        }
    }

    private String encryptPassword(String password) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(password.getBytes()));
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Failed to encrypt password", e);
        }
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
        String encryptedPassword = encryptPassword(account.getPassword());
        savedAccount.setPassword(encryptedPassword);
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        writer.writeValue(result, savedAccount);
    }
}
