package com.kenstevens.stratinit.util;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.kenstevens.stratinit.main.ClientConstants;


public abstract class XMLPersister {
	private final Log logger = LogFactory.getLog(getClass());

	public String load(String filename) {
		String retval = "";
		try {
			createDataDirIfNoExists();
			Serializer serializer = new Persister();
			File source = new File(filename);
			if (source.exists()) {
				DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
								.newInstance();
						DocumentBuilder docBuilder;
						docBuilder = docBuilderFactory.newDocumentBuilder();
						Document doc = docBuilder.parse(source);
						retval = deserialize(serializer, source, doc);
			} else {
				retval = "First time running.  Not loading save file.";
			}
		} catch (ParserConfigurationException e) {
			logger.error(e.getMessage(), e);
			retval = "Save file is corrupt.  Not loading Save file.";
		} catch (SAXException e) {
			logger.error(e.getMessage(), e);
			retval = "Save file is corrupt.  Not loading Save file.";
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			retval = "Unable to read save file.  Not loading Save file.";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			retval = "Save file is corrupt.  Not loading Save file.";
		}
		return retval;
	}

	protected abstract String deserialize(Serializer serializer, File source, Document doc) throws XMLException;

	protected abstract void serialize(Serializer serializer, File result) throws XMLException;

	public void save(String filename) throws XMLException {
		createDataDirIfNoExists();
		Serializer serializer = new Persister();
		File result = new File(filename);
		serialize(serializer, result);
	}

	private void createDataDirIfNoExists() {
		createDirIfNoExists(System.getProperty("user.home")+"/"+ClientConstants.DATA_DIR);
	}

	private void createDirIfNoExists(String dirName) {
		File dataDir = new File(dirName);
		if (!dataDir.canRead()) {
			dataDir.mkdir();
		}
	}
}
