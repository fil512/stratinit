package com.kenstevens.stratinit.util;

import java.io.File;

import org.jasypt.util.text.BasicTextEncryptor;
import org.simpleframework.xml.Serializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import com.kenstevens.stratinit.main.ClientConstants;
import com.kenstevens.stratinit.model.Account;

@Component
public class AccountPersister extends XMLPersister {
	@Autowired
	private Account account;

	private BasicTextEncryptor textEncryptor = new BasicTextEncryptor();

	public String load() {
		return load(System.getProperty("user.home")+"/"+ClientConstants.ACCOUNT_FILENAME);
	}

	public void save() throws XMLException {
		save(System.getProperty("user.home")+"/"+ClientConstants.ACCOUNT_FILENAME);
	}

	public AccountPersister() {
		textEncryptor.setPassword(ClientConstants.KEY);
	}

	public String decryptPassword(String encryptedPassword) {
		return textEncryptor.decrypt(encryptedPassword);
	}

	protected String deserialize(Serializer serializer,
			File source, Document doc) throws XMLException {
		Account loadedAccount;
		try {
			loadedAccount = serializer.read(Account.class, source);
		} catch (Exception e) {
			throw new XMLException(e);
		}
		String password = decryptPassword(loadedAccount.getPassword());
		loadedAccount.setPassword(password);
		account.load(loadedAccount);
		return "";
	}

	protected void serialize(Serializer serializer, File result) throws XMLException {
		Account savedAccount = new Account();
		savedAccount.load(account);
		String encryptedPassword = textEncryptor.encrypt(account.getPassword());
		savedAccount.setPassword(encryptedPassword);
		try {
			serializer.write(savedAccount, result);
		} catch (Exception e) {
			throw new XMLException(e);
		}
	}
}
